package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PersistenceException;
import at.ac.tuwien.qse.sepm.dao.repo.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoRepository;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;

public abstract class PhotoRepositoryBase
        extends PhotoProviderBase
        implements PhotoRepository {

    private final Collection<Listener> listeners = new LinkedList<>();

    public PhotoRepositoryBase(Logger logger) {
        super(logger);
    }

    @Override public void create(Path file, InputStream source) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        if (source == null) throw new IllegalArgumentException();
        LOGGER.debug("creating {}", file);
        createImpl(file, source);
        notifyCreate(file);
        LOGGER.info("created {}", file);
    }

    @Override public void update(Photo photo) throws PersistenceException {
        if (photo == null) throw new IllegalArgumentException();
        LOGGER.debug("updating {}", photo);
        updateImpl(photo);
        notifyUpdate(photo.getFile());
        LOGGER.info("updated {}", photo);
    }

    @Override public void delete(Path file) throws PersistenceException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("deleting {}", file);
        deleteImpl(file);
        notifyDelete(file);
        LOGGER.info("deleted {}", file);
    }

    @Override public void addListener(Listener listener) {
        if (listener == null) throw new IllegalArgumentException();
        listeners.add(listener);
    }

    @Override public void removeListener(Listener listener) {
        if (listener == null) throw new IllegalArgumentException();
        listeners.remove(listener);
    }

    protected void notifyCreate(Path file) {
        if (file == null) throw new IllegalArgumentException();
        listeners.forEach(l -> l.onCreate(this, file));
    }

    protected void notifyUpdate(Path file) {
        if (file == null) throw new IllegalArgumentException();
        listeners.forEach(l -> l.onUpdate(this, file));
    }

    protected void notifyDelete(Path file) {
        if (file == null) throw new IllegalArgumentException();
        listeners.forEach(l -> l.onDelete(this, file));
    }

    protected abstract void createImpl(Path file, InputStream source) throws PersistenceException;

    protected abstract void updateImpl(Photo photo) throws PersistenceException;

    protected abstract void deleteImpl(Path file) throws PersistenceException;
}
