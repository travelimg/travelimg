package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PersistenceException;
import at.ac.tuwien.qse.sepm.dao.repo.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoInfo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoProvider;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Collection;

public abstract class PhotoProviderBase implements PhotoProvider {

    protected final Logger LOGGER;

    protected PhotoProviderBase(Logger logger) {
        if (logger == null) throw new IllegalArgumentException();
        this.LOGGER = logger;
    }

    @Override public PhotoInfo check(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("checking {}", file);
        PhotoInfo result = checkImpl(file);
        LOGGER.info("checked {}", file);
        return result;
    }

    @Override public Collection<PhotoInfo> checkAll() throws PersistenceException {
        LOGGER.debug("checking all");
        Collection<PhotoInfo> result = checkAllImpl();
        LOGGER.info("checked {} photos", result.size());
        return result;
    }

    @Override public Photo read(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("reading {}", file);
        Photo result = readImpl(file);
        LOGGER.info("read {}", file);
        return result;
    }

    @Override public Collection<Photo> readAll() throws PersistenceException {
        LOGGER.debug("reading all");
        Collection<Photo> result = readAllImpl();
        LOGGER.info("read {} photos", result.size());
        return result;
    }

    protected abstract PhotoInfo checkImpl(Path file) throws PersistenceException;

    protected abstract Collection<PhotoInfo> checkAllImpl() throws PersistenceException;

    protected abstract Photo readImpl(Path file) throws PersistenceException;

    protected abstract Collection<Photo> readAllImpl() throws PersistenceException;
}
