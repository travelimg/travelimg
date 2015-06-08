package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PersistenceException;
import at.ac.tuwien.qse.sepm.dao.repo.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoCache;

import java.nio.file.Path;
import java.util.Collection;

/**
 * Photo cache that stores photo instances in an SQLite database.
 */
public class JdbcPhotoCache implements PhotoCache {

    @Override public void put(Photo photo) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override public void remove(Path file) throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override public Collection<Path> index() throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override public Photo read(Path file) throws PersistenceException {
        throw new UnsupportedOperationException();
    }
}
