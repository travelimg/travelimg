package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCPhotoDAO extends JDBCDAOBase implements PhotoDAO {

    private static final Logger logger = LogManager.getLogger();

    private static String insertStatement = "INSERT INTO Photo(photographer_id, path, date) VALUES (1, ?, ?);";


    public JDBCPhotoDAO() {

    }

    public Photo create(Photo photo) throws DAOException {
        Connection con = getConnection();
        try {
            try(PreparedStatement stmt = con.prepareStatement(insertStatement)) {
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
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public void update(Photo p) throws DAOException {

    }

    public void delete(Photo p) throws DAOException {

    }

    public List<Photo> readAll() throws DAOException {
        Connection con = getConnection();

        List<Photo> photos = new ArrayList<Photo>();
        try {
            ResultSet rs = con.createStatement().executeQuery("SELECT* FROM PHOTO ORDER BY DATE;");
            while(rs.next()){
                photos.add(new Photo(rs.getInt(1),null,rs.getString(3),rs.getDate(4),rs.getInt(5)));
            }
        } catch (SQLException e) {
           throw new DAOException(e.getMessage());
        }

        return photos;
    }
}
