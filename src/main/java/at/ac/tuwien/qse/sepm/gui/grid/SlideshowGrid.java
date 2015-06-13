package at.ac.tuwien.qse.sepm.gui.grid;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;

import java.util.List;
import java.util.stream.Collectors;

public class SlideshowGrid extends ImageGrid<SlideGridTile> {

    public SlideshowGrid(ImageCache imageCache) {
        super(imageCache, SlideGridTile::new);
    }

    public void setSlideshow(Slideshow slideshow) {
        clear();
        List<Photo> photos = slideshow.getSlides().stream()
                .map(Slide::getPhoto)
                .collect(Collectors.toList());

        setPhotos(photos);
    }

    @Override
    protected void onTileAdded(SlideGridTile tile) {
        tile.onLeftClicked(this::moveTileForward);
        tile.onRightClicked(this::moveTileBackward);
    }

    private void moveTileForward(SlideGridTile tile) {
        int prevIndex = tiles.indexOf(tile) - 1;
        if (prevIndex < 0) {
            return;
        }

        SlideGridTile previous = tiles.get(prevIndex);
        swapTiles(previous, tile);

        select(tile);
    }

    private void moveTileBackward(SlideGridTile tile) {
        int nextIndex = tiles.indexOf(tile) + 1;
        if (nextIndex >= tiles.size()) {
            return;
        }

        SlideGridTile next = tiles.get(nextIndex);
        swapTiles(tile, next);

        select(tile);
    }

    private void swapTiles(SlideGridTile t1, SlideGridTile t2) {
        int i1 = tiles.indexOf(t1);
        int i2 = tiles.indexOf(t2);

        if (i1 > i2) {
            int temp = i1;
            i1 = i2;
            i2 = temp;

            SlideGridTile tmp = t1;
            t1 = t2;
            t2 = tmp;
        }

        tiles.set(i1, t2);
        tiles.set(i2, t1);

        getChildren().remove(i2);
        getChildren().remove(i1);

        getChildren().add(i1, t1);
        getChildren().add(i1, t2);
    }
}
