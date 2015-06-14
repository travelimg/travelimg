package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoCache;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoNotFoundException;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Photo cache that stores photo instances in an SQLite database.
 */
public class JdbcPhotoCache implements PhotoCache {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private PhotoDAO photoDAO;

    @Override public void put(Photo photo) throws DAOException {
        if (photo == null) throw new IllegalArgumentException();
        LOGGER.debug("putting {}", photo);
        // TODO
        LOGGER.debug("put {}", photo);
    }

    @Override public void remove(Path file) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("removing {}", file);
        if (!contains(file)) {
            throw new PhotoNotFoundException(this, file);
        }
        // TODO
        LOGGER.debug("removed {}", file);
    }

    @Override public Collection<Path> index() throws DAOException {
        LOGGER.debug("indexing");
        Collection<Path> result = photoDAO.readAll().stream()
                .map(Photo::getFile)
                .collect(Collectors.toList());
        LOGGER.debug("indexed {}", result.size());
        return result;
    }

    @Override public Photo read(Path file) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("reading {}", file);
        if (!contains(file)) {
            LOGGER.warn("photo not found with file {}", file);
            throw new PhotoNotFoundException(this, file);
        }
        try {
            Photo photo = photoDAO.getByFile(file);
            LOGGER.debug("read {}", photo);
            return photo;
        } catch (DAOException | ValidationException ex) {
            throw new DAOException(ex);
        }
    }
}
