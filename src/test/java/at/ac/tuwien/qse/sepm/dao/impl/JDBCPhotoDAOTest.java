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

    private static Photo getTestPhoto(Integer id) {
        Pair<String, String> paths = getPhotoSourceDest(6);
        return new Photo(-1, defaultPhotographer, paths.getKey(), 0, LocalDate.now(), -30, 30);
    }

    private static Photo getExpectedPhoto(Integer id) {
        Pair<String, String> paths = getPhotoSourceDest(6);
        return new Photo(-1, defaultPhotographer, paths.getValue(), 0, LocalDate.now(), -30, 30);
    }

    private static Pair<String, String> getPhotoSourceDest(Integer id) {
        if (id < 6 || id > 8) return null;

        String date = dateFormatter.format(LocalDate.now());

        String source = Paths.get(sourceDir, id.toString() + ".jpg").toString();
        String dest = Paths.get(dataDir, date + "/" + id.toString() + ".jpg").toString();

        return new Pair<>(source, dest);
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
        Pair<String, String> paths = getPhotoSourceDest(6);
        Photo photo = getTestPhoto(6); //new Photo(-1, defaultPhotographer, paths.getKey(), 0, LocalDate.now(), -30, 30);
        Photo expected = getExpectedPhoto(6);// Photo(-1, defaultPhotographer, paths.getValue(), 0, LocalDate.now(), -30, 30);

        Photo value = photoDAO.create(photo);

        expected.setId(value.getId());
        assertEquals(expected, value);
    }

    @Test
    @WithData
    public void testCreateImageFileIsCopied() throws DAOException, ValidationException {
        Photo photo = getTestPhoto(7);
        Photo expected = getExpectedPhoto(7);

        Photo value = photoDAO.create(photo);

        assertEquals(expected.getPath(), value.getPath());
        assertTrue(Files.exists(Paths.get(expected.getPath())));
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void testCreateWithNonexistingPathThrows() throws DAOException, ValidationException {
        Photo photo = getTestPhoto(8);
        photo.setPath("/path/to/oblivion");

        photoDAO.create(photo);
    }

    @Test(expected = DAOException.class)
    public void testCreateWithUnknownPhotographerThrows() throws DAOException, ValidationException {
        Pair<String, String> paths = getPhotoSourceDest(6);
        Photo photo = new Photo(-1, new Photographer(-42, "Doesnotexist"), paths.getKey(), 0, LocalDate.now(), -30, 30);

        photoDAO.create(photo);
    }


}
