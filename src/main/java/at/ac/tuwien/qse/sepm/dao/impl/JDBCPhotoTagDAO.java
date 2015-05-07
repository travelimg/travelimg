package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.PhotoValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JDBCPhotoTagDAO extends JDBCDAOBase implements PhotoTagDAO {

    private static final Logger logger = LogManager.getLogger();

    private Connection con;

    private static final String deleteStatement = "Delete from PHOTOTAG  where PHOTO_ID=? and TAG_ID=? ";

    public JDBCPhotoTagDAO() throws DAOException {
        con = DBConnection.getConnection();
    }

    public void createPhotoTag(Photo p, Tag t) throws DAOException {

    }

    public void removeTagFromPhoto(Tag t, Photo p) throws DAOException, ValidationException {
        logger.debug("Deleting Tag from Photo {}", t ,p);
        PhotoValidator.validate(p);

        try (PreparedStatement stmt = con.prepareStatement(deleteStatement)) {
            stmt.setInt(1, p.getId());
            stmt.setInt(2,t.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DAOException("Failed to delete Tag from Photo", ex);
        }

    }

    public List<Tag> readTagsByPhoto(Photo p) throws DAOException {
        return null;
    }

    public List<Photo> readPhotosByTag(Tag t) throws DAOException {
        return null;
    }
}
