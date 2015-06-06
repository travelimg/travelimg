package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PersistenceException;
import at.ac.tuwien.qse.sepm.dao.repo.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoInfo;
import org.apache.logging.log4j.LogManager;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;

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

    public PhotoDirectory(Path path) throws PersistenceException {
        super(LogManager.getLogger());
        if (path == null) throw new IllegalArgumentException();
        this.path = path;

        (new Thread(this)).start();
    }

    /**
     * Get the path of the directory this repository uses for storing photo files.
     *
     * @return path of the directory
     */
    public Path getPath() {
        return path;
    }

    @Override protected void createImpl(Path file, InputStream source) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override protected void updateImpl(Photo photo) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override protected void deleteImpl(Path file) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override protected PhotoInfo checkImpl(Path file) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override protected Collection<PhotoInfo> checkAllImpl() throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override protected Photo readImpl(Path file) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override protected Collection<Photo> readAllImpl() throws PersistenceException {
        throw new UnsupportedOperationException();
    }
}
