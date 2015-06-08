package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PersistenceException;
import at.ac.tuwien.qse.sepm.dao.repo.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoRepository;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Repository that is an aggregate of multiple other repositories.
 */
public class MultiPhotoRepository implements PhotoRepository {

    @Override public boolean accepts(Path file) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override public void create(Path file, InputStream source) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override public void update(Photo photo) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override public void delete(Path file) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override public void addListener(Listener listener) {
        throw new UnsupportedOperationException();
    }

    @Override public void removeListener(Listener listener) {
        throw new UnsupportedOperationException();
    }

    @Override public Collection<Path> index() throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override public Photo read(Path file) throws PersistenceException {
        throw new UnsupportedOperationException();
    }
}
