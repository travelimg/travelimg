package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class JDBCPhotoTagDAO extends JDBCDAOBase implements PhotoTagDAO {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String CREATE_STRING = "INSERT INTO phototag VALUES(?, ?)";
    private static final String DELETE_STRING =
            "DELETE FROM phototag WHERE " + "photo_id = ? AND tag_id = ?";
    private static final String READ_TAGS_BY_PHOTO_STRING = "SELECT DISTINCT tag_id, name FROM "
            + "(tag JOIN phototag ON id = tag_id) WHERE photo_id = ?";
    private static final String READ_PHOTOS_BY_TAG_STRING =
            "SELECT DISTINCT * FROM (photo JOIN phototag ON id = photo_id) where tag_id = ?";
    private PreparedStatement createStmt;
    private PreparedStatement deleteStmt;
    private PreparedStatement readTagsStmt;
    private PreparedStatement readPhotosStmt;

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
            readTagsStmt = getConnection().prepareStatement(READ_TAGS_BY_PHOTO_STRING);
            readPhotosStmt = getConnection().prepareStatement(READ_PHOTOS_BY_TAG_STRING);
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

    /**
     * Return list of all tags which are currently set for <tt>photo</tt>.
     *
     * @param photo must not be null; photo.id must not be null;
     * @return List with all tags which are linked to <tt>photo</tt> as a PhotoTag;
     * If no tag exists, return an empty List.
     * @throws DAOException if an exception occurs on persistence layer
     */
    public List<Tag> readTagsByPhoto(Photo photo) throws DAOException {
        LOGGER.debug("Entering readTagsByPhoto with {}", photo);
        List<Tag> tagList = new LinkedList<>();
        try {
            ResultSet rs = readTagsStmt.executeQuery();
            while (rs.next()) {
                tagList.add(new Tag(rs.getInt("id"), rs.getString("name")));
            }
            LOGGER.info("Successfully read tags for {}", photo);
        } catch (SQLException ex) {
            LOGGER.error("Reading Tags failed due to SQLException");
            throw new DAOException("Could not read tags: " + ex.getMessage());
        }
        LOGGER.debug("Leaving readTagsByPhoto with {}", photo);
        return tagList;
    }

    /**
     * Return list of all photos which are currently tagged with Tag <tt>tag</tt>.
     *
     * @param tag must not be null; tag.id must not be null
     * @return List with all Photos, which are linked to <tt>tag</tt> as a PhotoTag;
     * @throws DAOException if an exception occurs on persistence layer
     */
    public List<Photo> readPhotosByTag(Tag tag) throws DAOException {
        LOGGER.debug("Entering readPhotosByTag with {}", tag);
        List<Photo> photoList = new LinkedList<>();
        try {
            ResultSet rs = readPhotosStmt.executeQuery();
            while (rs.next()) {
                photoList.add(new Photo(rs.getInt("id"), null,
                                //TODO: get Photographer by photographer_id
                                rs.getString("path"), rs.getInt("rating")));
                //TODO: get Exif data for photo
            }
            LOGGER.info("Successfully read photos for {}", tag);
        } catch (SQLException ex) {
            LOGGER.error("Reading photos failed due to SQLException");
            throw new DAOException("Could not read photos: " + ex.getMessage());
        }
        LOGGER.debug("Leaving readPhotosByTag with {}", tag);
        return photoList;
    }
}
