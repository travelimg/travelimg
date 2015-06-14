package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.entities.validators.PhotoValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.util.IOHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
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

    private static final String READ_ALL_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id FROM PHOTO;";
    private static final String DELETE_STATEMENT = "Delete from Photo where id =?";
    private static final String GET_BY_ID_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id FROM Photo where id=?";
    private static final String GET_BY_FILE_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id FROM Photo where path=?";
    private static final String UPDATE_STATEMENT = "UPDATE Photo SET path = ?, rating = ?, place_id = ? WHERE id = ?";
    private static final String READ_JOURNEY_STATEMENT = "SELECT id, photographer_id, path, rating, datetime, latitude, longitude, place_id FROM PHOTO WHERE datetime>=? AND datetime<=? ORDER BY datetime ASC";

    private final String photoDirectory;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH);
    private SimpleJdbcInsert insertPhoto;

    @Autowired
    private PhotoTagDAO photoTagDAO;
    @Autowired
    private PhotographerDAO photographerDAO;
    @Autowired
    private PlaceDAO placeDAO;
    @Autowired
    private IOHandler ioHandler;

    public JDBCPhotoDAO(String photoDirectory) {
        this.photoDirectory = photoDirectory;
    }

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

        try {
            String dest = copyToPhotoDirectory(photo);
            photo.setPath(dest);
        } catch (IOException ex) {
            logger.error("Failed to copy photo to destination directory", ex);
            throw new DAOException("Failed to copy photo to destination directory", ex);
        }

        PhotoValidator.validate(photo);

        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("photographer_id", photo.getPhotographer().getId());
        parameters.put("path", photo.getPath());
        parameters.put("rating", photo.getRating().ordinal());
        parameters.put("datetime", Timestamp.valueOf(photo.getDatetime()));
        parameters.put("latitude", photo.getLatitude());
        parameters.put("longitude", photo.getLongitude());
        parameters.put("place_id", photo.getPlace().getId());

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
                    photo.getRating().ordinal(),
                    photo.getPlace().getId(),
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
            int affected = jdbcTemplate.update(DELETE_STATEMENT, id);
            photoTagDAO.deleteAllEntriesOfSpecificPhoto(photo);
            jdbcTemplate.update(DELETE_STATEMENT, id);

            if (affected != 1) {
                throw new DAOException("Could not delete photo");
            }
        } catch (DataAccessException e) {
            throw new DAOException("Failed to delete photo", e);
        }

        try {
            ioHandler.delete(Paths.get(photo.getPath()));
        } catch (IOException ex) {
            logger.error("Failed to delete photo", ex);
            throw new DAOException("Failed to delete photo", ex);
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
            List<Photo> photos = jdbcTemplate.query(READ_JOURNEY_STATEMENT,
                    new PhotoRowMapper(), Timestamp.valueOf(journey.getStartDate()), Timestamp.valueOf(journey.getEndDate()));

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

    /**
     * Copy the photo to the travelimg photo directory. The structure created is Year/Month/Day.
     *
     * @param photo The photo to copy.
     * @return The destination path of the copied photo.
     * @throws IOException If an error occurs during copying.
     */
    private String copyToPhotoDirectory(Photo photo) throws IOException {
        File source = new File(photo.getPath());

        if (!source.exists()) {
            throw new IOException("File " + source.getPath() + " does not exist");
        }

        String filename = source.getName();
        String date = dateFormatter.format(photo.getDatetime());

        Path path = Paths.get(photoDirectory, date, filename);
        File dest = path.toFile();

        // create directory structure
        Paths.get(photoDirectory, date).toFile().mkdirs();

        if (source.getPath().equals(dest.getPath()))
            return photo.getPath();

        ioHandler.copyFromTo(source.toPath(), dest.toPath());

        return dest.getPath();
    }

    private class PhotoRowMapper implements RowMapper<Photo> {
        @Override
        public Photo mapRow(ResultSet rs, int rowNum) throws SQLException {

            Photo photo = new Photo();
            photo.setId(rs.getInt(1));
            photo.setPath(rs.getString(3));
            photo.setRating(Rating.from(rs.getInt(4)));
            photo.setDatetime(rs.getTimestamp(5).toLocalDateTime());
            photo.setLatitude(rs.getDouble(6));
            photo.setLongitude(rs.getDouble(7));

            try {
                int placeId = rs.getInt(8);
                Place place = placeDAO.getById(placeId);
                photo.setPlace(place);
            } catch (DAOException ex) {
                throw new DAOException.Unchecked(ex);
            } catch (ValidationException ex) {
                throw new ValidationException.Unchecked(ex);
            }

            try {
                int photographerId = rs.getInt(2);
                Photographer photographer = photographerDAO.getById(photographerId);
                photo.setPhotographer(photographer);
            } catch (DAOException ex) {
                throw new DAOException.Unchecked(ex);
            }

            try {
                List<Tag> tags = photoTagDAO.readTagsByPhoto(photo);
                photo.getTags().addAll(tags);
            } catch (DAOException ex) {
                throw new DAOException.Unchecked(ex);
            } catch (ValidationException ex) {
                throw new ValidationException.Unchecked(ex);
            }

            return photo;
        }
    }
}
