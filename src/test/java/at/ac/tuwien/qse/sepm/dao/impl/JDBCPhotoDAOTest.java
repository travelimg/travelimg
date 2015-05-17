package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@UsingTable("Photo")
public class JDBCPhotoDAOTest extends AbstractJDBCDAOTest {

    @Autowired
    PhotoDAO photoDAO;

    private static final Photographer defaultPhotographer = new Photographer(1, "Test Photographer");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH);

    private static final String dataDir = Paths.get(System.getProperty("java.io.tmpdir"), "travelimg").toString();
    private static final String sourceDir = Paths.get(JDBCPhotoDAOTest.class.getClassLoader().getResource("db/testimages").getPath()).toString();

    private Photo expectedPhotos[] = new Photo[] {
            new Photo(1, defaultPhotographer, dataDir + "/2015/03/06/1.jpg", 0, LocalDate.of(2015, 3, 6), 41.5, 19.5),
            new Photo(2, defaultPhotographer, dataDir + "/2005/09/11/2.jpg", 0, LocalDate.of(2005, 9, 11), 39.7, -104.9),
            new Photo(3, defaultPhotographer, dataDir + "/2005/09/11/3.jpg", 0, LocalDate.of(2005, 9, 11), 39.7, -104.9),
            new Photo(4, defaultPhotographer, dataDir + "/2005/09/11/4.jpg", 0, LocalDate.of(2005, 9, 11), 39.7, -104.9),
            new Photo(5, defaultPhotographer, dataDir + "/2015/03/04/5.jpg", 0, LocalDate.of(2015, 3, 4), 12.0, 12.0),
            new Photo(6, defaultPhotographer, dataDir + "/2015/05/17/6.jpg", 0, LocalDate.of(2015, 5, 17), 41.5042718, 19.5180115),
            new Photo(7, defaultPhotographer, dataDir + "/2015/05/17/7.jpg", 0, LocalDate.of(2015, 5, 17), 41.5042718, 19.5180115),
            new Photo(8, defaultPhotographer, dataDir + "/2015/05/17/8.jpg", 0, LocalDate.of(2015, 5, 17), 41.5042718, 19.5180115),
    };

    private Photo inputPhotos[] = new Photo[] {
            new Photo(6, defaultPhotographer, sourceDir + "/6.jpg", 0, LocalDate.of(2015, 5, 17), 41.5042718, 19.5180115),
            new Photo(7, defaultPhotographer, sourceDir + "/7.jpg", 0, LocalDate.of(2015, 5, 17), 41.5042718, 19.5180115),
            new Photo(8, defaultPhotographer, sourceDir + "/8.jpg", 0, LocalDate.of(2015, 5, 17), 41.5042718, 19.5180115),
    };

    private Photo getInputPhoto(int seq) {
        return new Photo(inputPhotos[seq]);
    }

    private Photo getExpectedPhoto(Photo template) {
        return new Photo(expectedPhotos[template.getId() - 1]);
    }

    private void setDataPrefixDir(Photo photo) {
        String path = photo.getPath().replace("$DIR", dataDir);
        photo.setPath(path);
    }

    @Before
    public void setUp() throws Exception {
        FileUtils.deleteDirectory(new File(dataDir));
        FileUtils.copyDirectory(new File(Paths.get(sourceDir, "prepared").toString()), new File(dataDir));
    }

    @Test
    public void testEmpty() throws DAOException {
        assertEquals(0, countRows());
    }

    @Test
    @WithData
    public void testWithData() throws DAOException {
        assertEquals(5, countRows());
    }

    @Test
    @WithData
    public void testCreateResultHasCorrectAttributes() throws DAOException, ValidationException {
        Photo photo = getInputPhoto(0);
        Photo expected = getExpectedPhoto(photo);

        Photo value = photoDAO.create(photo);

        expected.setId(value.getId());
        assertEquals(expected, value);
    }

    @Test
    @WithData
    public void testCreateImageFileIsCopied() throws DAOException, ValidationException {
        Photo photo = getInputPhoto(1);
        Photo expected = getExpectedPhoto(photo);

        Photo value = photoDAO.create(photo);

        assertEquals(expected.getPath(), value.getPath());
        assertTrue(Files.exists(Paths.get(expected.getPath())));
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void testCreateWithNonexistingPathThrows() throws DAOException, ValidationException {
        Photo photo = getInputPhoto(2);
        photo.setPath("/path/to/oblivion");

        photoDAO.create(photo);
    }

    @Test(expected = DAOException.class)
    public void testCreateWithUnknownPhotographerThrows() throws DAOException, ValidationException {
        Photo photo = getInputPhoto(0);
        photo.setPhotographer(new Photographer(-42, "does not exist"));

        photoDAO.create(photo);
    }

    @Test
    @WithData
    public void testReadAllRecordsExist() throws DAOException, ValidationException {
        List<Photo> photos = photoDAO.readAll();

        for(Photo photo : photos) {
            setDataPrefixDir(photo);
            Photo expected = getExpectedPhoto(photo);
            assertEquals(expected, photo);
        }
    }


}
