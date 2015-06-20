package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.Slide;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SlideGrid extends TilePane {

    private static final Logger LOGGER = LogManager.getLogger();
    private final List<SlideGridNode> nodes = new LinkedList<>();
    private List<Slide> slides = new LinkedList<>();
    private Consumer<Slide> slideChangedCallback = null;

    public SlideGrid() {
        setVgap(10);

        nodes.add(new SlideGridNode(new SlideTile(1)));
        nodes.add(new SlideGridNode(new SlideTile(2)));
        nodes.add(new SlideGridNode(new SlideTile(3)));
        nodes.add(new SlideGridNode(new SlideTile(4)));
        nodes.add(new SlideGridNode(new SlideTile(5)));
        nodes.add(new SlideGridNode(new SlideTile(6)));
        nodes.add(new SlideGridNode(new SlideTile(7)));
        nodes.add(new SlideGridNode(new SlideTile(8)));
        nodes.add(new SlideGridNode(new SlideTile(9)));
        nodes.add(new SlideGridNode(new SlideTile(10)));
        nodes.add(new SlideGridNode(new SlideTile(11)));
        nodes.add(new SlideGridNode(new SlideTile(12)));

        getChildren().addAll(nodes);

        nodes.forEach(n -> n.setSlidePositionChangeCallback(this::handleSlidePositionChange));
    }

    public List<Slide> getSlides() {
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }

    public void setSlideChangedCallback(Consumer<Slide> slideChangedCallback) {
        this.slideChangedCallback = slideChangedCallback;
    }

    private void handleSlideChange(Slide slide) {
        if (slideChangedCallback != null) {
            slideChangedCallback.accept(slide);
        }
    }

    private void handleSlidePositionChange(SlideTile target, Integer sourceId) {
        LOGGER.debug("{} dropped on {}", sourceId, target.getSlide().getId());

        Optional<SlideGridNode> targetNode = nodes.stream()
                .filter(n -> n.getTile().getSlide().getId().equals(target.getSlide().getId()))
                .findFirst();
        Optional<SlideGridNode> sourceNode = nodes.stream()
                .filter(n -> n.getTile().getSlide().getId().equals(sourceId))
                .findFirst();

        if (!targetNode.isPresent() || !sourceNode.isPresent()) {
            return;
        }

        // change the order of the nodes to reflect the drop operation by
        // constructing a new list and moving the source node just before the target node
        List<SlideGridNode> nodesNewOrder = new ArrayList<>();
        for (SlideGridNode node : nodes) {
            if (node == sourceNode.get()) {
                continue; // skip source node, inserted before target node
            }

            if (node == targetNode.get()) {
                // insert source node before target node
                nodesNewOrder.add(sourceNode.get());
                nodesNewOrder.add(targetNode.get());
            } else {
                nodesNewOrder.add(node);
            }
        }

        nodes.clear();
        nodes.addAll(nodesNewOrder);

        getChildren().clear();
        getChildren().addAll(nodes);

        // update all slides in order to persist the order change
        nodes.forEach(n -> handleSlideChange(n.getTile().getSlide()));
    }
}
