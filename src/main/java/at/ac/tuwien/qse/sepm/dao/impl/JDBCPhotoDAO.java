package at.ac.tuwien.qse.sepm.dao.impl;


import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.dao.PhotographerDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Tag;

import at.ac.tuwien.qse.sepm.entities.Rating;

import at.ac.tuwien.qse.sepm.entities.validators.PhotoValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JDBCPhotoDAO extends JDBCDAOBase implements PhotoDAO {

    private static final String READ_ALL_STATEMENT = "SELECT id, photographer_id, path, rating, date, latitude, longitude FROM PHOTO;";
    private static final String DELETE_STATEMENT = "Delete from Photo where id =?";
    private static final String READ_BY_YEAR_AND_MONTH_STATEMENT = "SELECT id, photographer_id, path, rating, date, latitude, longitude FROM PHOTO WHERE YEAR(DATE)=? AND MONTH(DATE)=?;";
    private static final String READ_MONTH_STATEMENT = "SELECT YEAR(date), MONTH(date) from Photo;";
    private static final String GET_BY_ID_STATEMENT = "SELECT id, photographer_id, path, rating, date, latitude, longitude FROM Photo where id=?";
    private static final String UPDATE_STATEMENT = "UPDATE Photo SET path = ?, rating = ? WHERE id = ?";


    private final String photoDirectory;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH);
    private SimpleJdbcInsert insertPhoto;

    @Autowired private PhotoTagDAO photoTagDAO;
    @Autowired private PhotographerDAO photographerDAO;

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
        parameters.put("path",photo.getPath());
        parameters.put("rating",photo.getRating().ordinal());
        parameters.put("date", Date.valueOf(photo.getDate()));
        parameters.put("latitude", photo.getLatitude());
        parameters.put("longitude", photo.getLongitude());

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
        if (photo == null) throw new IllegalArgumentException();
        logger.debug("Updating photo {}", photo);
        try {
            jdbcTemplate.update(UPDATE_STATEMENT,
                    // TODO: Also update photographer ID.
                    // This is currently not viable since all the read* methods just set the
                    // photographer to null.
                    photo.getPath(),
                    photo.getRating().ordinal(),
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

        List<Tag> taglist = photoTagDAO.readTagsByPhoto(photo);
        if (taglist != null) {
            for (Tag t : taglist) {
                photoTagDAO.removeTagFromPhoto(photo, t);
            }
        }

        try {
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
    public List<Photo> readAll() throws DAOException, ValidationException {
        logger.debug("retrieving all photos");

        try {
            List<Photo> photos = jdbcTemplate.query(READ_ALL_STATEMENT, new PhotoRowMapper());

            logger.debug("Successfully read all photos");
            return photos;
        } catch (DataAccessException e) {
            throw new DAOException("Failed to read all photos", e);
        } catch (RuntimeException ex) {
            throw new DAOException(ex.getCause());
        }
    }

    @Override
    public List<Photo> readPhotosByMonth(YearMonth month) throws DAOException {
        logger.debug("retrieving photos for monthh {}", month);

        try {
            List<Photo> photos = jdbcTemplate.query(READ_BY_YEAR_AND_MONTH_STATEMENT,
                    new PhotoRowMapper(), month.getYear(), month.getMonth().getValue()
            );

            logger.debug("Successfully retrieved photos");
            return photos;
        } catch (DataAccessException ex) {
            logger.error("Failed to read photos from given month", ex);
            throw new DAOException("Failed to read photos from given month", ex);
        } catch (RuntimeException ex) {
            logger.error("Failed to read photos from given month", ex);
            throw new DAOException("Failed to read photos from given month", ex.getCause());
        }
    }

    @Override
    public List<YearMonth> getMonthsWithPhotos() throws DAOException {

        try {
            return jdbcTemplate.query(READ_MONTH_STATEMENT, (rs, rowNum) -> {
                    return YearMonth.of(rs.getInt(1), rs.getInt(2));
            }).stream()
                    .distinct()
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to retrieve all months", ex);
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
        String filename = source.getName();
        String date = dateFormatter.format(photo.getDate());

        Path path = Paths.get(photoDirectory, date, filename);
        File dest = path.toFile();

        // create directory structure
        Paths.get(photoDirectory, date).toFile().mkdirs();

        if(source.getPath().equals(dest.getPath()))
            return photo.getPath();

        logger.debug("Copying {} to {}", source.getPath(), dest.getPath());

        Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return dest.getPath();
    }

    private class PhotoRowMapper implements RowMapper<Photo> {
        @Override
        public Photo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Photographer photographer;
            try {
                photographer = photographerDAO.getById(rs.getInt(2));
            } catch (DAOException ex) {
                throw new RuntimeException(ex);
            }

            Rating rating = Rating.from(rs.getInt(4));

            return new Photo(rs.getInt(1),
                    photographer,
                    rs.getString(3),
                    rating,
                    rs.getTimestamp(5).toLocalDateTime().toLocalDate(),
                    rs.getDouble(6),
                    rs.getDouble(7)
            );
        }
    }
}
