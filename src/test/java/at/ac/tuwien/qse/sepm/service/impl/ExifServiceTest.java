package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ExifServiceTest extends ServiceTestBase {
    private static final Logger logger = LogManager.getLogger(ClusterServiceTest.class);

    private static final Photographer defaultPhotographer = new Photographer(1,
            "Test Photographer");

    private static final String sourceDir = Paths.get(
            System.getProperty("os.name").contains("indow") ?
                    ImportTest.class.getClassLoader().getResource("db/testimages").getPath()
                            .substring(1) :
                    ImportTest.class.getClassLoader().getResource("db/testimages").getPath())
            .toString() + "/";

    Journey inputJourney = new Journey(1, "TestJourney", LocalDateTime.of(2005, 9, 10, 15, 44, 8),
            LocalDateTime.of(2005, 9, 12, 15, 44, 8));

    Tag inputTag = new Tag(3, "Testtag");

    Place inputPlace = new Place(2, "Vienna", "Austria", 48.20, 16.37);

    private List<Photo> inputPhotos = new ArrayList<Photo>() {{
        add(new Photo(7, defaultPhotographer, sourceDir + "/exif/6.jpg", Rating.NONE,
                LocalDateTime.of(2005, 9, 11, 15, 43, 55), 39.73934166666667, -104.99156111111111,
                new Place(1, "Vienna", "Austria", 48.20, 16.37)));
        add(new Photo(8, defaultPhotographer, sourceDir + "/exif/7.jpg", Rating.NONE,
                LocalDateTime.of(2005, 9, 11, 15, 44, 8), 39.739336111111115, -104.9916361111111,
                null));
        add(new Photo(6, defaultPhotographer, sourceDir + "/exif/8.jpg", Rating.NONE,
                LocalDateTime.of(2005, 9, 11, 15, 48, 7), 39.73994444444445, -104.98952777777778,
                new Place(1, "Vienna", "Austria", 48.20, 16.37)));
    }};

    private List<Photo> expectedPhotos = new ArrayList<Photo>() {{
        add(new Photo(7, defaultPhotographer, sourceDir + "/exif/6.jpg", Rating.NONE,
                LocalDateTime.of(2005, 9, 11, 15, 43, 55), 39.73934166666667, -104.99156111111111,
                null));
        add(new Photo(8, defaultPhotographer, sourceDir + "/exif/7.jpg", Rating.NONE,
                LocalDateTime.of(2005, 9, 11, 15, 44, 8), 39.739336111111115, -104.9916361111111,
                null));
        add(new Photo(6, defaultPhotographer, sourceDir + "/exif/8.jpg", Rating.NONE,
                LocalDateTime.of(2005, 9, 11, 15, 48, 7), 39.73994444444445, -104.98952777777778,
                new Place(1, "Vienna", "Austria", 48.20, 16.37)));
    }};

    @Autowired
    private ExifService exifService;
    @Autowired
    private JourneyDAO journeyDAO;
    @Autowired
    private PlaceDAO placeDAO;
    @Autowired
    private TagDAO tagDA0;
    @Autowired
    private PhotoDAO photoDAO;

    @BeforeClass
    public static void init() {
        new File(sourceDir + "/exif/").mkdirs();
    }

    @WithData
    @Test
    public void testTagExportToPhotoFile()
            throws ServiceException, ValidationException, DAOException {
        logger.debug("Entering testTagExportToPhotoFile()");

        try {

            Files.copy(new File(sourceDir + "8.jpg").toPath(),
                    new File(sourceDir + "/exif/8.jpg").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputPhotos.get(2).getData().getTags().add(inputTag);
        exifService.exportMetaToExif(inputPhotos.get(2));
        exifService.getTagsFromExif(expectedPhotos.get(2));

        Tag expectedTag = expectedPhotos.get(2).getData().getTags().iterator().next();
        Tag actualTag = inputPhotos.get(2).getData().getTags().iterator().next();
        assertEquals(expectedTag.getName(), actualTag.getName());
    }

    @WithData
    @Test
    public void testJourneyExportToPhotoFile()
            throws ServiceException, ValidationException, DAOException {
        logger.debug("Entering testJourneyExportToPhotoFile()");

        try {
            Files.copy(new File(sourceDir + "6.jpg").toPath(),
                    new File(sourceDir + "/exif/6.jpg").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputPhotos.get(0).getData().setJourney(inputJourney);
        exifService.exportMetaToExif(inputPhotos.get(0));
        exifService.getTagsFromExif(expectedPhotos.get(0));
        assertEquals(inputPhotos.get(0).getData().getJourney(), expectedPhotos.get(0).getData().getJourney());
        List<Journey> journeyList = journeyDAO.readAll();
        assertNotNull(journeyList);
    }


    @WithData
    @Test
    public void testPlaceExportToPhotoFile()
            throws ServiceException, ValidationException, DAOException {
        logger.debug("Entering testPlaceExportToPhotoFile()");

        try {
            Files.copy(new File(sourceDir + "7.jpg").toPath(),
                    new File(sourceDir + "/exif/7.jpg").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputPhotos.get(1).getData().setPlace(inputPlace);
        exifService.exportMetaToExif(inputPhotos.get(1));
        exifService.getTagsFromExif(expectedPhotos.get(1));
        assertEquals(inputPhotos.get(1).getData().getPlace().getCity(),
                expectedPhotos.get(1).getData().getPlace().getCity());
        assertEquals(inputPhotos.get(1).getData().getPlace().getCountry(), expectedPhotos.get(1).getData().getPlace().getCountry());
        List<Place> placeList = placeDAO.readAll();
        assertNotNull(placeList);
    }

}


