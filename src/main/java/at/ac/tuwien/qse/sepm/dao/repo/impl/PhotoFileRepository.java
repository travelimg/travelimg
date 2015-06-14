package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.repo.*;
import at.ac.tuwien.qse.sepm.entities.Photo;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Photo repository that manages photos as files in a directory.
 */
public class PhotoFileRepository implements PhotoRepository {

    private static final Logger LOGGER = LogManager.getLogger();

    private final FileManager fileManager;
    private final FileWatcher watcher;
    private final PhotoSerializer serializer;

    private final FileListener listener = new FileListener();
    private final Collection<Listener> listeners = new LinkedList<>();

    public PhotoFileRepository(FileWatcher watcher, PhotoSerializer serializer) {
        this(watcher, serializer, new PhysicalFileManager());
    }

    public PhotoFileRepository(FileWatcher watcher, PhotoSerializer serializer, FileManager fileManager) {
        if (fileManager == null) throw new IllegalArgumentException();
        if (watcher == null) throw new IllegalArgumentException();
        if (serializer == null) throw new IllegalArgumentException();
        this.fileManager = fileManager;
        this.watcher = watcher;
        this.serializer = serializer;
        watcher.addListener(listener);
    }

    /**
     * {@inheritDoc}
     *
     * This implementation checks whether the file is recognized by the underlying watcher.
     */
    @Override public boolean accepts(Path file) {
        if (file == null) throw new IllegalArgumentException();
        boolean result = watcher.recognizes(file);
        LOGGER.debug("accepts is {} for {}", result, file);
        return result;
    }

    @Override public void create(Path file, InputStream source) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        if (source == null) throw new IllegalArgumentException();
        LOGGER.debug("creating file {}", file);

        if (!accepts(file)) {
            LOGGER.debug("repository does not accept {}", file);
            throw new PhotoNotFoundException(this, file);
        }

        if (contains(file)) {
            LOGGER.debug("file already exists at {}", file);
            throw new PhotoAlreadyExistsException(this, file);
        }

        try {
            fileManager.createDirectories(file);
            fileManager.createFile(file);
            OutputStream destination = fileManager.newOutputStream(file);
            IOUtils.copy(source, destination);
            LOGGER.debug("created {}", file);
        } catch (IOException ex) {
            throw new DAOException(ex);
        }
    }

    @Override public void update(Photo photo) throws DAOException {
        if (photo == null) throw new IllegalArgumentException();
        LOGGER.debug("updating {}", photo);

        Path file = photo.getFile();
        if (!contains(file)) {
            LOGGER.trace("repository does not contain file {}", file);
            throw new PhotoNotFoundException(this, file);
        }

        Path temp = Paths.get(file.toString() + ".temp");
        InputStream is = null;
        OutputStream os = null;
        try {
            if (!fileManager.exists(temp)) {
                try {
                    fileManager.createFile(temp);
                } catch (IOException ex) {
                    LOGGER.warn("failed creating temp file at {}", temp);
                    throw new DAOException(ex);
                }
            }

            try {
                is = fileManager.newInputStream(file);
            } catch (IOException ex) {
                LOGGER.warn("failed creating input stream for file {}", file);
                throw new DAOException(ex);
            }

            try {
                os = fileManager.newOutputStream(temp);
            } catch (IOException ex) {
                LOGGER.warn("failed creating output stream for file {}", temp);
                throw new DAOException();
            }

            serializer.update(is, os, photo.getData());

            try {
                fileManager.copy(temp, file);
            } catch (IOException ex) {
                LOGGER.warn("failed copying {} -> {}", temp, file);
                throw new DAOException(ex);
            }

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                LOGGER.warn("failed closing input stream for file {}");
                LOGGER.error(ex);
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ex) {
                LOGGER.warn("failed closing output stream for file {}");
                LOGGER.error(ex);
            }
            try {
                if (fileManager.exists(temp)) {
                    fileManager.delete(temp);
                }
            } catch (IOException ex) {
                LOGGER.warn("failed deleting temp file {}", temp);
                LOGGER.error(ex);
            }
        }
    }

    @Override public void delete(Path file) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("deleting {}", file);

        if (!contains(file)) {
            LOGGER.debug("repository does not contain file {}", file);
            throw new PhotoNotFoundException(this, file);
        }
        try {
            fileManager.delete(file);
            LOGGER.info("deleted {}", file);
        } catch (IOException ex) {
            LOGGER.debug("failed deleting file {}", file);
            throw new DAOException(ex);
        }
    }

    @Override public void addListener(Listener listener) {
        if (listener == null) throw new IllegalArgumentException();
        listeners.add(listener);
        LOGGER.debug("added listener {}", listener);
    }

    @Override public void removeListener(Listener listener) {
        if (listener == null) throw new IllegalArgumentException();
        listeners.remove(listener);
        LOGGER.debug("removed listener {}", listener);
    }

    @Override public boolean contains(Path file) throws DAOException {
        LOGGER.debug("contains {}", file);
        boolean result = watcher.recognizes(file) && fileManager.exists(file);
        LOGGER.debug("contains is {} for {}", result, file);
        return result;
    }

    @Override public Collection<Path> index() throws DAOException {
        LOGGER.debug("indexing");
        Collection<Path> files = watcher.index();
        LOGGER.debug("indexed {}", files.size());
        return files;
    }

    @Override public Photo read(Path file) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("reading {}", file);

        if (!contains(file)) {
            LOGGER.debug("directory does not contain file {}", file);
            throw new PhotoNotFoundException(this, file);
        }

        InputStream is;
        try {
            is = fileManager.newInputStream(file);
        } catch (IOException ex) {
            LOGGER.warn("failed opening file {}", file);
            throw new DAOException(ex);
        }

        PhotoMetadata data = serializer.read(is);
        Photo photo = new Photo(file, data);

        try {
            is.close();
        } catch (IOException ex) {
            LOGGER.warn("failed closing input stream to file {}", file);
        }

        LOGGER.debug("read photo {}", photo);
        return photo;
    }

    private void notifyCreate(Path path) {
        LOGGER.debug("notify create of {}", path);
        listeners.forEach(l -> l.onCreate(this, path));
    }

    private void notifyUpdate(Path path) {
        LOGGER.debug("notify update of {}", path);
        listeners.forEach(l -> l.onUpdate(this, path));
    }

    private void notifyDelete(Path path) {
        LOGGER.debug("notify delete of {}", path);
        listeners.forEach(l -> l.onDelete(this, path));
    }

    private class FileListener implements FileWatcher.Listener {

        @Override public void onCreate(FileWatcher watcher, Path file) {
            notifyCreate(file);
        }

        @Override public void onUpdate(FileWatcher watcher, Path file) {
            notifyUpdate(file);
        }

        @Override public void onDelete(FileWatcher watcher, Path file) {
            notifyDelete(file);
        }
    }
}
