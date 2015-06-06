package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PersistenceException;
import at.ac.tuwien.qse.sepm.dao.repo.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoInfo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.springframework.cglib.core.Local;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Photo cache that stores photo instances in memory.
 */
public class MemoryPhotoCache extends PhotoCacheBase {

    private final Map<Path, Photo> photos = new HashMap<>();
    private final Map<Path, LocalDateTime> modified = new HashMap<>();

    public MemoryPhotoCache() {
        super(LogManager.getLogger());
    }

    @Override protected PhotoInfo checkImpl(Path file) throws PersistenceException {
        if (!photos.containsKey(file)) {
            return null;
        }

        LocalDateTime date = LocalDateTime.from(modified.get(file));
        return new PhotoInfo(file, date);
    }

    @Override protected Collection<PhotoInfo> checkAllImpl() throws PersistenceException {
        Collection<PhotoInfo> result = new ArrayList<>(photos.size());
        for (Path file : photos.keySet()) {
            result.add(check(file));
        }
        return result;
    }

    @Override protected Photo readImpl(Path file) throws PersistenceException {
        if (!photos.containsKey(file)) {
            throw new PhotoNotFoundException(this, file);
        }
        return new Photo(photos.get(file));
    }

    @Override protected Collection<Photo> readAllImpl() throws PersistenceException {
        Collection<Photo> result = new ArrayList<>(photos.size());
        for (Path file : photos.keySet()) {
            result.add(read(file));
        }
        return result;
    }

    @Override protected void putImpl(Photo photo) throws PersistenceException {
        photos.put(photo.getFile(), photo);
        modified.put(photo.getFile(), LocalDateTime.now());
    }

    @Override protected void removeImpl(Path file) throws PersistenceException {
        if (!photos.containsKey(file)) {
            throw new PhotoNotFoundException(this, file);
        }
        photos.remove(file);
        modified.remove(file);
    }
}
