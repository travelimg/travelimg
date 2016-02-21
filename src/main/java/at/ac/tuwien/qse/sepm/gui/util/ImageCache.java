package at.ac.tuwien.qse.sepm.gui.util;


/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.nio.file.Path;

/**
 * Cache for images. If the image is not present it is loaded from disk and put into the cache.
 */
public class ImageCache {

    private static final Logger LOGGER = LogManager.getLogger();

    private LRUCache<Path, Image> smallCache = new LRUCache<>(500);
    private LRUCache<Path, Image> mediumCache = new LRUCache<>(300);
    private LRUCache<Path, Image> largeCache = new LRUCache<>(50);
    private LRUCache<Path, Image> originalCache = new LRUCache<>(5);

    /**
     * Get an image for a photo at the given size.
     *
     * If it is not already in the cache then load it from disk.
     *
     * @param path The photo path for which to retrieve the image file.
     * @param size  The size for which the photo should be loaded.
     * @return The image at the given size.
     */
    public Image get(Path path, ImageSize size) {
        LRUCache<Path, Image> cache = getCacheForSize(size);

        if (!cache.containsKey(path)) {
            load(path, size);
        }

        return cache.get(path);
    }

    /**
     * Load an image from disk into the cache at the given size.
     *
     * @param path The image path to load.
     * @param size  The size of the resulting image
     */
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
