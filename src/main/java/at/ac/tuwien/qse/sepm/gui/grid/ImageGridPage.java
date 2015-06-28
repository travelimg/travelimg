package at.ac.tuwien.qse.sepm.gui.grid;


import at.ac.tuwien.qse.sepm.entities.Photo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ImageGridPage extends ImageGrid<PhotoGridTile> {

    private static final Logger LOGGER = LogManager.getLogger();

    public ImageGridPage(List<Photo> photos) {
        super(PhotoGridTile::new);

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
}
