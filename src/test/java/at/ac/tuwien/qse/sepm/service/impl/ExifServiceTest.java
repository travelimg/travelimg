package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.WithData;
import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 02.06.2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-config.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ExifServiceTest {

    private static final Photographer defaultPhotographer = new Photographer(1, "Test Photographer");
    private static final Place defaultPlace = new Place(1, "Unkown place", "Unknown place");

    private static final String dataDir = Paths
            .get(System.getProperty("java.io.tmpdir"), "travelimg").toString();
    private static final String sourceDir = Paths.get(
            System.getProperty("os.name").contains("indow") ?
                    ImportTest.class.getClassLoader().getResource("db/testimages").getPath()
                            .substring(1) :
                    ImportTest.class.getClassLoader().getResource("db/testimages")
                            .getPath()).toString();

    private List<Photo> expectedPhotos = new ArrayList<Photo>() {{
        add(new Photo(6, defaultPhotographer, dataDir + "/2005/09/11/6.jpg", Rating.NONE, LocalDateTime
                .of(2005, 9, 11, 15, 43, 55), 39.73934166666667, -104.99156111111111, defaultPlace));
        add(new Photo(7, defaultPhotographer, dataDir + "/2005/09/11/7.jpg", Rating.NONE, LocalDateTime.of(2005, 9, 11, 15, 44, 8), 39.739336111111115, -104.9916361111111, defaultPlace));
        add(new Photo(8, defaultPhotographer, dataDir + "/2005/09/11/8.jpg", Rating.NONE, LocalDateTime.of(2005, 9, 11, 15, 48, 7), 39.73994444444445, -104.98952777777778, defaultPlace));
    }};

    List<Photo> inputPhotos = new ArrayList<Photo>() {{
        add(new Photo(6, defaultPhotographer, sourceDir + "/6.jpg", Rating.NONE, null, 0, 0, defaultPlace));
        add(new Photo(7, defaultPhotographer, sourceDir + "/7.jpg", Rating.NONE, null, 0, 0, defaultPlace));
        add(new Photo(8, defaultPhotographer, sourceDir + "/8.jpg", Rating.NONE, null, 0, 0, defaultPlace));
    }};

    @Autowired private ExifService exifService;

    @WithData
    @Test
    public void testTagExportToPhotoFile() throws ServiceException {
        Tag testTag = new Tag(null, "Testtag");
        inputPhotos.get(0).getTags().add(testTag);
        exifService.exportTagsToExif(inputPhotos.get(0));
    }

    @WithData
    @Test
    public void testTagImportFromPhotoFile() {

    }
}


