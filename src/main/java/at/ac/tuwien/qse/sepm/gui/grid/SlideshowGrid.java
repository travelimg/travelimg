package at.ac.tuwien.qse.sepm.gui.grid;


import at.ac.tuwien.qse.sepm.gui.util.ImageCache;

public class SlideshowGrid extends ImageGrid {

    public SlideshowGrid(ImageCache imageCache) {
        super(imageCache, PhotoGridTile::new);
    }
}
