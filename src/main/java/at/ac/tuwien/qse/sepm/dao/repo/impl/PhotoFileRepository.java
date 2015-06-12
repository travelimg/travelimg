package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.*;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Photo repository that manages photos as files in a directory.
 */
public class PhotoFileRepository extends RunnablePhotoRepository {

    private static final Logger LOGGER = LogManager.getLogger();

    private final FileWatcher watcher;
    private final PhotoSerializer serializer;

    private final FileListener listener = new FileListener();
    private final Collection<Listener> listeners = new LinkedList<>();

    public PhotoFileRepository() {
        this(new PollingFileWatcher(), new JpegSerializer());
    }

    public PhotoFileRepository(FileWatcher watcher, PhotoSerializer serializer) {
        if (watcher == null) throw new IllegalArgumentException();
        if (serializer == null) throw new IllegalArgumentException();
        this.watcher = watcher;
        this.serializer = serializer;
        watcher.addListener(listener);
    }

    /**
     * {@inheritDoc}
     *
     * This implementation checks whether the file is a child of any of the directories that are
     * registered to the watcher.
     */
    @Override public boolean accepts(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        for (Path directory : watcher.getDirectories()) {
            if (file.startsWith(directory)) {
                LOGGER.debug("accepting file {} in directory {}", file, directory);
                return true;
            }
        }
        LOGGER.warn("cannot accept file {}", file);
        return false;
    }

    @Override public void create(Path file, InputStream source) throws PersistenceException {
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
            Files.createDirectories(file.getParent());
            Files.createFile(file);
            OutputStream destination = Files.newOutputStream(file);
            IOUtils.copy(source, destination);
            LOGGER.debug("created {}", file);
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override public void update(Photo photo) throws PersistenceException {
        if (photo == null) throw new IllegalArgumentException();
        LOGGER.debug("updating {}", photo);

        Path file = photo.getFile();
        if (!contains(file)) {
            LOGGER.trace("repository does not contain file {}", file);
            throw new PhotoNotFoundException(this, file);
        }

        Path temp = file.resolve(".temp");
        InputStream is = null;
        OutputStream os = null;
        try {
            try {
                is = Files.newInputStream(file);
            } catch (IOException ex) {
                LOGGER.warn("failed creating input stream for file {}", file);
                throw new PersistenceException(ex);
            }

            try {
                os = Files.newOutputStream(temp);
            } catch (IOException ex) {
                LOGGER.warn("failed creating output stream for file {}", temp);
                throw new PersistenceException();
            }

            serializer.update(is, os, photo.getData());

            try {
                Files.copy(temp, file, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                LOGGER.warn("failed copying {} -> {}", temp, file);
                throw new PersistenceException(ex);
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
                if (Files.exists(temp)) {
                    Files.delete(temp);
                }
            } catch (IOException ex) {
                LOGGER.warn("failed deleting temp file {}", temp);
                LOGGER.error(ex);
            }
        }
    }

    @Override public void delete(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("deleting {}", file);

        if (!contains(file)) {
            LOGGER.debug("repository does not contain file {}", file);
            throw new PhotoNotFoundException(this, file);
        }
        try {
            Files.delete(file);
            LOGGER.info("deleted {}", file);
        } catch (IOException ex) {
            LOGGER.debug("failed deleting file {}", file);
            throw new PersistenceException(ex);
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

    @Override public boolean contains(Path file) throws PersistenceException {
        LOGGER.debug("contains {}", file);
        boolean result = Files.exists(file) && accepts(file);
        LOGGER.debug("contains {} is {}", file, result);
        return result;
    }

    @Override public Collection<Path> index() throws PersistenceException {
        LOGGER.debug("indexing");
        Collection<Path> files = watcher.index();
        LOGGER.debug("indexed {}", files.size());
        return files;
    }

    @Override public Photo read(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("reading {}", file);

        if (!contains(file)) {
            LOGGER.debug("directory does not contain file {}", file);
            throw new PhotoNotFoundException(this, file);
        }

        throw new UnsupportedOperationException();
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
