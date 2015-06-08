package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Photo repository that stores photo files in memory.
 */
public class MemoryPhotoRepository implements PhotoRepository {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Path basePath;
    private final Map<Path, Object> files = new HashMap<>();
    private final Map<Path, LocalDateTime> modified = new HashMap<>();
    private final Collection<Listener> listeners = new LinkedList<>();

    public MemoryPhotoRepository(Path basePath) {
        if (basePath == null) throw new IllegalArgumentException();
        this.basePath = basePath;
    }

    public Path getBasePath() {
        return basePath;
    }

    @Override public boolean accepts(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("accepting {}", file);
        boolean result = file.startsWith(getBasePath());
        LOGGER.info("accepts {} is {}", file, result);
        return result;
    }

    @Override public void create(Path file, InputStream source) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        if (source == null) throw new IllegalArgumentException();
        LOGGER.debug("creating {}", file);

        if (!file.startsWith(getBasePath())) {
            throw new PersistenceException("File is not accepted by this repository.");
        }
        if (contains(file)) {
            throw new PhotoAlreadyExistsException(this, file);
        }

        try {
            byte[] array = IOUtils.toByteArray(source);
            files.put(file, array);
            modified.put(file, LocalDateTime.now());
            LOGGER.info("created {}", file);
            listeners.forEach(l -> l.onCreate(this, file));
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override public void update(Photo photo) throws PersistenceException {
        if (photo == null) throw new IllegalArgumentException();
        LOGGER.debug("updating {}", photo);

        Path file = photo.getFile();
        if (!contains(file)) {
            throw new PhotoNotFoundException(this, file);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            update(photo, stream);
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
        files.put(file, stream.toByteArray());
        modified.put(file, LocalDateTime.now());
        LOGGER.info("updated {}", photo);
        listeners.forEach(l -> l.onUpdate(this, file));
    }

    @Override public void delete(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("deleting {}", file);

        if (!contains(file)) {
            throw new PhotoNotFoundException(this, file);
        }

        files.remove(file);
        modified.remove(file);
        LOGGER.info("deleted {}", file);
        listeners.forEach(l -> l.onDelete(this, file));
    }

    @Override public void addListener(Listener listener) {
        if (listener == null) throw new IllegalArgumentException();
        listeners.add(listener);
        LOGGER.info("added listener {}", listener);
    }

    @Override public void removeListener(Listener listener) {
        if (listener == null) throw new IllegalArgumentException();
        listeners.remove(listener);
        LOGGER.info("removed listener {}", listener);
    }

    @Override public Collection<Path> index() throws PersistenceException {
        LOGGER.debug("indexing");
        Collection<Path> result = files.keySet();
        LOGGER.info("indexed {}", result.size());
        return result;
    }

    @Override public boolean contains(Path file) throws PersistenceException {
        return files.containsKey(file);
    }

    @Override public Photo read(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("reading {}", file);

        if (!contains(file)) {
            throw new PhotoNotFoundException(this, file);
        }

        InputStream stream = new ByteArrayInputStream((byte[])files.get(file));
        try {
            Photo photo = read(file, stream);
            LOGGER.info("read {}", photo);
            return photo;
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }

    protected Photo read(Path file, InputStream stream) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void update(Photo photo, OutputStream stream) throws IOException {
        throw new UnsupportedOperationException();
    }
}
