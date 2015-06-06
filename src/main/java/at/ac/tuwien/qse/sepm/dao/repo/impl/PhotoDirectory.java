package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PersistenceException;
import at.ac.tuwien.qse.sepm.dao.repo.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoInfo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoNotFoundException;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
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

    private final Path path;

    private WatchService watcher;

    public PhotoDirectory(Path path) throws PersistenceException {
        super(LogManager.getLogger());
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

    @Override protected PhotoInfo checkImpl(Path file) throws PersistenceException {
        if (!contains(file)) {
            LOGGER.debug("directory does not contain file {}", file);
            return null;
        }
        FileTime fileTime;
        try {
            fileTime = Files.getLastModifiedTime(file);
        } catch (IOException ex) {
            LOGGER.debug("failed reading modification time for file {}", file);
            throw new PersistenceException(ex);
        }

        LocalDateTime time = LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
        return new PhotoInfo(file, time);
    }

    @Override protected Collection<PhotoInfo> checkAllImpl() throws PersistenceException {
        // Directory is created on demand. So if no photos have been added yet, it may not exist.
        // Files.list fails if that is the case.
        if (!Files.exists(getPath())) {
            LOGGER.debug("directory does not exist at {}", getPath());
            return new ArrayList<>(0);
        }
        try {
            Collection<Path> files = Files.list(getPath()).collect(Collectors.toList());
            Collection<PhotoInfo> result = new ArrayList<>(files.size());
            LOGGER.debug("found {} files in directory {}", files.size(), getPath());
            for (Path file : files) {
                result.add(check(file));
            }
            return result;
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override protected Photo readImpl(Path file) throws PersistenceException {
        if (!contains(file)) {
            LOGGER.debug("directory does not contain file {}", file);
            throw new PhotoNotFoundException(this, file);
        }

        throw new UnsupportedOperationException();
    }

    @Override protected Collection<Photo> readAllImpl() throws PersistenceException {
        try {
            Collection<Path> files = Files.list(getPath()).collect(Collectors.toList());
            Collection<Photo> photos = new ArrayList<>(files.size());
            LOGGER.debug("found {} files in directory {}", files.size(), getPath());
            for (Path file : files) {
                Photo photo = read(file);
                photos.add(photo);
            }
            return photos;
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override protected void createImpl(Path file, InputStream source) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override protected void updateImpl(Photo photo) throws PersistenceException {
        Path file = photo.getFile();
        if (!contains(file)) {
            LOGGER.debug("directory does not contain file {}", file);
            throw new PhotoNotFoundException(this, file);
        }
        throw new UnsupportedOperationException();
    }

    @Override protected void deleteImpl(Path file) throws PersistenceException {
        if (!contains(file)) {
            LOGGER.debug("directory does not contain file {}", file);
            throw new PhotoNotFoundException(this, file);
        }
        try {
            Files.delete(file);
        } catch (IOException ex) {
            LOGGER.debug("failed deleting file {}", file);
            throw new PersistenceException(ex);
        }
    }

    protected Photo read(Path file, InputStream stream) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void update(Photo photo, OutputStream stream) throws IOException {
        throw new UnsupportedOperationException();
    }

    private boolean contains(Path file) {
        return Files.exists(file) && file.startsWith(getPath());
    }

    private void runSetUp() throws PersistenceException {

        // Files gather, and now my watch begins.
        WatchService watcher;
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            LOGGER.error("failed creating watcher for directory {}", getPath());
            stop();
            notifyError(new PersistenceException(ex));
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
            notifyError(new PersistenceException(ex));

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
                notifyCreate(file);
            }
            if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                notifyUpdate(file);
            }
            if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                notifyDelete(file);
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

    private void stop() {
        // TODO
    }
}
