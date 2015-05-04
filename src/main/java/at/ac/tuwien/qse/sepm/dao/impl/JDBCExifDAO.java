package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.ExifDAO;
import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCExifDAO implements ExifDAO {

    private Connection con;

    public JDBCExifDAO() throws DAOException {
        con = DBConnection.getConnection();
    }

    public Exif create(Exif e) throws DAOException {
        try {
            PreparedStatement insertStatement = con.prepareStatement("INSERT INTO exif(photo_id, date, exposure, aperture, focallength, iso, flash, cameramodel, longitude, latitude, altitude) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
            insertStatement.setInt(1, e.getId());
            insertStatement.setTimestamp(2, e.getDate());
            insertStatement.setDouble(3, e.getExposure());
            insertStatement.setDouble(4, e.getAperture());
            insertStatement.setDouble(5, e.getFocalLength());
            insertStatement.setInt(6, e.getIso());
            insertStatement.setBoolean(7, e.isFlash());
            insertStatement.setString(8, e.getCameraModel());
            insertStatement.setDouble(9, e.getLongitude());
            insertStatement.setDouble(10, e.getLatitude());
            insertStatement.setDouble(11, e.getAltitude());
            insertStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DAOException(ex.getMessage());
        }
        return null;
    }

    public Exif read(Exif e) throws DAOException {
        return null;
    }

    public void update(Exif e) throws DAOException {

    }

    public void delete(Exif e) throws DAOException {

    }

    public List<Exif> readAll() throws DAOException {
        List<Exif> exifs = new ArrayList<Exif>();
        try {
            ResultSet rs = con.createStatement().executeQuery("SELECT* FROM Exif ORDER BY DATE;");
            while(rs.next()){
                exifs.add(new Exif(rs.getInt(1), rs.getTimestamp(2), rs.getDouble(3), rs.getDouble(4), rs.getDouble(5), rs.getInt(6), rs.getBoolean(7), rs.getString(8), rs.getDouble(9), rs.getDouble(10), rs.getDouble(11)));
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
        return exifs;
    }
}
