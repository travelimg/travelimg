package at.ac.tuwien.qse.sepm.gui.grid;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ImageGridPage extends ImageGrid {

    private static final Logger LOGGER = LogManager.getLogger();

    public ImageGridPage(List<Photo> photos, ImageCache imageCache) {
        super(imageCache);

        setPhotos(photos);
    }

    /**
     * Return the first photo in the grid.
     *
     * @return the first photo in the grid or null if the page is empty
     */
    public Photo getActivePhoto() {
        if (photos.isEmpty())
            return null;

        return photos.get(0);
    }

    /**
     * Select the n'th photo (index) in the grid.
     *
     * @param index Specify which photo to select.
     */
    public void selectAt(int index) {
        if (tiles.isEmpty())
            return;

        PhotoGridTile tile = tiles.get(Math.max(Math.min(tiles.size() - 1, index), 0));
        tile.select();
        onSelectionChange();
    }

    /**
     * Return the index of the first selected tile.
     *
     * @return the index of the first selected tile or -1 if none is selected.
     */
    public int getFirstSelectedIndex() {
        PhotoGridTile selected = tiles.stream()
                .filter(ImageGridTile::isSelected)
                .findFirst()
                .orElse(null);

        if (selected == null) {
            return -1;
        } else {
            return tiles.indexOf(selected);
        }
    }
}
