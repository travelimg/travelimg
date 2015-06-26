package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCPhotoDAO extends JDBCDAOBase implements PhotoDAO {

    private static final String READ_ALL_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id FROM PHOTO;";
    private static final String READ_ALL_PATHS_STATEMENT = "SELECT path FROM PHOTO;";
    private static final String DELETE_STATEMENT = "Delete from Photo where id =?";
    private static final String GET_BY_ID_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id FROM Photo where id=?";
    private static final String GET_BY_FILE_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id FROM Photo where path=?";
    private static final String UPDATE_STATEMENT = "UPDATE Photo SET photographer_id=?, path=?, rating=?, datetime=?, latitude=?, longitude=?, place_id=?, journey_id=? WHERE id = ?";
    private static final String READ_JOURNEY_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id FROM PHOTO WHERE journey_id=? ORDER BY datetime ASC";
    private static final String READ_INTERVAL_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id FROM PHOTO WHERE datetime>=? AND datetime<=? ORDER BY datetime ASC";

    private SimpleJdbcInsert insertPhoto;

    @Autowired
    private PhotoTagDAO photoTagDAO;
    @Autowired
    private PhotographerDAO photographerDAO;
    @Autowired
    private PlaceDAO placeDAO;
    @Autowired
    private JourneyDAO journeyDAO;
    @Autowired
    private SlideDAO slideDAO;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.insertPhoto = new SimpleJdbcInsert(dataSource)
                .withTableName("Photo")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Photo create(Photo photo) throws DAOException {
        if (photo == null) throw new IllegalArgumentException();
        logger.debug("Creating photo {}", photo);

        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("path", photo.getPath());
        parameters.put("rating", photo.getData().getRating().ordinal());
        parameters.put("datetime", Timestamp.valueOf(photo.getData().getDatetime()));
        parameters.put("latitude", photo.getData().getLatitude());
        parameters.put("longitude", photo.getData().getLongitude());

        Place place = photo.getData().getPlace();
        parameters.put("place_id", null);
        if (place != null) {
            parameters.put("place_id", place.getId());
        }

        Journey journey = photo.getData().getJourney();
        parameters.put("journey_id", null);
        if (journey != null) {
            parameters.put("journey_id", journey.getId());
        }

        Photographer photographer = photo.getData().getPhotographer();
        parameters.put("photographer_id", null);
        if (photographer != null) {
            parameters.put("photographer_id", photographer.getId());
        }

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
    public void update(Photo photo) throws DAOException {
        if (photo == null) throw new IllegalArgumentException();
        if (photo.getId() == null) throw new IllegalArgumentException();
        logger.debug("Updating photo {}", photo);

        try {
            Place place = photo.getData().getPlace();
            Journey journey = photo.getData().getJourney();
            Photographer photographer = photo.getData().getPhotographer();
            jdbcTemplate.update(UPDATE_STATEMENT,
                    photographer != null ? photographer.getId() : null,
                    photo.getPath(),
                    photo.getData().getRating().ordinal(),
                    Timestamp.valueOf(photo.getData().getDatetime()),
                    photo.getData().getLatitude(),
                    photo.getData().getLongitude(),
                    place != null ? place.getId() : null,
                    journey != null ? journey.getId() : null,
                    photo.getId());
            logger.debug("Successfully update photo {}", photo);
        } catch (DataAccessException e) {
            logger.debug("Failed updating photo {}", photo);
            throw new DAOException("Failed to update photo", e);
        }
    }

    @Override
    public void delete(Photo photo) throws DAOException {
        if (photo == null) throw new IllegalArgumentException();
        if (photo.getId() == null) throw new IllegalArgumentException();
        logger.debug("Deleting photo {}", photo);

        try {
            int id = photo.getId();
            photoTagDAO.deleteAllEntriesOfSpecificPhoto(photo);
            slideDAO.deleteAllSlidesWithPhoto(photo);
            jdbcTemplate.update(DELETE_STATEMENT, id);
        } catch (DataAccessException e) {
            throw new DAOException("Failed to delete photo", e);
        }
    }

    @Override
    public Photo getById(int id) throws DAOException {
        logger.debug("Get photo with id {}", id);

        try {
            return this.jdbcTemplate.queryForObject(GET_BY_ID_STATEMENT, new Object[]{id}, new PhotoRowMapper());
        } catch (DataAccessException ex) {
            logger.error("Failed to get photo", ex);
            throw new DAOException("Failed to get photo", ex);
        }
    }

    @Override
    public Photo getByFile(Path file) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        logger.debug("Get photo with path {}", file);

        try {
            return jdbcTemplate.queryForObject(GET_BY_FILE_STATEMENT, new Object[]{file.toString()}, new PhotoRowMapper());
        } catch (DataAccessException ex) {
            logger.error("Failed to get photo");
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
    public List<Path> readAllPaths() throws DAOException {
        logger.debug("retrieving all paths");

        try {
            List<Path> paths = jdbcTemplate.query(READ_ALL_PATHS_STATEMENT, (rs, rowNum) -> {
                return Paths.get(rs.getString(1));
            });
            logger.debug("Successfully read all paths: " + paths.size());
            return paths;
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to read all paths");
        }
    }

    @Override
    public List<Photo> readPhotosByJourney(Journey journey) throws DAOException {
        if (journey == null) throw new IllegalArgumentException();
        if (journey.getId() == null) throw new IllegalArgumentException();
        logger.debug("retrieving photos for journey {}", journey);

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

    @Override
    public List<Photo> readPhotosBetween(LocalDateTime start, LocalDateTime end) throws DAOException {
        if (start == null) throw new IllegalArgumentException();
        if (end == null) throw new IllegalArgumentException();
        logger.debug("retrieving photos between {} and {}", start, end);

        try {
            List<Photo> photos = jdbcTemplate.query(READ_INTERVAL_STATEMENT,
                    new PhotoRowMapper(), Timestamp.valueOf(start), Timestamp.valueOf(end)
            );
            logger.debug("Successfully retrieved photos");
            return photos;
        } catch (DataAccessException ex) {
            logger.error("Failed to read photos from given interval", ex);
            throw new DAOException("Failed to read photos from given interval", ex);
        } catch (ValidationException.Unchecked | DAOException.Unchecked ex) {
            logger.error("Failed to read photos from given interval", ex);
            throw new DAOException("Failed to read photos from given interval", ex.getCause());
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
                if (placeId != 0) {
                    Place place = placeDAO.getById(placeId);
                    photo.getData().setPlace(place);
                }
            } catch (DAOException ex) {
                throw new DAOException.Unchecked(ex);
            } catch (ValidationException ex) {
                throw new ValidationException.Unchecked(ex);
            }

            try {
                int journeyId = rs.getInt(9);
                if (journeyId != 0) {
                    Journey journey = journeyDAO.getByID(journeyId);
                    photo.getData().setJourney(journey);
                }
            } catch (DAOException ex) {
                throw new DAOException.Unchecked(ex);
            } catch (ValidationException ex) {
                throw new ValidationException.Unchecked(ex);
            }

            try {
                int photographerId = rs.getInt(2);
                if (photographerId != 0) {
                    Photographer photographer = photographerDAO.getById(photographerId);
                    photo.getData().setPhotographer(photographer);
                }
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
