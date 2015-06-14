package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoNotFoundException;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoCache;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

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
        return photoDAO.readAll().stream()
                .map(Photo::getFile)
                .collect(Collectors.toList());
    }

    @Override public Photo read(Path file) throws DAOException {
        try {
            return photoDAO.getByFile(file);
        } catch (DAOException | ValidationException ex) {
            throw new PhotoNotFoundException(this, file);
        }
    }
}
