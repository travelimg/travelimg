package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Photo cache that stores photo instances in memory.
 */
public class MemoryPhotoCache implements PhotoCache {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<Path, Photo> photos = new HashMap<>();
    private final Map<Path, LocalDateTime> modified = new HashMap<>();

    @Override public void put(Photo photo) throws PersistenceException {
        if (photo == null) throw new IllegalArgumentException();
        LOGGER.debug("putting {}", photo);
        photos.put(photo.getFile(), photo);
        modified.put(photo.getFile(), LocalDateTime.now());
        LOGGER.debug("put {}", photo);
    }

    @Override public void remove(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("removing {}", file);
        if (!contains(file)) {
            throw new PhotoNotFoundException(this, file);
        }
        photos.remove(file);
        modified.remove(file);
        LOGGER.info("removed {}", file);
    }

    @Override public Collection<Path> index() throws PersistenceException {
        LOGGER.debug("indexing");
        Collection<Path> result = photos.keySet();
        LOGGER.info("indexed {}", result.size());
        return result;
    }

    @Override public boolean contains(Path file) throws PersistenceException {
        return photos.containsKey(file);
    }

    @Override public Photo read(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("reading {}", file);
        if (!contains(file)) {
            throw new PhotoNotFoundException(this, file);
        }
        Photo result = new Photo(photos.get(file));
        LOGGER.info("read {}", result);
        return result;
    }
}
