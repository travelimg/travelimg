package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.ExifDAO;
import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.imaging.util.IoUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ExifServiceImpl implements ExifService {

    private static final Logger logger = LogManager.getLogger(ExifServiceImpl.class);
    private ExifDAO exifDAO;

    public ExifServiceImpl() {

    }

    @Autowired public void setExifDAO(ExifDAO exifDAO) {
        this.exifDAO = exifDAO;
    }

    public void changeExif(Photo photo) throws ServiceException {
        File file = new File(photo.getPath());
        Exif exif = photo.getExif();

        try {
            this.setExifTags(file, exif);
            exifDAO.update(exif);
        } catch (IOException | DAOException | ImageWriteException | ImageReadException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public Exif importExif(Photo photo) throws ServiceException {
        try {
            return exifDAO.importExif(photo);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void setExifTags(final File jpegImageFile, Exif exif)
            throws IOException, ImageReadException, ImageWriteException {

        File tempFile = new File(jpegImageFile.getPath() + "d");
        OutputStream os = null;
        boolean canThrow = false;
        try {
            TiffOutputSet outputSet = null;

            // note that metadata might be null if no metadata is found.
            final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            if (null != jpegMetadata) {
                // note that exif might be null if no Exif metadata is found.
                final TiffImageMetadata exifMeta = jpegMetadata.getExif();

                if (exifMeta != null) {
                    // TiffImageMetadata class is immutable (read-only).
                    // TiffOutputSet class represents the Exif data to write.
                    //
                    // Usually, we want to update existing Exif metadata by
                    // changing
                    // the values of a few fields, or adding a field.
                    // In these cases, it is easiest to use getOutputSet() to
                    // start with a "copy" of the fields read from the image.
                    outputSet = exifMeta.getOutputSet();

                }
            }

            // if file does not contain any exif metadata, we create an empty
            // set of exif metadata. Otherwise, we keep all of the other
            // existing tags.
            if (outputSet == null) {
                outputSet = new TiffOutputSet();
            }

            TiffOutputDirectory rootDirectory = outputSet.getOrCreateRootDirectory();
            TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
            TiffOutputDirectory gpsDirectory = outputSet.getOrCreateGPSDirectory();

            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME);
            exifDirectory.add(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME,
                    RationalNumber.valueOf(1/Double.parseDouble(exif.getExposure().substring(2))));


            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
            exifDirectory.add(ExifTagConstants.EXIF_TAG_APERTURE_VALUE,
                    RationalNumber.valueOf(exif.getAperture()));

            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH);
            exifDirectory.add(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH,
                    RationalNumber.valueOf(exif.getFocalLength()));

            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_ISO);
            exifDirectory.add(ExifTagConstants.EXIF_TAG_ISO, (short) exif.getIso());

            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_FLASH);
            exifDirectory.add(ExifTagConstants.EXIF_TAG_FLASH, (short) (exif.isFlash() ? 1 : 0));

            rootDirectory.removeField(TiffTagConstants.TIFF_TAG_MAKE);
            rootDirectory.add(TiffTagConstants.TIFF_TAG_MAKE, new String[] { exif.getMake() });

            rootDirectory.removeField(TiffTagConstants.TIFF_TAG_MODEL);
            rootDirectory.add(TiffTagConstants.TIFF_TAG_MODEL, new String[] { exif.getModel() });

            gpsDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_ALTITUDE);
            gpsDirectory.add(GpsTagConstants.GPS_TAG_GPS_ALTITUDE,
                    RationalNumber.valueOf(exif.getAltitude()));

            outputSet.setGPSInDegrees(exif.getLongitude(), exif.getLatitude());

            os = new FileOutputStream(tempFile);
            os = new BufferedOutputStream(os);

            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);
            canThrow = true;
            Files.copy(tempFile.toPath(), jpegImageFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            Files.delete(tempFile.toPath());
        } finally {
            IoUtils.closeQuietly(canThrow, os);
        }
    }
}
