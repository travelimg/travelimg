package at.ac.tuwien.qse.sepm.gui.util;


import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.nio.file.Paths;

public class ImageCacheImpl implements ImageCache {

    private static final Logger LOGGER = LogManager.getLogger();

    private LRUCache<Integer, Image> smallCache = new LRUCache<>(500);
    private LRUCache<Integer, Image> mediumCache = new LRUCache<>(300);
    private LRUCache<Integer, Image> originalCache = new LRUCache<>(5);

    @Override
    public Image get(Photo photo, ImageSize size) {
        LRUCache<Integer, Image> cache = getCacheForSize(size);

        if (!cache.containsKey(photo.getId())) {
            load(photo, size);
        }

        return cache.get(photo.getId());
    }

    @Override
    public void load(Photo photo, ImageSize size) {
        String url;

        try {
            url = Paths.get(photo.getPath()).toUri().toURL().toString();
        } catch (MalformedURLException ex) {
            LOGGER.error("Failed to convert photo path to URL", ex);
            return;
        }

        Image image;

        // load the image in the background at the right size
        if (size == ImageSize.ORIGINAL)
            image = new Image(url, true);
        else {
            int width, height;
            width = height = ImageSize.inPixels(size);
            image = new Image(url, width, height, false, false, true);
        }

        // put image in cache
        getCacheForSize(size).put(photo.getId(), image);
    }

    private LRUCache<Integer, Image> getCacheForSize(ImageSize size) {
        if (size == ImageSize.SMALL) return smallCache;
        if (size == ImageSize.MEDIUM) return mediumCache;
        return originalCache;
    }
}
