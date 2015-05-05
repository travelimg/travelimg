package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.ExifDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.validators.PhotoValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class JDBCPhotoDAO extends JDBCDAOBase implements PhotoDAO {

    private static final Logger logger = LogManager.getLogger();

    private static final String insertStatement = "INSERT INTO Photo(id, photographer_id, path, date, rating) VALUES (?, ?, ?, ?, ?);";
    private static final String readAllStatement = "SELECT* FROM PHOTO ORDER BY DATE;";

    private final String photoDirectory;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MMM/dd");

    private ExifDAO exifDAO;

    public JDBCPhotoDAO(String photoDirectory) {
        this.photoDirectory = photoDirectory;
        logger.debug(photoDirectory);

        // TODO
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
            stmt.setDate(4, new java.sql.Date(photo.getExif().getDate().getTime()));
            stmt.setInt(5, photo.getRating());

            int affectedRows = stmt.executeUpdate();

            if(affectedRows != 1) {
                logger.error("Failed to create photo {}", photo);
                throw new DAOException("Failed to create photo");
            }

            logger.info("Created photo {}", photo);

            return photo;
        } catch (SQLException e) {
            throw new DAOException("Failed to create photo", e);
        }
    }

    public void update(Photo photo) throws DAOException, ValidationException {

    }

    public void delete(Photo photo) throws DAOException, ValidationException {

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
                        rs.getDate(4),
                        rs.getInt(5)
                ));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }

        return photos;
    }

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
