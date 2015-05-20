package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Rating;
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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

@UsingTable("Photo")
public class JDBCPhotoDAOTest extends AbstractJDBCDAOTest {

    @Autowired
    PhotoDAO photoDAO;

    private static final Photographer defaultPhotographer = new Photographer(1, "Test Photographer");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH);

    private static final String dataDir = Paths.get(System.getProperty("java.io.tmpdir"), "travelimg").toString();
    private static final String sourceDir = Paths.get(System.getProperty( "os.name" ).contains( "indow" ) ?
            JDBCPhotoDAOTest.class.getClassLoader().getResource("db/testimages").getPath().substring(1) :
            JDBCPhotoDAOTest.class.getClassLoader().getResource("db/testimages").getPath()).toString();

    private YearMonth expectedMonths[] = new YearMonth[] {
            YearMonth.of(2005, 9),
            YearMonth.of(2015, 3),
            YearMonth.of(2015, 5)
    };

    private Photo expectedPhotos[] = new Photo[] {
            new Photo(1, defaultPhotographer, dataDir + "/2015/03/06/1.jpg", Rating.NONE, LocalDateTime.of(2015, 3, 6, 0, 0, 0), 41.5, 19.5),
            new Photo(2, defaultPhotographer, dataDir + "/2005/09/11/2.jpg", Rating.NONE, LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9),
            new Photo(3, defaultPhotographer, dataDir + "/2005/09/11/3.jpg", Rating.NONE, LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9),
            new Photo(4, defaultPhotographer, dataDir + "/2005/09/11/4.jpg", Rating.NONE, LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9),
            new Photo(5, defaultPhotographer, dataDir + "/2015/03/04/5.jpg", Rating.NONE, LocalDateTime.of(2015, 3, 4, 0, 0, 0), 12.0, 12.0),
            new Photo(6, defaultPhotographer, dataDir + "/2015/05/17/6.jpg", Rating.NONE, LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115),
            new Photo(7, defaultPhotographer, dataDir + "/2015/05/17/7.jpg", Rating.NONE, LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115),
            new Photo(8, defaultPhotographer, dataDir + "/2015/05/17/8.jpg", Rating.NONE, LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115),
    };

    private Photo inputPhotos[] = new Photo[] {
            new Photo(6, defaultPhotographer, sourceDir + "/6.jpg", Rating.NONE, LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115),
            new Photo(7, defaultPhotographer, sourceDir + "/7.jpg", Rating.NONE, LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115),
            new Photo(8, defaultPhotographer, sourceDir + "/8.jpg", Rating.NONE, LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115),
    };

    public JDBCPhotoDAOTest() {
        for(Photo photo : Arrays.asList(expectedPhotos)) {
            String path = photo.getPath().replace("/", File.separator);
            photo.setPath(path);
        }

        for(Photo photo : Arrays.asList(inputPhotos)) {
            String path = photo.getPath().replace("/", File.separator);
            photo.setPath(path);
        }

    }

    private Photo getInputPhoto(int seq) {
        return new Photo(inputPhotos[seq]);
    }

    private Photo getExpectedPhoto(int id) {
        return new Photo(expectedPhotos[id - 1]);
    }

    private void setDataPrefixDir(Photo photo) {
        String path = photo.getPath().replace("$DIR", dataDir);
        photo.setPath(path);
    }

    private List<Photo> setPrefix(List<Photo> photos) {
        for(Photo photo : photos) {
            setDataPrefixDir(photo);
        }

        return photos;
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

    @Test(expected = ValidationException.class)
    @WithData
    public void testCreateWithNullThrows() throws DAOException, ValidationException {
        photoDAO.create(null);
    }

    @Test
    @WithData
    public void testCreateResultHasCorrectAttributes() throws DAOException, ValidationException {
        Photo photo = getInputPhoto(0);
        Photo expected = getExpectedPhoto(photo.getId());

        Photo value = photoDAO.create(photo);

        expected.setId(value.getId());
        assertEquals(expected, value);
    }

    @Test
    @WithData
    public void testCreateImageFileIsCopied() throws DAOException, ValidationException {
        Photo photo = getInputPhoto(1);
        Photo expected = getExpectedPhoto(photo.getId());

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
        List<Photo> photos = setPrefix(photoDAO.readAll());

        for(Photo photo : photos) {
            setDataPrefixDir(photo);
            Photo expected = getExpectedPhoto(photo.getId());
            assertEquals(expected, photo);
        }
    }

    @Test
    @WithData
    public void testCreateAddsEntry() throws DAOException, ValidationException {
        int initial = countRows();

        Photo photo = getInputPhoto(0);

        assertThat(setPrefix(photoDAO.readAll()), not(hasItem(photo)));

        int id = photoDAO.create(photo).getId();
        photo.setId(id);

        assertEquals(initial + 1, countRows());
        assertThat(setPrefix(photoDAO.readAll()), hasItem(photo));
    }

    @Test
    @WithData
    public void testReadAllReturnsCreated() throws DAOException, ValidationException {
        Photo photo = getInputPhoto(1);
        Photo expected = getExpectedPhoto(photo.getId());

        assertThat(setPrefix(photoDAO.readAll()), not(hasItem(expected)));

        int id = photoDAO.create(photo).getId();
        expected.setId(id);

        assertThat(setPrefix(photoDAO.readAll()), hasItem(expected));
    }

    @Test
    public void testReadMonthsEmpty() throws DAOException {
        assertEquals(0, photoDAO.getMonthsWithPhotos().size());
    }

    @Test
    @WithData
    public void testReadMonthsWithData() throws DAOException {
        assertEquals(2, photoDAO.getMonthsWithPhotos().size());

        for(YearMonth month : photoDAO.getMonthsWithPhotos()) {
            assertThat(Arrays.asList(expectedMonths), hasItem(month));
        }
    }

    @Test
    @WithData
    public void testCreateNewMonthsAffectsReadMonths() throws DAOException, ValidationException {
        Photo photo = getInputPhoto(0);

        int initial = photoDAO.getMonthsWithPhotos().size();
        photoDAO.create(photo);
        assertEquals(initial + 1, photoDAO.getMonthsWithPhotos().size());
    }

    @Test
    public void testReadPhotosByMonthEmpty() throws DAOException {
        for(YearMonth month : expectedMonths) {
            assertEquals(0, photoDAO.readPhotosByMonth(month).size());
        }
    }

    @Test
    @WithData
    public void testReadPhotosByMonthWithData() throws DAOException {
        assertEquals(3, photoDAO.readPhotosByMonth(expectedMonths[0]).size());
        assertEquals(2, photoDAO.readPhotosByMonth(expectedMonths[1]).size());
    }

    @Test
    @WithData
    public void testCreateAffectsReadPhotosByMonth() throws DAOException, ValidationException {
        Photo photo = getInputPhoto(1);

        int id = photoDAO.create(photo).getId();
        photo.setId(id);

        assertThat(photoDAO.readPhotosByMonth(expectedMonths[2]), hasItem(photo));
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void testDeleteWithNullThrows() throws DAOException, ValidationException {
        photoDAO.delete(null);
    }

    @Test
    @WithData
    public void testDeleteWithNonexistingPhotoIsNop() throws DAOException, ValidationException {
        int initial = countRows();

        Photo photo = getExpectedPhoto(1);
        photo.setId(1337);
        photoDAO.delete(photo);

        assertEquals(initial, countRows());
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void testDeleteWithINvalidIdThrows() throws DAOException, ValidationException {
        Photo photo = getExpectedPhoto(5);
        photo.setId(-2);

        photoDAO.delete(photo);
    }

    @Test
    @WithData
    public void testDeletePhotoRemovesEntry() throws DAOException, ValidationException {
        int initial = countRows();

        Photo photo = getExpectedPhoto(3);
        assertThat(setPrefix(photoDAO.readAll()), hasItem(photo));

        photoDAO.delete(photo);

        assertEquals(initial - 1, countRows());
        assertThat(setPrefix(photoDAO.readAll()), not(hasItem(photo)));
    }

}
