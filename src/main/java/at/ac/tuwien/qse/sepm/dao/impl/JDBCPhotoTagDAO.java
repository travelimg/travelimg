package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.PhotoValidator;
import at.ac.tuwien.qse.sepm.entities.validators.TagValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class JDBCPhotoTagDAO extends JDBCDAOBase implements PhotoTagDAO {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String CREATE_STRING = "INSERT INTO phototag VALUES(?, ?)";
    private static final String DELETE_STRING = "DELETE FROM phototag WHERE "
            + "photo_id = ? AND tag_id = ?";
    private static final String READ_TAGS_BY_PHOTO_STRING = "SELECT DISTINCT tag_id, name FROM "
            + "(tag JOIN phototag ON id = tag_id) WHERE photo_id = ?";
    private static final String READ_PHOTOS_BY_TAG_STRING = "SELECT DISTINCT * FROM (photo JOIN "
            + "phototag ON id = photo_id) where tag_id = ?";

    /**
     * Create a photo-tag entry in database which links Tag <tt>tag</tt> to Photo <tt>photo</tt>.
     * If an equal entry already exists, nothing happens.
     *
     * @param photo must not be null; photo.id must not be null;
     * @param tag   must not be null; tag.id must not be null;
     * @throws DAOException: if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    public void createPhotoTag(Photo photo, Tag tag) throws DAOException, ValidationException {
        LOGGER.debug("Entering createPhotoTag with {} {}", photo, tag);

        PhotoValidator.validateID(photo.getId());
        TagValidator.validateID(tag);

        if (!readTagsByPhoto(photo).contains(tag)) {
            try {
                jdbcTemplate.update(CREATE_STRING, photo.getId(), tag.getId());
                LOGGER.info("Photo-Tag entry successfully created");
            } catch (DataAccessException ex) {
                LOGGER.error("Photo-Tag entry creation failed due to DataAccessException");
                throw new DAOException("Photo-tag entry creation failed.", ex);
            }
        } else {
            //photo-tag entry already exists
            LOGGER.info("PhotoTag entry to be created already exists. No action taken.");
        }
        LOGGER.debug("Leaving createPhotoTag with {} {}", photo, tag);
    }

    /**
     * Delete if exists the photo-tag entry where Photo = <tt>photo</tt> and Tag = <tt>tag</tt>
     *
     * @param photo must not be null; photo.id must not be null;
     * @param tag   must not be null; tag.id must not be null;
     * @throws DAOException: if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    public void removeTagFromPhoto(Photo photo, Tag tag) throws DAOException, ValidationException {
        LOGGER.debug("Entering removeTagFromPhoto with {} {}", photo, tag);

        PhotoValidator.validateID(photo.getId());
        TagValidator.validateID(tag);

        try {
            jdbcTemplate.update(DELETE_STRING, photo.getId(), tag.getId());
            LOGGER.info("Photo-Tag entry successfully deleted");
        } catch (DataAccessException ex) {
            LOGGER.error("Photo-Tag entry deletion failed due to DataAccessException");
            throw new DAOException("Could not delete photo-tag entry.", ex);
        }
        LOGGER.debug("Leaving removeTagFromPhoto with {} {}", photo, tag);
    }

    /**
     * Return list of all tags which are currently set for <tt>photo</tt>.
     *
     * @param photo must not be null; photo.id must not be null;
     * @return List with all tags which are linked to <tt>photo</tt> as a PhotoTag;
     *     If no tag exists, return an empty List.
     * @throws DAOException if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    public List<Tag> readTagsByPhoto(Photo photo) throws DAOException, ValidationException {
        LOGGER.debug("Entering readTagsByPhoto with {}", photo);

        PhotoValidator.validateID(photo.getId());

        List<Tag> tagList;
        try {
            tagList = jdbcTemplate.query(READ_TAGS_BY_PHOTO_STRING, (rs, rowNum) -> {
                    return new Tag(rs.getInt("id"), rs.getString("name"));
                });
            LOGGER.info("Successfully read tags for {}", photo);
        } catch (DataAccessException ex) {
            LOGGER.error("Reading Tags failed due to DataAccessException");
            throw new DAOException("Could not read tags.", ex);
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
     * @throws ValidationException: if parameter validation fails
     */
    public List<Photo> readPhotosByTag(Tag tag) throws DAOException, ValidationException {
        LOGGER.debug("Entering readPhotosByTag with {}", tag);
        TagValidator.validateID(tag);
        List<Photo> photoList;
        try {
            photoList = jdbcTemplate.query(READ_PHOTOS_BY_TAG_STRING, (rs, rowNum) -> {
                Photo photo = new Photo(rs.getInt(1), null, rs.getString(3), rs.getInt(4),rs.getTimestamp(5).toLocalDateTime().toLocalDate(),rs.getDouble(6),rs.getDouble(7));
                return photo;
                });
            LOGGER.info("Successfully read photos for {}", tag);
        } catch (DataAccessException ex) {
            LOGGER.error("Reading photos failed due to DataAccessException");
            throw new DAOException("Could not read photos.", ex);
        }
        LOGGER.debug("Leaving readPhotosByTag with {}", tag);
        return photoList;
    }
}
