package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.validators.PhotoValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCPhotoDAO extends JDBCDAOBase implements PhotoDAO {

    private static final Logger logger = LogManager.getLogger();

    private static final String insertStatement = "INSERT INTO Photo(photographer_id, path, date) VALUES (1, ?, ?);";
    private static final String readAllStatement = "SELECT* FROM PHOTO ORDER BY DATE;";

    public JDBCPhotoDAO() {

    }

    public Photo create(Photo photo) throws DAOException, ValidationException {
        PhotoValidator.validate(photo);

        try(PreparedStatement stmt = getConnection().prepareStatement(insertStatement)) {
            stmt.setString(1, photo.getPath());
            stmt.setDate(2, new java.sql.Date(photo.getDate().getTime()));

            int affectedRows = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();

            if(affectedRows != 1 || !rs.next()) {
                logger.error("Failed to create photo {}", photo);
                throw new DAOException("Failed to create photo");
            }

            int id = rs.getInt(1);
            photo.setId(id);

            logger.info("Created photo {}", photo);

            return photo;
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
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
}
