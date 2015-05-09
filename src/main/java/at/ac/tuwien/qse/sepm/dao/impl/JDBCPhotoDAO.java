package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.PhotoValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JDBCPhotoDAO extends JDBCDAOBase implements PhotoDAO {

    private static final Logger logger = LogManager.getLogger();

    private static final String insertStatement = "INSERT INTO Photo(id, photographer_id, path, rating) VALUES (?, ?, ?, ?);";
    private static final String readAllStatement = "SELECT id, photographer_id, path, rating FROM PHOTO;";

    private static final String deleteStatement = "Delete from Photo where id =?";

    private static final String readByYearAndMonthStatement = "SELECT PHOTO_ID,PHOTOGRAPHER_ID,PATH,RATING FROM PHOTO JOIN EXIF WHERE ID=PHOTO_ID AND YEAR(DATE)=? AND MONTH(DATE)=?;";


    private final String photoDirectory;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MMM/dd", Locale.ENGLISH);

    private ExifDAO exifDAO;
    private PhotoTagDAO photoTagDAO;

    public JDBCPhotoDAO(String photoDirectory) {
        this.photoDirectory = photoDirectory;

        // TODO: Spring
        try {
            this.exifDAO = new JDBCExifDAO();
        } catch(DAOException e) {

        }
    }

    public Photo create(Photo photo) throws DAOException, ValidationException {
        logger.debug("Creating photo {}", photo);

        PhotoValidator.validate(photo);

        photo.setId(getNextId());

        // store exif data
        exifDAO.importExif(photo);

        try(PreparedStatement stmt = getConnection().prepareStatement(insertStatement)) {
            try {
                String dest = copyToPhotoDirectory(photo);
                photo.setPath(dest);
            } catch(IOException e) {
                logger.error("Failed to copy photo to destination directory", e);
                throw new DAOException("Failed to copy photo to destination directory", e);
            }

            stmt.setInt(1, photo.getId());
            stmt.setInt(2, photo.getPhotographer().getId());
            stmt.setString(3, photo.getPath());
            stmt.setInt(4, photo.getRating());

            int affectedRows = stmt.executeUpdate();

            if(affectedRows != 1) {
                logger.error("Failed to create photo {}", photo);
                throw new DAOException("Failed to create photo");
            }

            logger.debug("Created photo {}", photo);

            return photo;
        } catch (SQLException e) {
            throw new DAOException("Failed to create photo", e);
        }
    }

    public void update(Photo photo) throws DAOException, ValidationException {

    }

    /**
     * 
     * @param photo Specifies which photo to delete by providing the id.
     * @throws DAOException
     * @throws ValidationException
     */
    public void delete(Photo photo) throws DAOException, ValidationException {
        logger.debug("Deleting photo {}", photo);
        // validate photo
        PhotoValidator.validate(photo);

        int id = photo.getId();
        // delete from Table exif
        exifDAO.delete(photo.getExif());

        // delete from Table photoTag
        List<Tag> taglist = photoTagDAO.readTagsByPhoto(photo);
        for (Tag t : taglist) {
            photoTagDAO.removeTagFromPhoto(t, photo);
        }

        try (PreparedStatement stmt = getConnection().prepareStatement(deleteStatement)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to delete photo", e);
        }
    }


        public List<Photo> readAll() throws DAOException, ValidationException {
        List<Photo> photos = new ArrayList<Photo>();
        try(Statement stmt = getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(readAllStatement);

            while(rs.next()) {
                photos.add(new Photo(
                        rs.getInt(1),
                        null,
                        rs.getString(3),
                        rs.getInt(4)
                ));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }

        return photos;
    }

    @Override
    public List<Photo> readPhotosByYearAndMonth(int year, int month) throws DAOException {
        List<Photo> photos = new ArrayList<Photo>();
        try(PreparedStatement stmt = getConnection().prepareStatement(readByYearAndMonthStatement)) {

            stmt.setInt(1,year);
            stmt.setInt(2,month);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                photos.add(new Photo(
                        rs.getInt(1),
                        null,
                        rs.getString(3),
                        rs.getInt(4)
                ));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }

        return photos;
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
        try(Statement stmt = getConnection().createStatement()) {
            ResultSet result = stmt.executeQuery("select id from Photo order by id desc limit 1");

            if(!result.next()) // no data available
                return 0;

            return result.getInt(1) + 1;
        } catch(SQLException e) {
            throw new DAOException("Failed to retrieve next id", e);
        }
    }


}
