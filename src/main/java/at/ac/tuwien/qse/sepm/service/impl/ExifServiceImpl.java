package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.service.*;
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
import org.apache.commons.imaging.util.IoUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ExifServiceImpl implements ExifService {

    private static final Logger logger = LogManager.getLogger();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter
            .ofPattern("yyyy:MM:dd HH:mm:ss");

    @Autowired
    PhotoService photoService;
    @Autowired
    TagService tagService;
    @Autowired
    ClusterService clusterService;

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

    @Override
    public void getTagsFromExif(Photo photo) throws ServiceException {
        logger.debug("getTagsFromExif" + photo + ":" + photo.getTags());
        File file = new File(photo.getPath());
        String tags = "";
        Journey journey = null;

        try {
            final ImageMetadata metadata = Imaging.getMetadata(file);
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

            if (jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_USER_COMMENT)
                    != null) {
                tags = jpegMetadata
                        .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_USER_COMMENT)
                        .getValueDescription();
                // no tags from our programm
                if (!tags.contains("travelimg"))
                    return;
                logger.debug("Tags in exif found: " + tags);
                tags = tags.replace("'", "");
            }
            String[] tagArray = tags.split("/");
            for (String element : tagArray) {
                if (element.equals("travelimg"))
                    continue;

                if (element.contains("journey")) {
                    String[] tempJourney = element.split("\\.");

                    LocalDateTime startdate = LocalDateTime.parse(tempJourney[2], dateFormatter);

                    LocalDateTime enddate = LocalDateTime.parse(tempJourney[3], dateFormatter);

                    journey = new Journey(0, tempJourney[1], startdate, enddate);
                    continue;
                }

                if (element.contains("place")) {
                    String[] tempPlace = element.split("\\|");
                    Place place = new Place(0, tempPlace[1], tempPlace[2],
                            Double.parseDouble(tempPlace[3]), Double.parseDouble(tempPlace[4]),
                            journey);
                    // TODO: only create records if they don't already exist for the photo
                    clusterService.addPlace(place);
                    photoService.addPlaceToPhotos(Arrays.asList(photo), place);
                    photoService.addJourneyToPhotos(Arrays.asList(photo),
                            journey);
                    continue;
                }

                // tags
                Tag tag = new Tag(null, element);
                Tag tempTag = tagService.readName(tag);
                if (tempTag == null) {
                    tagService.create(tag);
                    tempTag = tag;
                }
                tagService.addTagToPhotos(Arrays.asList(photo), tempTag);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ImageReadException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportMetaToExif(Photo photo) throws ServiceException {
        logger.debug("exportMetaToExif" + photo + ":" + photo.getTags());
        File jpegImageFile = new File(photo.getPath());
        File tempFile = new File(photo.getPath() + "d");
        OutputStream os = null;
        boolean canThrow = false;

        String tags = "travelimg";

        for (Tag element : photo.getTags()) {
            tags += "/" + element.getName();
        }

        if (photo.getPlace() != null) {
            Journey journey = photo.getPlace().getJourney();
            tags += "/journey." + journey.getName() + "." + journey.getStartDate()
                    .format(dateFormatter) + "." + journey.getEndDate().format(dateFormatter);

            Place place = photo.getPlace();
            tags += "/place|" + place.getCity() + "|" + place.getCountry() + "|" + place
                    .getLatitude() + "|" + place.getLongitude();
        }

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

            TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();

            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT);
            exifDirectory.add(ExifTagConstants.EXIF_TAG_USER_COMMENT, tags);
            logger.debug("Write tags to file: " + tags);

            os = new FileOutputStream(tempFile);
            os = new BufferedOutputStream(os);

            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);
            canThrow = true;
            Files.copy(tempFile.toPath(), jpegImageFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            Files.delete(tempFile.toPath());
        } catch (ImageWriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ImageReadException e) {
            e.printStackTrace();
        } finally {
            try {
                IoUtils.closeQuietly(canThrow, os);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void attachDateAndGeoData(Photo photo) throws ServiceException {
        File file = new File(photo.getPath());
        LocalDateTime datetime = photo.getDatetime();
        double latitude = photo.getLatitude();
        double longitude = photo.getLongitude();

        try {
            ImageMetadata metadata = Imaging.getMetadata(file);

            if (datetime == null)
                datetime = getDateTime(metadata);

            if (Double.compare(latitude, 0.0) == 0 || Double.compare(longitude, 0.0) == 0) {
                final TiffImageMetadata.GPSInfo gpsInfo = ((JpegImageMetadata) metadata).getExif()
                        .getGPS();
                if (null != gpsInfo) {
                    longitude = gpsInfo.getLongitudeAsDegreesEast();
                    latitude = gpsInfo.getLatitudeAsDegreesNorth();
                }
            }
        } catch (IOException | ImageReadException e) {
            // intentionally ignore and use default
            logger.debug("Error occurred attaching geodate and date", e);
        }

        if (datetime == null)
            datetime = LocalDateTime.MIN;

        photo.setDatetime(datetime);
        photo.setLatitude(latitude);
        photo.setLongitude(longitude);
    }

    private LocalDateTime getDateTime(ImageMetadata metadata) {
        if (metadata == null)
            return null;

        final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
        if (jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)
                != null) {
            String tempDate = jpegMetadata
                    .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)
                    .getValueDescription();
            tempDate = tempDate
                    .substring(1, tempDate.length() - 1); // remove enclosing single quotes

            return dateFormatter.parse(tempDate, LocalDateTime::from);
        } else {
            return null;
        }
    }

}
