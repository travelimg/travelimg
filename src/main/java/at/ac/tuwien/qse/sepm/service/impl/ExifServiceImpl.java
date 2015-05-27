package at.ac.tuwien.qse.sepm.service.impl;


import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExifServiceImpl implements ExifService {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

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
            final TiffImageMetadata exifMetadata = jpegMetadata.getExif();

            if (jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME) != null) {
                exposure = jpegMetadata
                        .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME)
                        .getValueDescription().split(" ")[0];
            }

            if (jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_APERTURE_VALUE) != null) {
                aperture = jpegMetadata
                        .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_APERTURE_VALUE)
                        .getDoubleValue();
            }
            if (jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH) != null) {
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

            if (jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_ALTITUDE) != null) {
                altitude = jpegMetadata
                        .findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_ALTITUDE)
                        .getDoubleValue();
            }

            return new Exif(photo.getId(), exposure, aperture, focalLength, iso, flash, make, model, altitude);
        } catch (IOException | ImageReadException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void attachDateAndGeoData(Photo photo) throws ServiceException {
        File file = new File(photo.getPath());
        LocalDateTime datetime;
        double latitude = 0.0;
        double longitude = 0.0;
        final TiffImageMetadata exifMetadata;
        try {
            final ImageMetadata metadata = Imaging.getMetadata(file);
            if (metadata == null) {
                throw new ServiceException("No metadata found");
            }
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            exifMetadata = jpegMetadata.getExif();
            if(jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL) != null) {
                String tempDate = jpegMetadata
                        .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)
                        .getValueDescription();
                tempDate = tempDate.substring(1, tempDate.length() - 1); // remove enclosing single quotes

                datetime = dateFormatter.parse(tempDate, LocalDateTime::from);
            } else {
                throw new ServiceException("Photo has no datetime");
            }
        } catch (IOException | ImageReadException e) {
            //problem here, an invalid photo or one without a datetime is useless
            throw new ServiceException(e.getMessage(), e);
        }
        try {
            if (exifMetadata != null) {
                final TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
                if (null != gpsInfo) {
                    longitude = gpsInfo.getLongitudeAsDegreesEast();
                    latitude = gpsInfo.getLatitudeAsDegreesNorth();
                }
            }
        } catch (ImageReadException e) {
            //intentionally ignore this, at least we have successfully read the datetime at this point ;)
            LOGGER.debug(e);
        }
        photo.setDatetime(datetime);
        photo.setLatitude(latitude);
        photo.setLongitude(longitude);
    }

    @Override
    public void modifyExifTags(Photo photo) throws ServiceException {
        /*
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
            exifDirectory.add(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME, RationalNumber
                    .valueOf(1 / Double.parseDouble(exif.getExposure().substring(2))));

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
            rootDirectory.add(TiffTagConstants.TIFF_TAG_MAKE, exif.getMake());

            rootDirectory.removeField(TiffTagConstants.TIFF_TAG_MODEL);
            rootDirectory.add(TiffTagConstants.TIFF_TAG_MODEL, exif.getModel());

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
        } catch (ImageReadException | ImageWriteException | IOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage(), e);
        } finally {
            try {
                IoUtils.closeQuietly(canThrow, os);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ServiceException(e.getMessage(), e);
            }
        }*/
    }

}
