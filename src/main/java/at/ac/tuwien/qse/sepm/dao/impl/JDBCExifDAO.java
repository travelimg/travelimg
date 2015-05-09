package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.ExifDAO;
import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import org.springframework.dao.DataAccessException;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class JDBCExifDAO extends JDBCDAOBase implements ExifDAO {

    private Connection con;

    private static final String readStatement = "SELECT photo_id, date, exposure, aperture, focallength, iso, flash, cameramodel, longitude, latitude, altitude FROM exif WHERE photo_id=?";
    private static final String readMonthStatement = "SELECT YEAR(date), MONTH(date) from exif;";

    public JDBCExifDAO() throws DAOException {
        con = DBConnection.getConnection();
    }

    public Exif create(Exif e) throws DAOException {
        String query = "INSERT INTO exif(photo_id, date, exposure, aperture, focallength, iso, flash, cameramodel, longitude, latitude, altitude) VALUES (?,?,?,?,?,?,?,?,?,?,?);";

        try(PreparedStatement insertStatement = con.prepareStatement(query)) {

            insertStatement.setInt(1, e.getId());
            insertStatement.setTimestamp(2, e.getDate());
            insertStatement.setString(3, e.getExposure());
            insertStatement.setDouble(4, e.getAperture());
            insertStatement.setDouble(5, e.getFocalLength());
            insertStatement.setInt(6, e.getIso());
            insertStatement.setBoolean(7, e.isFlash());
            insertStatement.setString(8, e.getCameraModel());
            insertStatement.setString(9, e.getLongitude());
            insertStatement.setString(10, e.getLatitude());
            insertStatement.setDouble(11, e.getAltitude());
            insertStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DAOException(ex.getMessage());
        }
        return null;
    }

    public Exif read(Photo photo) throws DAOException {
        logger.debug("Reading exif data for photo {}", photo);

        try {
            return jdbcTemplate.queryForObject(readStatement, (rs, rowNum) -> {
                return new Exif(
                        rs.getInt(1),
                        rs.getTimestamp(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getDouble(5),
                        rs.getInt(6),
                        rs.getBoolean(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getDouble(11)
                );
            }, photo.getId());
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to retrieve exif data for given photo", ex);
        }
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
                exifs.add(new Exif(rs.getInt(1), rs.getTimestamp(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5), rs.getInt(6), rs.getBoolean(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getDouble(11)));
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
        return exifs;
    }

    @Override
    public Exif importExif(Photo p) throws DAOException {
        File file = new File(p.getPath());
        Timestamp date;
        String exposure;
        double aperture;
        double focalLength;
        int iso;
        boolean flash;
        String cameraModel;
        Rational[] longitude;
        String longitudeString;
        Rational[] latitude;
        String latitudeString;
        double altitude = 0;
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            Directory subIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            Directory iFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            Directory gPSDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            try {

                date = new Timestamp(subIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL).getTime());
                exposure = subIFDDirectory.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
                aperture = subIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_APERTURE);
                focalLength = subIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_FOCAL_LENGTH);
                iso = subIFDDirectory.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
                flash = subIFDDirectory.getBoolean(ExifSubIFDDirectory.TAG_FLASH);
                cameraModel = iFD0Directory.getString(ExifIFD0Directory.TAG_MAKE) + " " + iFD0Directory.getString(ExifIFD0Directory.TAG_MODEL);

                longitude = gPSDirectory.getRationalArray(GpsDirectory.TAG_LONGITUDE);
                latitude = gPSDirectory.getRationalArray(GpsDirectory.TAG_LATITUDE);
                altitude = gPSDirectory.getDouble(GpsDirectory.TAG_ALTITUDE);

                if(longitude == null || latitude == null) {
                    throw new MetadataException("No longitude/latitude data set");
                }
                double longitudeHours = longitude[0].doubleValue();
                double longitudeMinutes = longitude[1].doubleValue();
                double longitudeSeconds = (longitude[1].doubleValue() - Math.floor(longitude[1].doubleValue())) * 60;
                longitudeString = longitudeHours + " " + longitudeMinutes + " " + longitudeSeconds + " " + gPSDirectory.getString(GpsDirectory.TAG_LONGITUDE_REF);

                double latitudeHours = latitude[0].doubleValue();
                double latitudeMinutes = latitude[1].doubleValue();
                double latitudeSeconds = (latitude[1].doubleValue() - Math.floor(latitude[1].doubleValue())) * 60;
                latitudeString = latitudeHours + " " + latitudeMinutes + " " + latitudeSeconds + " " + gPSDirectory.getString(GpsDirectory.TAG_LATITUDE_REF);




                Exif exif = new Exif(p.getId(), date, exposure, aperture, focalLength, iso, flash, cameraModel, longitudeString, latitudeString, altitude);
                this.create(exif);
                p.setExif(exif);
                return exif;
            } catch (MetadataException e) {
                e.printStackTrace();
                throw new DAOException(e.getMessage(), e);
            }
        } catch (ImageProcessingException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage(), e);
        }
    }

    @Override
    public List<Date> getMonthsWithPhotos() throws DAOException {
        try {
            return jdbcTemplate.query(readMonthStatement, (rs, rowNum) -> {
                return new Date(rs.getInt(1) - 1900, rs.getInt(2) - 1, 1);
            }).stream()
                    .distinct()
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to retrieve all months", ex);
        }
    }
}
