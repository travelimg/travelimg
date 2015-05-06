package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.ExifDAO;
import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCExifDAO implements ExifDAO {

    private Connection con;

    public JDBCExifDAO() throws DAOException {
        con = DBConnection.getConnection();
    }

    public Exif create(Exif e) throws DAOException {
        String query = "INSERT INTO exif(photo_id, date, exposure, aperture, focallength, iso, flash, make, model, latitude, longitude, altitude) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";

        try (PreparedStatement insertStatement = con.prepareStatement(query)) {

            insertStatement.setInt(1, e.getId());
            insertStatement.setTimestamp(2, e.getDate());
            insertStatement.setString(3, e.getExposure());
            insertStatement.setDouble(4, e.getAperture());
            insertStatement.setDouble(5, e.getFocalLength());
            insertStatement.setInt(6, e.getIso());
            insertStatement.setBoolean(7, e.isFlash());
            insertStatement.setString(8, e.getMake());
            insertStatement.setString(9, e.getModel());
            insertStatement.setDouble(10, e.getLatitude());
            insertStatement.setDouble(11, e.getLongitude());
            insertStatement.setDouble(12, e.getAltitude());
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
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Exif ORDER BY DATE;");
            while (rs.next()) {
                exifs.add(new Exif(rs.getInt(1), rs.getTimestamp(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5), rs.getInt(6), rs.getBoolean(7), rs.getString(8), rs.getString(9), rs.getDouble(10), rs.getDouble(11), rs.getDouble(12)));
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
        String make;
        String model;
        double altitude;

        try {
            final ImageMetadata metadata = Imaging.getMetadata(file);
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            final TiffImageMetadata exifMetadata = jpegMetadata.getExif();

            String tempDate = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL).getValueDescription();
            date = Timestamp.valueOf(this.convertSeparators(tempDate));
            exposure = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME).getValueDescription().split(" ")[0];
            aperture = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_APERTURE_VALUE).getDoubleValue();
            focalLength = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH).getDoubleValue();
            iso = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_ISO).getIntValue();
            flash = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_FLASH).getIntValue() != 0;

            make = jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_MAKE).getValueDescription();
            model = jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_MODEL).getValueDescription();

            altitude = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_ALTITUDE).getDoubleValue();


            if (null != exifMetadata) {
                final TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
                if (null != gpsInfo) {
                    final double longitude = gpsInfo.getLongitudeAsDegreesEast();
                    final double latitude = gpsInfo.getLatitudeAsDegreesNorth();
                    Exif exif = new Exif(p.getId(), date, exposure, aperture, focalLength, iso, flash, make, model, latitude, longitude, altitude);
                    this.create(exif);
                    p.setExif(exif);
                    return exif;
                }
            }
            throw new DAOException("Error while retrieving the GPS data");

        } catch (IOException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage(), e);
        }  catch (ImageReadException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage(), e);
        }
    }

    String convertSeparators(String input) {
        input = input.substring(1, input.length()-2);
        char[] chars = input.toCharArray();
        chars[4] = '-';
        chars[7] = '-';
        return new String(chars);
    }
}
