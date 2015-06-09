package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.FormatException;
import at.ac.tuwien.qse.sepm.dao.repo.PersistenceException;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoMetaData;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoSerializer;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JpegSerializer implements PhotoSerializer {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

    @Override public PhotoMetaData read(InputStream is) throws PersistenceException {
        if (is == null) throw new IllegalArgumentException();
        LOGGER.debug("reading metadata");

        ImageMetadata imageData;
        try {
            imageData = Imaging.getMetadata(is, null);
        } catch (ImageReadException | IOException ex) {
            LOGGER.warn("failed reading metadata");
            throw new FormatException(ex);
        }

        PhotoMetaData result = new PhotoMetaData();
        if (imageData == null) {
            LOGGER.debug("could not find image metadata");
            return result;
        }

        if (!(imageData instanceof JpegImageMetadata)) {
            LOGGER.debug("metadata is of unknown type");
            return result;
        }

        JpegImageMetadata jpegData = (JpegImageMetadata)imageData;
        readDate(jpegData, result);
        readGps(jpegData, result);
        // TODO: read rest of data

        return result;
    }

    @Override public void update(InputStream is, OutputStream os, PhotoMetaData metadata) throws PersistenceException {
        if (is == null) throw new IllegalArgumentException();
        if (os == null) throw new IllegalArgumentException();
        if (metadata == null) throw new IllegalArgumentException();
        LOGGER.debug("writing to stream metadata {}", metadata);


    }

    private void readDate(JpegImageMetadata input, PhotoMetaData output) {
        LOGGER.debug("reading date from metadata");
        TiffField field = input.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
        if (field == null) {
            LOGGER.debug("metadata contains no date");
            return;
        }
        String dateString = field.getValueDescription();
        dateString = dateString.substring(1, dateString.length() - 1); // remove enclosing single quotes
        output.setDate(DATE_FORMATTER.parse(dateString, LocalDateTime::from));
        LOGGER.debug("read date as {}", output.getDate());
    }

    private void readGps(JpegImageMetadata input, PhotoMetaData output) {
        try {
            TiffImageMetadata tiffData = input.getExif();
            if (tiffData == null) {
                LOGGER.debug("failed reading GPS since metadata contains no Exif");
                return;
            }
            TiffImageMetadata.GPSInfo gps = input.getExif().getGPS();
            if (gps == null) {
                LOGGER.debug("metadata contains no GPS");
                return;
            }
            output.setLatitude(gps.getLatitudeAsDegreesNorth());
            output.setLongitude(gps.getLongitudeAsDegreesEast());
            LOGGER.debug("read GPS as longitude {} and latitude {}", output.getLongitude(), output.getLatitude());
        } catch (ImageReadException ex) {
            LOGGER.warn("failed reading GPS data");
        }
    }
}
