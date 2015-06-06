package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PersistenceException;
import at.ac.tuwien.qse.sepm.dao.repo.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoInfo;
import org.apache.logging.log4j.LogManager;

import java.nio.file.Path;
import java.util.Collection;

public class JdbcPhotoCache extends PhotoCacheBase {

    public JdbcPhotoCache() {
        super(LogManager.getLogger());
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

    @Override protected void putImpl(Photo photo) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override protected void removeImpl(Path file) throws PersistenceException {
        throw new UnsupportedOperationException();
    }
}
