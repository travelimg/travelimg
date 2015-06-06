package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PersistenceException;
import at.ac.tuwien.qse.sepm.dao.repo.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoInfo;
import org.apache.logging.log4j.LogManager;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;

public class CachedPhotoRepository extends RunnablePhotoRepository {

    public CachedPhotoRepository() {
        super(LogManager.getLogger());
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
