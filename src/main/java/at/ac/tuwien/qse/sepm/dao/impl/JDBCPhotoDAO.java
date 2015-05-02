package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCPhotoDAO implements PhotoDAO {

    private Connection con;

    public JDBCPhotoDAO() throws DAOException{
        con = DBConnection.getConnection();
    }

    public Photo create(Photo p) throws DAOException {
        try {
            PreparedStatement insertStatement = con.prepareStatement("INSERT INTO Photo(photographer_id,path,date) VALUES (1,?,?);");
            insertStatement.setString(1,p.getPath());
            insertStatement.setDate(2,new java.sql.Date(p.getDate().getTime()));
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
        return null;
    }

    public Photo read(Photo p) throws DAOException {
        return null;
    }

    public void update(Photo p) throws DAOException {

    }

    public void delete(Photo p) throws DAOException {

    }

    public List<Photo> readAll() throws DAOException {
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
