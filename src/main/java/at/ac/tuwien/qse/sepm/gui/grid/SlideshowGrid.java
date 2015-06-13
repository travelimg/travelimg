package at.ac.tuwien.qse.sepm.gui.grid;


import at.ac.tuwien.qse.sepm.gui.util.ImageCache;

public class SlideshowGrid extends ImageGrid<SlideGridTile> {

    public SlideshowGrid(ImageCache imageCache) {
        super(imageCache, SlideGridTile::new);
    }


    @Override
    protected void onTileAdded(SlideGridTile tile) {
        
    }
}
