package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.PhotoValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JDBCPhotoTagDAO extends JDBCDAOBase implements PhotoTagDAO {

    private static final Logger logger = LogManager.getLogger();



    private static final String deleteStatement = "Delete from PHOTOTAG  where PHOTO_ID=? and TAG_ID=? ";

    public JDBCPhotoTagDAO() throws DAOException {

    }

    public void createPhotoTag(Photo p, Tag t) throws DAOException {

    }

    /**
     *  delete the delivered Tag from the delivered Photo
     * @param t which Tag to delete
     * @param p on which Photo
     * @throws DAOException
     * @throws ValidationException
     */
    public void removeTagFromPhoto(Tag t, Photo p) throws DAOException, ValidationException {
        logger.debug("Deleting Tag from Photo {}", t );
        PhotoValidator.validate(p);
        try{
            jdbcTemplate.update(deleteStatement,p.getId(),t.getId());
        }catch(DataAccessException e) {
        throw new DAOException("Failed to delete Tag from Photo", e);
        }

    }

    public List<Tag> readTagsByPhoto(Photo p) throws DAOException {
        return null;
    }

    public List<Photo> readPhotosByTag(Tag t) throws DAOException {
        return null;
    }
}
