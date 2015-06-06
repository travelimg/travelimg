package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PersistenceException;
import at.ac.tuwien.qse.sepm.dao.repo.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoCache;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public abstract class PhotoCacheBase
        extends PhotoProviderBase
        implements PhotoCache {

    protected PhotoCacheBase(Logger logger) {
        super(logger);
    }

    @Override public void put(Photo photo) throws PersistenceException {
        if (photo == null) throw new IllegalArgumentException();
        LOGGER.debug("checking {}", photo);
        putImpl(photo);
        LOGGER.info("checked {}", photo);
    }

    @Override public void remove(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("removing {}", file);
        removeImpl(file);
        LOGGER.info("checked {}", file);
    }

    protected abstract void putImpl(Photo photo) throws PersistenceException;

    protected abstract void removeImpl(Path file) throws PersistenceException;
}
