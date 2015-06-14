package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.repo.FormatException;
import at.ac.tuwien.qse.sepm.entities.PhotoMetadata;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoSerializer;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
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

    @Override public PhotoMetadata read(InputStream is) throws DAOException {
        if (is == null) throw new IllegalArgumentException();
        LOGGER.debug("reading metadata");

        ImageMetadata imageData;
        try {
            imageData = Imaging.getMetadata(is, null);
        } catch (ImageReadException | IOException ex) {
            LOGGER.warn("failed reading metadata");
            throw new FormatException(ex);
        }

        PhotoMetadata result = new PhotoMetadata();
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

    @Override public void update(InputStream is, OutputStream os, PhotoMetadata metadata) throws DAOException {
        if (is == null) throw new IllegalArgumentException();
        if (os == null) throw new IllegalArgumentException();
        if (metadata == null) throw new IllegalArgumentException();
        LOGGER.debug("updating photo metadata {}", metadata);

        try {
            is.mark(Integer.MAX_VALUE);
            ImageMetadata imageData = Imaging.getMetadata(is, null);
            if (imageData == null) {
                LOGGER.debug("could not find image metadata");
                throw new DAOException("No metadata found.");
            }
            if (!(imageData instanceof JpegImageMetadata)) {
                LOGGER.debug("metadata is of unknown type");
                throw new DAOException("Metadata is of unknown type.");
            }

            JpegImageMetadata jpegData = (JpegImageMetadata)imageData;
            TiffOutputSet outputSet = new TiffOutputSet();
            TiffImageMetadata exifData = jpegData.getExif();
            if (exifData != null) {
                outputSet = exifData.getOutputSet();
            }

            TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
            outputSet.setGPSInDegrees(metadata.getLongitude(), metadata.getLatitude());
            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
            exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, DATE_FORMATTER.format(metadata.getDatetime()));

            is.reset();
            new ExifRewriter().updateExifMetadataLossless(is, os, outputSet);

        } catch (IOException | ImageReadException | ImageWriteException ex) {
            LOGGER.warn("failed updating metadata");
            throw new DAOException(ex);
        }

        LOGGER.debug("updated photo metadata");
    }

    private void readDate(JpegImageMetadata input, PhotoMetadata output) {
        LOGGER.debug("reading date from metadata");
        TiffField field = input.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
        if (field == null) {
            LOGGER.debug("metadata contains no date");
            return;
        }
        String dateString = field.getValueDescription();
        dateString = dateString.substring(1, dateString.length() - 1); // remove enclosing single quotes
        output.setDatetime(DATE_FORMATTER.parse(dateString, LocalDateTime::from));
        LOGGER.debug("read date as {}", output.getDatetime());
    }

    private void readGps(JpegImageMetadata input, PhotoMetadata output) {
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
