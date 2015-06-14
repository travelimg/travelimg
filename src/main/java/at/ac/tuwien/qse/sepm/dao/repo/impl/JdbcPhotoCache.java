package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.repo.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoCache;
import at.ac.tuwien.qse.sepm.dao.DAOException;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.Collection;

/**
 * Photo cache that stores photo instances in an SQLite database.
 */
public class JdbcPhotoCache implements PhotoCache {

    @Autowired
    private PhotoDAO photoDAO;

    @Override public void put(Photo photo) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override public void remove(Path file) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override public Collection<Path> index() throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override public Photo read(Path file) throws DAOException {
        throw new UnsupportedOperationException();
    }
}
