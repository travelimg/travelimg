package at.ac.tuwien.qse.sepm.dao.impl;


import at.ac.tuwien.qse.sepm.dao.*;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.ExifDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Exif;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.PhotoValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class JDBCPhotoDAO extends JDBCDAOBase implements PhotoDAO {

    private static final String insertStatement = "INSERT INTO Photo(id, photographer_id, path, rating) VALUES (?, ?, ?, ?);";
    private static final String readAllStatement = "SELECT id, photographer_id, path, rating FROM PHOTO;";
   // private static final String readByYearAndMonthStatement = "SELECT PHOTO_ID,PHOTOGRAPHER_ID,PATH,RATING FROM PHOTO JOIN EXIF WHERE ID=PHOTO_ID AND YEAR(DATE)=? AND MONTH(DATE)=?;";

    private static final String deleteStatement = "Delete from Photo where id =?";

    private static final String readByYearAndMonthStatement = "SELECT PHOTO_ID,PHOTOGRAPHER_ID,PATH,RATING FROM PHOTO JOIN EXIF WHERE ID=PHOTO_ID AND YEAR(DATE)=? AND MONTH(DATE)=?;";


    private final String photoDirectory;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MMM/dd", Locale.ENGLISH);

    @Autowired private ExifDAO exifDAO;
    @Autowired private PhotoTagDAO photoTagDAO;

    public JDBCPhotoDAO(String photoDirectory) {
        this.photoDirectory = photoDirectory;
    }

    @Autowired
    public void setExifDAO(ExifDAO exifDAO) {
        this.exifDAO = exifDAO;
    }
    @Autowired
    public void setPhotoTagDAO(PhotoTagDAO photoTagDAO) { this.photoTagDAO =photoTagDAO;}

    public Photo create(Photo photo) throws DAOException, ValidationException {
        logger.debug("Creating photo {}", photo);

        PhotoValidator.validate(photo);

        photo.setId(getNextId());

        // store exif data
        exifDAO.importExif(photo);

        try {
            String dest = copyToPhotoDirectory(photo);
            photo.setPath(dest);
        } catch(IOException e) {
            logger.error("Failed to copy photo to destination directory", e);
            throw new DAOException("Failed to copy photo to destination directory", e);
        }

        try {
            jdbcTemplate.update(insertStatement, photo.getId(), photo.getPhotographer().getId(), photo.getPath(), photo.getRating());

            logger.debug("Created photo {}", photo);
            return photo;
        } catch(DataAccessException e) {
            throw new DAOException("Failed to create photo", e);
        }
    }

    public void update(Photo photo) throws DAOException, ValidationException {

    }

    /**
     * delete the photo which is delivered
     * @param photo Specifies which photo to delete by providing the id.
     * @throws DAOException
     * @throws ValidationException
     */
    public void delete(Photo photo) throws DAOException, ValidationException {
        logger.debug("Deleting photo {}", photo);
        // validate photo
        //PhotoValidator.validate(photo); // disabled for IR1

        int id = photo.getId();
        // delete from Table exif
        exifDAO.delete(photo.getExif());

        // delete from Table photoTag

        List<Tag> taglist = photoTagDAO.readTagsByPhoto(photo);
        if (taglist !=null) {
            for (Tag t : taglist) {
                photoTagDAO.removeTagFromPhoto(t, photo);
            }
        }
        try{
            jdbcTemplate.update(deleteStatement, id);

        }catch(DataAccessException e) {
            throw new DAOException("Failed to delete photo", e);
        }

    }





    public List<Photo> readAll() throws DAOException, ValidationException {
        logger.debug("retrieving all photos");

        try {
            List<Photo> photos = jdbcTemplate.query(readAllStatement, new RowMapper<Photo>() {
                @Override
                public Photo mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Photo(rs.getInt(1), null, rs.getString(3), rs.getInt(4));
                }
            });

            attachExif(photos);

            logger.debug("Successfully read all photos");
            return photos;
        } catch(DataAccessException e) {
            throw new DAOException("Failed to read all photos", e);
        }
    }

    @Override
    public List<Photo> readPhotosByDate(Date date) throws DAOException {
        logger.debug("retrieving photos by date {}", date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        try {
            List<Photo> photos = jdbcTemplate.query(readByYearAndMonthStatement, (ResultSet rs, int rowNum) -> {
                return new Photo(rs.getInt(1), null, rs.getString(3), rs.getInt(4));
            }, year, month);

            attachExif(photos);

            logger.debug("Successfully retrieved photos");
            return photos;
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to read photos from given month", ex);
        }
    }

    /**
     * Load the exif data for each photo in the given list.
     *
     * @param photos The list of photos which will be annotated with the exif data.
     * @throws DAOException if an error occurs during reading the exif data.
     */
    private void attachExif(List<Photo> photos) throws DAOException {
        for(Photo photo : photos) {
            photo.setExif(exifDAO.read(photo));
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
        String date = dateFormatter.format(photo.getExif().getDate());

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

    /**
     * Return the next unused id.
     *
     * @return An integer that can be used to identify a new photo.
     * @throws DAOException if an error occurs executing the query.
     */
    private int getNextId() throws DAOException {
        try {
            return jdbcTemplate.queryForObject("select id from Photo order by id desc limit 1",
                    Integer.class) + 1;
        }  catch(IncorrectResultSizeDataAccessException e) {
            // no data in table yet
            return 0;
        } catch(DataAccessException e) {
            throw new DAOException("Failed to retrieve next id", e);
        }
    }


}
