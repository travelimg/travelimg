package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExifServiceTest extends ServiceTestBase {

    private static final String sourceDir = Paths.get(
            System.getProperty("os.name").contains("indow") ?
                    ImportTest.class.getClassLoader().getResource("db/testimages").getPath()
                            .substring(1) :
                    ImportTest.class.getClassLoader().getResource("db/testimages").getPath())
            .toString() + "/";

    private Exif expected = new Exif(6, "1/410", 2.8, 5.7, 125, true, "'RICOH'", "'Caplio Pro G3'", 1577.0);
    private Photo photo = new Photo(6, null, Paths.get(sourceDir, "6.jpg").toString(), Rating.NONE, null, 0, 0, null);


    @Autowired
    private ExifService exifService;

    @Test
    public void testReadExif() throws ServiceException {
        Exif actual = exifService.getExif(photo);

        assertThat(actual, equalTo(expected));
    }
}


