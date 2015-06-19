package at.ac.tuwien.qse.sepm.gui.grid;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SlideshowGrid extends ImageGrid<SlideGridTile> {

    private Slideshow slideshow;
    private Consumer<Slide> slideChangedCallback = null;

    public SlideshowGrid(ImageCache imageCache) {
        super(imageCache, SlideGridTile::new);
    }

    public void setSlideshow(Slideshow slideshow) {
        this.slideshow = slideshow;

        clear();
        List<Photo> photos = slideshow.getSlides().stream()
                .filter(s -> s instanceof PhotoSlide) // TODO: remove
                .map(s -> (PhotoSlide)s) // TODO: remove
                .map(PhotoSlide::getPhoto)
                .collect(Collectors.toList());

        setPhotos(photos);
    }

    public void setSlideChangedCallback(Consumer<Slide> slideChangedCallback) {
        this.slideChangedCallback = slideChangedCallback;
    }

    @Override
    protected void onTileAdded(SlideGridTile tile) {
        tile.onLeftClicked(this::moveTileForward);
        tile.onRightClicked(this::moveTileBackward);
    }

    private void moveTileForward(SlideGridTile tile) {
        int thisIndex = tiles.indexOf(tile);
        int prevIndex = thisIndex - 1;

        if (prevIndex < 0) {
            return;
        }

        swap(prevIndex, thisIndex);
    }

    private void moveTileBackward(SlideGridTile tile) {
        int thisIndex = tiles.indexOf(tile);
        int nextIndex = thisIndex + 1;

        if (nextIndex >= tiles.size()) {
            return;
        }

        swap(thisIndex, nextIndex);
    }

    private void swap(int i1, int i2) {
        SlideGridTile t1 = tiles.get(i1);
        SlideGridTile t2 = tiles.get(i2);

        // swap in gui
        tiles.set(i1, t2);
        tiles.set(i2, t1);

        getChildren().remove(i2);
        getChildren().remove(i1);

        getChildren().add(i1, t1);
        getChildren().add(i1, t2);

        // update slides
        Slide s1 = slideshow.getSlides().get(i1);
        Slide s2 = slideshow.getSlides().get(i2);

        s1.setOrder(i2);
        s2.setOrder(i1);

        handleSlideChange(s1);
        handleSlideChange(s2);
    }

    private void handleSlideChange(Slide slide) {
        if (slideChangedCallback != null) {
            slideChangedCallback.accept(slide);
        }
    }
}
