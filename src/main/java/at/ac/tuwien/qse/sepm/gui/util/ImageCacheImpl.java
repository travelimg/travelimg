package at.ac.tuwien.qse.sepm.gui.util;


import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.nio.file.Path;

public class ImageCacheImpl implements ImageCache {

    private static final Logger LOGGER = LogManager.getLogger();

    private LRUCache<Path, Image> smallCache = new LRUCache<>(500);
    private LRUCache<Path, Image> mediumCache = new LRUCache<>(300);
    private LRUCache<Path, Image> largeCache = new LRUCache<>(50);
    private LRUCache<Path, Image> originalCache = new LRUCache<>(5);

    @Override
    public Image get(Path path, ImageSize size) {
        LRUCache<Path, Image> cache = getCacheForSize(size);

        if (!cache.containsKey(path)) {
            load(path, size);
        }

        return cache.get(path);
    }

    @Override
    public void load(Path path, ImageSize size) {
        String url;

        try {
            url = path.toUri().toURL().toString();
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
            width = height = size.pixels();
            image = new Image(url, width, height, false, false, true);
        }

        // put image in cache
        getCacheForSize(size).put(path, image);
    }

    private LRUCache<Path, Image> getCacheForSize(ImageSize size) {
        if (size == ImageSize.SMALL) return smallCache;
        if (size == ImageSize.MEDIUM) return mediumCache;
        if (size == ImageSize.LARGE) return largeCache;
        return originalCache;
    }
}
