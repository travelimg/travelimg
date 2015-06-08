package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PersistenceException;
import at.ac.tuwien.qse.sepm.dao.repo.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Photo repository that stores file in a directory.
 *
 * Files that are not children directory's path are considered invalid and cannot be created.
 *
 * This class uses a WatchService for observing external changes to the files in the directory.
 * If the client creates, updates, or deletes files from the file system this repository will notify
 * the attached listeners.
 */
public class PhotoDirectory extends RunnablePhotoRepository {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Path path;
    private final Collection<Listener> listeners = new LinkedList<>();

    private WatchService watcher;

    public PhotoDirectory(Path path) throws PersistenceException {
        if (path == null) throw new IllegalArgumentException();
        this.path = path;
    }

    /**
     * Get the path of the directory this repository uses for storing photo files.
     *
     * @return path of the directory
     */
    public Path getPath() {
        return path;
    }

    @Override public boolean accepts(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        return file.startsWith(getPath());
    }

    @Override public void create(Path file, InputStream source) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override public void update(Photo photo) throws PersistenceException {
        if (photo == null) throw new IllegalArgumentException();
        LOGGER.debug("updating {}", photo);

        Path file = photo.getFile();
        if (!contains(file)) {
            LOGGER.debug("directory does not contain file {}", file);
            throw new PhotoNotFoundException(this, file);
        }
        throw new UnsupportedOperationException();
    }

    @Override public void delete(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("deleting {}", file);

        if (!contains(file)) {
            LOGGER.debug("directory does not contain file {}", file);
            throw new PhotoNotFoundException(this, file);
        }
        try {
            Files.delete(file);
        } catch (IOException ex) {
            LOGGER.debug("failed deleting file {}", file);
            LOGGER.info("deleted {}", file);
            throw new PersistenceException(ex);
        }
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

    @Override public boolean contains(Path file) throws PersistenceException {
        LOGGER.debug("contains {}", file);
        boolean result = Files.exists(file) && file.startsWith(getPath());
        LOGGER.info("contains {} is {}", file, result);
        return result;
    }

    @Override public Collection<Path> index() throws PersistenceException {
        LOGGER.debug("indexing");

        // Directory is created on demand. So if no photos have been added yet, it may not exist.
        // Files.list fails if that is the case.
        if (!Files.exists(getPath())) {
            LOGGER.debug("directory does not exist at {}", getPath());
            return new ArrayList<>(0);
        }
        try {
            Collection<Path> files = Files.list(getPath()).collect(Collectors.toList());
            LOGGER.debug("found {} files in directory", files.size());
            LOGGER.info("indexed {}", files.size());
            return files;
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
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

    protected Photo read(Path file, InputStream stream) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void update(Photo photo, OutputStream stream) throws IOException {
        throw new UnsupportedOperationException();
    }

    private void runSetUp() throws PersistenceException {

        // Files gather, and now my watch begins.
        WatchService watcher;
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            LOGGER.error("failed creating watcher for directory {}", getPath());
            stop();
            //notifyError(new PersistenceException(ex));
            return;
        }

        try {
            getPath().register(watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
        } catch (IOException ex) {
            LOGGER.error("failed registering watcher for directory {}", getPath());
            LOGGER.error(ex);
            stop();
            //notifyError(new PersistenceException(ex));

            try {
                watcher.close();
            } catch (IOException exx) {
                LOGGER.error("failed closing watcher for directory {}", getPath());
                LOGGER.error(exx);
            }

            return;
        }
    }

    private void action() throws InterruptedException {

        // Wait for key to be signaled.
        WatchKey key = watcher.take();

        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();

            // Some events may have been lost.
            if (kind == StandardWatchEventKinds.OVERFLOW) {
                LOGGER.error("watch event overflow in directory {}", getPath());
                continue;
            }

            // Fetch the filename.
            WatchEvent<Path> ev = (WatchEvent<Path>)event;
            Path relativeFile = ev.context();
            Path file = getPath().resolve(relativeFile);
            LOGGER.debug("received file event {} for file {}", kind, file);

            if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                listeners.forEach(l -> l.onCreate(this, file));
            }
            if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                listeners.forEach(l -> l.onUpdate(this, file));
            }
            if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                listeners.forEach(l -> l.onDelete(this, file));
            }
        }

        // Check if watcher is still running.
        boolean valid = key.reset();
        if (!valid) {
            LOGGER.info("watch key invalid");
            stop();
        }
    }

    private void tearDown() {
        try {
            watcher.close();
        } catch (IOException ex) {
            LOGGER.error("failed closing watcher for directory {}", getPath());
            LOGGER.error(ex);
        }
    }
}
