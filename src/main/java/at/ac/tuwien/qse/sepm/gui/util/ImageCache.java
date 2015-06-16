package at.ac.tuwien.qse.sepm.gui.util;

import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.scene.image.Image;

/**
 * Cache for images. If the image is not present it is loaded from disk and put into the cache.
 *
 * It is up to the implemtation how many images are cached and which replacement strategy they use.
 */
public interface ImageCache {

    /**
     * Get an image for a photo at the given size.
     *
     * If it is not already in the cache then load it from disk.
     *
     * @param photo The photo for which to retrieve the image file.
     * @param size  The size for which the photo should be loaded.
     * @return The image at the given size.
     */
    Image get(Photo photo, ImageSize size);

    /**
     * Load an image from disk into the cache at the given size.
     *
     * @param photo The image to load.
     * @param size  The size of the resulting image
     */
    void load(Photo photo, ImageSize size);
}
