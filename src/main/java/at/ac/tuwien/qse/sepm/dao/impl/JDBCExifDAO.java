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

import org.springframework.dao.DataAccessException;

import java.io.File;
import java.io.IOException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JDBCExifDAO extends JDBCDAOBase implements ExifDAO {

    private static final String insertStatement = "INSERT INTO exif(photo_id, date, exposure, aperture, focallength, iso, flash, make, model, latitude, longitude, altitude) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
    private static final String readStatement = "SELECT photo_id, date, exposure, aperture, focallength, iso, flash, make, model, latitude, longitude, altitude FROM exif WHERE photo_id=?";
    private static final String readAllStatement = "SELECT * FROM Exif ORDER BY DATE;";
    private static final String readMonthStatement = "SELECT YEAR(date), MONTH(date) from exif;";
    private static final String updateStatement = "UPDATE exif SET date=?, exposure=?, aperture=?, focallength=?, iso=?, flash=?, make=?, model=?, latitude=?, longitude=?, altitude=? WHERE photo_id=?";
    private static final String deleteStatement = "Delete from Exif where PHOTO_ID  =?";

    public Exif create(Exif exif) throws DAOException {
        logger.debug("Entering create() " + exif);
        jdbcTemplate.update(insertStatement, exif.getId(), exif.getDate(), exif.getExposure(),
                exif.getAperture(), exif.getFocalLength(), exif.getIso(), exif.isFlash(),
                exif.getMake(), exif.getModel(), exif.getLatitude(), exif.getLongitude(),
                exif.getAltitude());
        return exif;
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
                        rs.getDouble(10),
                        rs.getDouble(11),
                        rs.getDouble(12)
                );
            }, photo.getId());
        } catch (DataAccessException ex) {
            throw new DAOException("Failed to retrieve exif data for given photo", ex);
        }
    }

    public void update(Exif exif) throws DAOException {
        logger.debug("Entering update() " + exif);
        jdbcTemplate.update(updateStatement, exif.getId(), exif.getDate(), exif.getExposure(),
                exif.getAperture(), exif.getFocalLength(), exif.getIso(), exif.isFlash(),
                exif.getMake(), exif.getModel(), exif.getLatitude(), exif.getLongitude(),
                exif.getAltitude());
    }

    /**
     * delete an Exif Objekt
     * @param e the Exif Objekt
     * @throws DAOException
     */
    public void delete(Exif e) throws DAOException {
        try{
            jdbcTemplate.update(deleteStatement,e.getId());
        } catch(DataAccessException ex) {
            throw new DAOException("Failed to delete exif", ex);
        }
    }

    public List<Exif> readAll() throws DAOException {
        try {
            return jdbcTemplate.query(readAllStatement, (rs, rowNum) -> {
                return new Exif(rs.getInt(1), rs.getTimestamp(2), rs.getString(3), rs.getDouble(4),
                        rs.getDouble(5), rs.getInt(6), rs.getBoolean(7), rs.getString(8),
                        rs.getString(9), rs.getDouble(10), rs.getDouble(11), rs.getDouble(12));
            });
        } catch (DataAccessException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    @Override public Exif importExif(Photo photo) throws DAOException {
        File file = new File(photo.getPath());
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

            String tempDate = jpegMetadata
                    .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)
                    .getValueDescription();
            date = Timestamp.valueOf(this.convertSeparators(tempDate));
            exposure = jpegMetadata
                    .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME)
                    .getValueDescription().split(" ")[0];
            aperture = jpegMetadata
                    .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_APERTURE_VALUE)
                    .getDoubleValue();
            focalLength = jpegMetadata
                    .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH)
                    .getDoubleValue();
            iso = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_ISO)
                    .getIntValue();
            flash = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_FLASH)
                    .getIntValue() != 0;

            make = jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_MAKE)
                    .getValueDescription();
            model = jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_MODEL)
                    .getValueDescription();

            altitude = jpegMetadata
                    .findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_ALTITUDE)
                    .getDoubleValue();

            if (null != exifMetadata) {
                final TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
                if (null != gpsInfo) {
                    final double longitude = gpsInfo.getLongitudeAsDegreesEast();
                    final double latitude = gpsInfo.getLatitudeAsDegreesNorth();
                    Exif exif = new Exif(photo.getId(), date, exposure, aperture, focalLength, iso,
                            flash, make, model, latitude, longitude, altitude);
                    this.create(exif);
                    photo.setExif(exif);
                    return exif;
                }
            }
            throw new DAOException("Error while retrieving the GPS data");

        } catch (IOException | ImageReadException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage(), e);
        }
    }

    String convertSeparators(String input) {
        input = input.substring(1, input.length() - 2);
        char[] chars = input.toCharArray();
        chars[4] = '-';
        chars[7] = '-';
        return new String(chars);
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
