package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.entities.validators.PhotoValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.util.IOHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class JDBCPhotoDAO extends JDBCDAOBase implements PhotoDAO {

    private static final String READ_ALL_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id FROM PHOTO;";
    private static final String DELETE_STATEMENT = "Delete from Photo where id =?";
    private static final String GET_BY_ID_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id FROM Photo where id=?";
    private static final String GET_BY_FILE_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id FROM Photo where path=?";
    private static final String UPDATE_STATEMENT = "UPDATE Photo SET path = ?, rating = ?, place_id = ?, journey_id WHERE id = ?";
    private static final String READ_JOURNEY_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id FROM PHOTO WHERE journey_id=? ORDER BY datetime ASC";

    private SimpleJdbcInsert insertPhoto;

    @Autowired
    private PhotoTagDAO photoTagDAO;
    @Autowired
    private PhotographerDAO photographerDAO;
    @Autowired
    private PlaceDAO placeDAO;
    @Autowired
    private JourneyDAO journeyDAO;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertPhoto = new SimpleJdbcInsert(dataSource)
                .withTableName("Photo")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Photo create(Photo photo) throws DAOException, ValidationException {
        logger.debug("Creating photo {}", photo);

        PhotoValidator.validate(photo);

        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("photographer_id", photo.getData().getPhotographer().getId());
        parameters.put("path", photo.getPath());
        parameters.put("rating", photo.getData().getRating().ordinal());
        parameters.put("datetime", Timestamp.valueOf(photo.getData().getDatetime()));
        parameters.put("latitude", photo.getData().getLatitude());
        parameters.put("longitude", photo.getData().getLongitude());
        parameters.put("place_id", photo.getData().getPlace().getId());
        parameters.put("journey_id", photo.getData().getJourney().getId());

        try {
            Number newId = insertPhoto.executeAndReturnKey(parameters);
            photo.setId((int) newId.longValue());
            return photo;
        } catch (DataAccessException ex) {
            logger.error("Failed to create photo", ex);
            throw new DAOException("Failed to create photo", ex);
        }
    }

    @Override
    public void update(Photo photo) throws DAOException, ValidationException {
        logger.debug("Updating photo {}", photo);

        PhotoValidator.validate(photo);

        try {
            jdbcTemplate.update(UPDATE_STATEMENT,
                    photo.getPath(),
                    photo.getData().getRating().ordinal(),
                    photo.getData().getPlace().getId(),
                    photo.getData().getJourney().getId(),
                    photo.getId());
            logger.debug("Successfully update photo {}", photo);
        } catch (DataAccessException e) {
            logger.debug("Failed updating photo {}", photo);
            throw new DAOException("Failed to update photo", e);
        }
    }

    @Override
    public void delete(Photo photo) throws DAOException, ValidationException {
        logger.debug("Deleting photo {}", photo);

        PhotoValidator.validate(photo);
        PhotoValidator.validateID(photo.getId());

        int id = photo.getId();

        try {
            photoTagDAO.deleteAllEntriesOfSpecificPhoto(photo);
            jdbcTemplate.update(DELETE_STATEMENT, id);
        } catch (DataAccessException e) {
            throw new DAOException("Failed to delete photo", e);
        }
    }

    @Override
    public Photo getById(int id) throws DAOException, ValidationException {
        logger.debug("Get photo with id {}", id);

        PhotoValidator.validateID(id);

        try {
            return this.jdbcTemplate.queryForObject(GET_BY_ID_STATEMENT, new Object[]{id}, new PhotoRowMapper());
        } catch (DataAccessException ex) {
            logger.error("Failed to get photo", ex);
            throw new DAOException("Failed to get photo", ex);
        }
    }

    @Override
    public Photo getByFile(Path file) throws DAOException, ValidationException {
        logger.debug("Get photo with path {}", file);
        if (file == null) {
            throw new ValidationException("file can not be null");
        }

        try {
            return jdbcTemplate.queryForObject(GET_BY_FILE_STATEMENT, new Object[]{file.toString()}, new PhotoRowMapper());
        } catch (DataAccessException ex) {
            logger.error("Failed to get photo", ex);
            throw new DAOException("Failed to get photo", ex);
        }
    }


    @Override
    public List<Photo> readAll() throws DAOException {
        logger.debug("retrieving all photos");

        try {
            List<Photo> photos = jdbcTemplate.query(READ_ALL_STATEMENT, new PhotoRowMapper());

            logger.debug("Successfully read all photos: " + photos.size());
            return photos;
        } catch (DataAccessException e) {
            throw new DAOException("Failed to read all photos", e);
        } catch (ValidationException.Unchecked | DAOException.Unchecked ex) {
            throw new DAOException(ex);
        }
    }

    @Override
    public List<Photo> readPhotosByJourney(Journey journey) throws DAOException {
        logger.debug("retrieving photos for monthh {}", journey);

        try {
            List<Photo> photos = jdbcTemplate.query(READ_JOURNEY_STATEMENT, new PhotoRowMapper(), journey.getId());

            logger.debug("Successfully retrieved photos");
            return photos;
        } catch (DataAccessException ex) {
            logger.error("Failed to read photos from given journey", ex);
            throw new DAOException("Failed to read photos from given journey", ex);
        } catch (ValidationException.Unchecked | DAOException.Unchecked ex) {
            logger.error("Failed to read photos from given journey", ex);
            throw new DAOException("Failed to read photos from given journey", ex.getCause());
        }
    }

    private class PhotoRowMapper implements RowMapper<Photo> {
        @Override
        public Photo mapRow(ResultSet rs, int rowNum) throws SQLException {

            Photo photo = new Photo();
            photo.setId(rs.getInt(1));
            photo.setPath(rs.getString(3));
            photo.getData().setRating(Rating.from(rs.getInt(4)));
            photo.getData().setDatetime(rs.getTimestamp(5).toLocalDateTime());
            photo.getData().setLatitude(rs.getDouble(6));
            photo.getData().setLongitude(rs.getDouble(7));

            try {
                int placeId = rs.getInt(8);
                Place place = placeDAO.getById(placeId);
                photo.getData().setPlace(place);
            } catch (DAOException ex) {
                throw new DAOException.Unchecked(ex);
            } catch (ValidationException ex) {
                throw new ValidationException.Unchecked(ex);
            }

            try {
                int journeyId = rs.getInt(9);
                Journey journey = journeyDAO.getByID(journeyId);
                photo.getData().setJourney(journey);
            } catch (DAOException ex) {
                throw new DAOException.Unchecked(ex);
            } catch (ValidationException ex) {
                throw new ValidationException.Unchecked(ex);
            }

            try {
                int photographerId = rs.getInt(2);
                Photographer photographer = photographerDAO.getById(photographerId);
                photo.getData().setPhotographer(photographer);
            } catch (DAOException ex) {
                throw new DAOException.Unchecked(ex);
            }

            try {
                List<Tag> tags = photoTagDAO.readTagsByPhoto(photo);
                photo.getData().getTags().addAll(tags);
            } catch (DAOException ex) {
                throw new DAOException.Unchecked(ex);
            } catch (ValidationException ex) {
                throw new ValidationException.Unchecked(ex);
            }

            return photo;
        }
    }
}
