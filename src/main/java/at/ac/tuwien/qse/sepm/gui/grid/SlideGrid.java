package at.ac.tuwien.qse.sepm.gui.grid;


import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.TitleSlide;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import at.ac.tuwien.qse.sepm.gui.dialogs.ResultDialog;
import at.ac.tuwien.qse.sepm.service.SlideshowService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SlideGrid extends TilePane {

    private static final Logger LOGGER = LogManager.getLogger();

    private final List<SlideGridNode> nodes = new LinkedList<>();
    private List<Slide> slides = new LinkedList<>();

    private Consumer<Slide> slideChangedCallback = null;
    private BiConsumer<Slide, Integer> slideAddedCallback = null;

    public SlideGrid() {

        /*addSlide(new TitleSlide(1, 1, 1, "Indien 2015", 0));
        addSlide(new PhotoSlide(2, 1, 2, "1. November - Bombai Test eines sehr langen textes", photos[0]));
        addSlide(new PhotoSlide(3, 1, 3, null, photos[1]));
        addSlide(new PhotoSlide(4, 1, 4, null, photos[2]));
        addSlide(new PhotoSlide(5, 1, 5, null, photos[3]));
        addSlide(new MapSlide(6, 1, 6, null, 10.0, 15.0));
        addSlide(new PhotoSlide(7, 1, 7, "4. November - Elefanten", photos[4]));
        addSlide(new PhotoSlide(8, 1, 8, null, photos[5]));
        addSlide(new PhotoSlide(9, 1, 9, null, photos[6]));
        addSlide(new MapSlide(10, 1, 10, "Indien 2015", 10.0, 15.0));
        addSlide(new PhotoSlide(11, 1, 11, null, photos[7]));
        addSlide(new PhotoSlide(12, 1, 12, null, photos[8]));
        addSlide(new TitleSlide(14, 1, 13, null, 0));*/

        getStyleClass().add("slide-grid");
        getChildren().addAll(nodes);
        setAlignment(Pos.TOP_CENTER);
    }

    public void addSlide(Slide slide) {
        SlideTile tile;
        if (slide instanceof PhotoSlide) {
            tile = new PhotoSlideTile((PhotoSlide)slide);
        } else if (slide instanceof MapSlide) {
            tile = new MapSlideTile((MapSlide)slide);
        } else if (slide instanceof TitleSlide) {
            tile = new TitleSlideTile((TitleSlide)slide);
        } else {
            throw new RuntimeException("Unknow slide type.");
        }
        tile.setOnMouseClicked(event -> handleTileClicked(tile, event));
        SlideGridNode node = new SlideGridNode(tile);
        node.setSlidePositionChangeCallback(this::handleSlidePositionChange);
        node.setSlideAddedCallback(this::handleSlideAdded);
        nodes.add(node);
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

    public void deselectAll() {
        LOGGER.debug("deselecting all items");
        nodes.forEach(n -> n.getTile().deselect());
    }

    public void setSlideAddedCallback(BiConsumer<Slide, Integer> slideAddedCallback) {
        this.slideAddedCallback = slideAddedCallback;
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

    private void handleSlideAdded(SlideGridNode successor) {
        LOGGER.debug("Added slide before {}", successor.getTile().getSlide().getId());

        int index = nodes.indexOf(successor);

        SlideTypeDialog dialog = new SlideTypeDialog(this);
        Optional<SlideType> type = dialog.showForResult();

        if (type.isPresent()) {
            if (type.get() == SlideType.MAP) {
                MapSlide slide = new MapSlide(-1, 0, 0, "caption", 0, 0);
                slideAddedCallback.accept(slide, index);
            } else {
                TitleSlide slide = new TitleSlide(-1, 0, 0, "caption", 0);
                slideAddedCallback.accept(slide, index);
            }
        }
    }

    private enum SlideType {
        MAP,
        TITLE
    }

    private static class SlideTypeDialog extends ResultDialog<SlideType> {
        @FXML
        private Button mapButton;
        @FXML
        private Button titleButton;

        public SlideTypeDialog(Node origin) {
            super(origin, "Bitte Folientyp wählen");
            FXMLLoadHelper.load(this, this, SlideTypeDialog.class, "view/SlideTypeDialog.fxml");

            mapButton.setOnAction((event) -> {
                setResult(SlideType.MAP);
                close();
            });

            titleButton.setOnAction((event) -> {
                setResult(SlideType.TITLE);
                close();
            });
        }
    }

    private void handleTileClicked(SlideTile tile, MouseEvent event) {
        if (event.isControlDown()) {
            if (tile.isSelected()) {
                tile.deselect();
            } else {
                tile.select();
            }
        } else {
            deselectAll();
            tile.select();
        }
    }
}
