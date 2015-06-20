package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import javafx.scene.layout.TilePane;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class SlideGrid extends TilePane {

    private List<Slide> slides = new LinkedList<>();
    private Consumer<Slide> slideChangedCallback = null;

    private final List<SlideGridNode> nodes = new LinkedList<>();

    public SlideGrid() {
        setVgap(10);
        getChildren().addAll(
                new SlideGridNode(new SlideTile()),
                new SlideGridNode(new SlideTile()),
                new SlideGridNode(new SlideTile()),
                new SlideGridNode(new SlideTile()),
                new SlideGridNode(new SlideTile()),
                new SlideGridNode(new SlideTile()),
                new SlideGridNode(new SlideTile()),
                new SlideGridNode(new SlideTile()),
                new SlideGridNode(new SlideTile()),
                new SlideGridNode(new SlideTile()),
                new SlideGridNode(new SlideTile()),
                new SlideGridNode(new SlideTile())
        );
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

    protected void onTileAdded(SlideTileBase tile) {

    }

    private void handleSlideChange(Slide slide) {
        if (slideChangedCallback != null) {
            slideChangedCallback.accept(slide);
        }
    }
}
