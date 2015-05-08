package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JDBCPhotoTagDAO extends JDBCDAOBase implements PhotoTagDAO {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String CREATE_STRING = "INSERT INTO phototag VALUES(?, ?)";
    private static final String DELETE_STRING = "DELETE FROM phototag WHERE "
            + "photo_id = ? AND tag_id = ?";
    private PreparedStatement createStmt;
    private PreparedStatement deleteStmt;

    /**
     * Create new instance and initialize statements
     *
     * @throws DAOException if an SQLException occurs during statement initialization
     */
    public JDBCPhotoTagDAO() throws DAOException {
        LOGGER.debug("Entering JDBCPhotoTagDAO-Constructor");
        try {
            createStmt = getConnection().prepareStatement(CREATE_STRING);
            deleteStmt = getConnection().prepareStatement(DELETE_STRING);
        } catch (SQLException ex) {
            LOGGER.error("SQL Exception while initializing statements.");
            throw new DAOException("Statements could not be initialized: " + ex.getMessage());
        }
        LOGGER.debug("Leaving JDBCPhotoTagDAO-Constructor");
    }

    /**
     * Create a photo-tag entry in database which links Tag <tt>tag</tt> to Photo <tt>photo</tt>.
     * If an equal entry already exists, nothing happens.
     *
     * @param photo must not be null; photo.id must not be null;
     * @param tag   must not be null; tag.id must not be null;
     * @throws DAOException: if an exception occurs on persistence layer
     */
    public void createPhotoTag(Photo photo, Tag tag) throws DAOException {
        LOGGER.debug("Entering createPhotoTag with {} {}", photo, tag);
        //TODO: Check if photo-tag entry already exists. Implement using 'readTagsByPhoto'
        try {
            createStmt.setInt(1, photo.getId());
            createStmt.setInt(2, tag.getId());

            createStmt.executeUpdate();
            LOGGER.info("Photo-Tag entry successfully created");
        } catch (SQLException ex) {
            LOGGER.error("Photo-Tag entry creation failed due to SQLException");
            throw new DAOException("Photo-tag entry creation failed: " + ex.getMessage());
        }
        LOGGER.debug("Leaving createPhotoTag with {} {}", photo, tag);
    }

    /**
     * Delete if exists the photo-tag entry where Photo = <tt>photo</tt> and Tag = <tt>tag</tt>
     *
     * @param photo must not be null; photo.id must not be null;
     * @param tag   must not be null; tag.id must not be null;
     * @throws DAOException: if an exception occurs on persistence layer
     */
    public void removeTagFromPhoto(Photo photo, Tag tag) throws DAOException {
        LOGGER.debug("Entering removeTagFromPhoto with {} {}", photo, tag);
        try {
            deleteStmt.setInt(1, photo.getId());
            deleteStmt.setInt(2, tag.getId());
            deleteStmt.executeUpdate();
            LOGGER.info("Photo-Tag entry successfully deleted");
        } catch (SQLException ex) {
            LOGGER.error("Photo-Tag entry deletion failed due to SQLException");
            throw new DAOException("Could not delete photo-tag entry: " + ex.getMessage());
        }
        LOGGER.debug("Leaving removeTagFromPhoto with {} {}", photo, tag);
    }

    public List<Tag> readTagsByPhoto(Photo photo) throws DAOException {
        return null;
    }

    public List<Photo> readPhotosByTag(Tag tag) throws DAOException {
        return null;
    }
}
