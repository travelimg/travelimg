package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoNotFoundException;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoRepository;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoSerializer;
import at.ac.tuwien.qse.sepm.dao.repo.impl.FileManager;
import at.ac.tuwien.qse.sepm.dao.repo.impl.JpegSerializer;
import at.ac.tuwien.qse.sepm.dao.repo.impl.PhotoFileRepository;
import at.ac.tuwien.qse.sepm.dao.repo.impl.PhysicalFileManager;
import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.IOHandler;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExifServiceImpl implements ExifService {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public Exif getExif(Photo photo) throws ServiceException {
        File file = new File(photo.getPath());
        String exposure = "not available";
        double aperture = 0.0;
        double focalLength = 0.0;
        int iso = 0;
        boolean flash = false;
        String make = "not available";
        String model = "not available";
        double altitude = 0.0;

        try {
            final ImageMetadata metadata = Imaging.getMetadata(file);
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

            if (jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME)
                    != null) {
                exposure = jpegMetadata
                        .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME)
                        .getValueDescription().split(" ")[0];
            }

            if (jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_APERTURE_VALUE)
                    != null) {
                aperture = jpegMetadata
                        .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_APERTURE_VALUE)
                        .getDoubleValue();
            }
            if (jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH)
                    != null) {
                focalLength = jpegMetadata
                        .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH)
                        .getDoubleValue();
            }
            if (jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_ISO) != null) {
                iso = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_ISO)
                        .getIntValue();
            }

            if (jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_FLASH) != null) {
                flash = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_FLASH)
                        .getIntValue() != 0;
            }

            if (jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_MAKE) != null) {
                make = jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_MAKE)
                        .getValueDescription();
            }
            if (jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_MODEL) != null) {
                model = jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_MODEL)
                        .getValueDescription();
            }

            if (jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_ALTITUDE)
                    != null) {
                altitude = jpegMetadata
                        .findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_ALTITUDE)
                        .getDoubleValue();
            }

            return new Exif(photo.getId(), exposure, aperture, focalLength, iso, flash, make, model,
                    altitude);
        } catch (IOException | ImageReadException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override public void setMetaData(Photo photo) throws ServiceException {
        JpegSerializer serializer = new JpegSerializer();
        FileManager fileManager = new PhysicalFileManager();

        logger.debug("Set metadata {}", photo);

        Path file = photo.getFile();

        Path temp = Paths.get(file.toString() + ".temp");
        InputStream is = null;
        OutputStream os = null;
        try {
            if (!fileManager.exists(temp)) {
                try {
                    fileManager.createFile(temp);
                } catch (IOException ex) {
                    logger.warn("failed creating temp file at {}", temp);
                    throw new ServiceException(ex);
                }
            }

            try {
                is = fileManager.newInputStream(file);
            } catch (IOException ex) {
                logger.warn("failed creating input stream for file {}", file);
                throw new ServiceException(ex);
            }

            try {
                os = fileManager.newOutputStream(temp);
            } catch (IOException ex) {
                logger.warn("failed creating output stream for file {}", temp);
                throw new ServiceException(ex);
            }

            try {
                serializer.update(is, os, photo.getData());
            } catch (DAOException ex) {
                throw new ServiceException(ex);
            }

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                logger.warn("failed closing input stream for file {}");
                logger.error(ex);
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ex) {
                logger.warn("failed closing output stream for file {}");
                logger.error(ex);
            }
        }

        try {
            fileManager.copy(temp, file);
        } catch (IOException ex) {
            logger.warn("failed copying {} -> {}", temp, file);
            throw new ServiceException(ex);
        } finally {
            try {
                if (fileManager.exists(temp)) {
                    fileManager.delete(temp);
                }
            } catch (IOException ex) {
                logger.warn("failed deleting temp file {}", temp);
                logger.error(ex);
            }
        }
    }
}
