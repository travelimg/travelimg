package at.ac.tuwien.qse.sepm.dao.impl;


import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class JDBCPhotoDAO extends JDBCDAOBase implements PhotoDAO {

    private static final String readAllStatement = "SELECT id, photographer_id, path, rating, date, latitude, longitude FROM PHOTO;";
    private static final String deleteStatement = "Delete from Photo where id =?";
    private static final String readByYearAndMonthStatement = "SELECT id, photographer_id, path, rating, date, latitude, longitude FROM PHOTO WHERE YEAR(DATE)=? AND MONTH(DATE)=?;";
    private static final String readMonthStatement = "SELECT YEAR(date), MONTH(date) from Photo;";

    private final String photoDirectory;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH);
    private SimpleJdbcInsert insertPhoto;

    @Autowired private PhotoTagDAO photoTagDAO;

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



    public Photo create(Photo photo) throws DAOException, ValidationException {
        logger.debug("Creating photo {}", photo);

        try {
            String dest = copyToPhotoDirectory(photo);
            photo.setPath(dest);
            PhotoValidator.validate(photo);

            Map<String, Object> parameters = new HashMap<String, Object>(1);
            parameters.put("photographer_id", photo.getPhotographer().getId());
            parameters.put("path",photo.getPath());
            parameters.put("rating",photo.getRating());
            parameters.put("date", Date.valueOf(photo.getDate()));
            parameters.put("latitude", photo.getLatitude());
            parameters.put("longitude", photo.getLongitude());
            Number newId = insertPhoto.executeAndReturnKey(parameters);
            photo.setId((int) newId.longValue());
            return photo;
        } catch(IOException e) {
            logger.error("Failed to copy photo to destination directory", e);
            throw new DAOException("Failed to copy photo to destination directory", e);
        }
    }

    public void update(Photo photo) throws DAOException, ValidationException {

    }

    public void delete(Photo photo) throws DAOException, ValidationException {
        logger.debug("Deleting photo {}", photo);
        // validate photo
        //PhotoValidator.validate(photo); // disabled for IR1

        int id = photo.getId();

        // delete from Table photoTag

        //TODO maybe the JDBCPhotoTagDAO has errors, like a parameter not set...
        /*List<Tag> taglist = photoTagDAO.readTagsByPhoto(photo);
        if (taglist !=null) {
            for (Tag t : taglist) {
                photoTagDAO.removeTagFromPhoto(photo, t);
            }
        }*/
        try {
            jdbcTemplate.update(deleteStatement, id);

        } catch (DataAccessException e) {
            throw new DAOException("Failed to delete photo", e);
        }

    }

    public List<Photo> readAll() throws DAOException, ValidationException {
        logger.debug("retrieving all photos");

        try {
            List<Photo> photos = jdbcTemplate.query(readAllStatement, new RowMapper<Photo>() {
                @Override
                public Photo mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Photo photo = new Photo(rs.getInt(1), null, rs.getString(3), rs.getInt(4),rs.getTimestamp(5).toLocalDateTime().toLocalDate(),rs.getDouble(6),rs.getDouble(7));
                    return photo;
                }
            });

            logger.debug("Successfully read all photos");
            return photos;
        } catch(DataAccessException e) {
            throw new DAOException("Failed to read all photos", e);
        }
    }

    @Override
    public List<Photo> readPhotosByMonth(YearMonth month) throws DAOException {
        logger.debug("retrieving photos for monthh {}", month);

        try {
            List<Photo> photos = jdbcTemplate.query(readByYearAndMonthStatement, (ResultSet rs, int rowNum) -> {
                Photo photo = new Photo(rs.getInt(1), null, rs.getString(3), rs.getInt(4),rs.getTimestamp(5).toLocalDateTime().toLocalDate(),rs.getDouble(6),rs.getDouble(7));
                return photo;
            }, month.getYear(), month.getMonth().getValue());

            logger.debug("Successfully retrieved photos");
            return photos;
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to read photos from given month", ex);
        }
    }

    @Override
    public List<YearMonth> getMonthsWithPhotos() throws DAOException {
        try {
            return jdbcTemplate.query(readMonthStatement, (rs, rowNum) -> {
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



}
