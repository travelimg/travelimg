package at.ac.tuwien.qse.sepm.dao.repo.impl;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.repo.FormatException;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoSerializer;
import at.ac.tuwien.qse.sepm.entities.*;
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
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy:MM:dd HH:mm:ss");

    @Override public PhotoMetadata read(InputStream is) throws DAOException {
        if (is == null)
            throw new IllegalArgumentException();
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

        JpegImageMetadata jpegData = (JpegImageMetadata) imageData;
        readDate(jpegData, result);
        readGps(jpegData, result);
        readMetaData(jpegData, result);
        return result;
    }

    @Override public void update(InputStream is, OutputStream os, PhotoMetadata metadata)
            throws DAOException {
        if (is == null)
            throw new IllegalArgumentException();
        if (os == null)
            throw new IllegalArgumentException();
        if (metadata == null)
            throw new IllegalArgumentException();
        LOGGER.debug("updating photo metadata {}", metadata);

        String tags = "travelimg";

        for (Tag element : metadata.getTags()) {
            tags += "/" + element.getName();
        }

        Rating rating = metadata.getRating();
        tags += "/rating|" + rating;

        Place place = metadata.getPlace();
        if (place != null) {
            tags += "/place|" + place.getCity() + "|" + place.getCountry() + "|" + place
                    .getLatitude() + "|" + place.getLongitude();
        }

        Journey journey = metadata.getJourney();
        if (journey != null) {
            tags += "/journey|" + journey.getName() + "|" + journey.getStartDate()
                    .format(DATE_FORMATTER) + "|" + journey.getEndDate().format(DATE_FORMATTER);
        }

        Photographer photographer = metadata.getPhotographer();
        if (photographer != null) {
            tags += "/photographer|" + photographer.getName();
        }

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

            JpegImageMetadata jpegData = (JpegImageMetadata) imageData;
            TiffOutputSet outputSet = new TiffOutputSet();
            TiffImageMetadata exifData = jpegData.getExif();
            if (exifData != null) {
                outputSet = exifData.getOutputSet();
            }

            TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
            outputSet.setGPSInDegrees(metadata.getLongitude(), metadata.getLatitude());

            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
            exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL,
                    DATE_FORMATTER.format(metadata.getDatetime()));

            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT);
            exifDirectory.add(ExifTagConstants.EXIF_TAG_USER_COMMENT, tags);

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
        TiffField field = input
                .findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
        if (field == null) {
            LOGGER.debug("metadata contains no date");
            return;
        }
        String dateString = field.getValueDescription();
        dateString = dateString
                .substring(1, dateString.length() - 1); // remove enclosing single quotes
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
            LOGGER.debug("read GPS as longitude {} and latitude {}", output.getLongitude(),
                    output.getLatitude());
        } catch (ImageReadException ex) {
            LOGGER.warn("failed reading GPS data");
        }
    }

    private void readMetaData(JpegImageMetadata input, PhotoMetadata result) {
        String tags = "";
        if (input.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_USER_COMMENT) != null) {
            tags = input.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_USER_COMMENT)
                    .getValueDescription();
            // no tags from our programm
            if (!tags.contains("travelimg"))
                return;
            LOGGER.debug("Tags in exif found: " + tags);
            tags = tags.replace("'", "");
        } else {
            return;
        }
        String[] tagArray = tags.split("/");
        for (String element : tagArray) {
            if (element.equals("travelimg"))
                continue;

            // journeys
            if (element.contains("journey")) {
                String[] tempJourney = element.split("\\|");
                LocalDateTime startDate = LocalDateTime.parse(tempJourney[2], DATE_FORMATTER);
                LocalDateTime endDate = LocalDateTime.parse(tempJourney[3], DATE_FORMATTER);
                result.setJourney(new Journey(null, tempJourney[1], startDate, endDate));
                continue;
            }

            // places
            if (element.contains("place")) {
                String[] tempPlace = element.split("\\|");
                result.setPlace(
                        new Place(0, tempPlace[1], tempPlace[2], Double.parseDouble(tempPlace[3]),
                                Double.parseDouble(tempPlace[4])));
                continue;
            }

            // rating
            if (element.contains("rating")) {
                String[] tempRating = element.split("\\|");
                result.setRating(Rating.valueOf(tempRating[1]));
                continue;
            }

            // photographer
            if (element.contains("photographer")) {
                String[] tempPhotographer = element.split("\\|");
                result.setPhotographer(new Photographer(null, tempPhotographer[1]));
                continue;
            }

            // tags
            if (!element.trim().isEmpty()) {
                result.getTags().add(new Tag(null, element));
            }
        }

    }
}