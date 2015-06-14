package at.ac.tuwien.qse.sepm.dao.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.util.TestIOHandler;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

@UsingTable("Photo")
public class JDBCPhotoDAOTest extends AbstractJDBCDAOTest {

    private static final Photographer defaultPhotographer = new Photographer(1, "Test Photographer");
    private static final Place defaultPlace = new Place(1, "Unkown place", "Unknown place", 0.0, 0.0, null);
    private static final String dataDir = Paths.get(System.getProperty("java.io.tmpdir"), "travelimg").toString();
    private static final String sourceDir = Paths.get(System.getProperty("os.name").contains("indow") ?
            JDBCPhotoDAOTest.class.getClassLoader().getResource("db/testimages").getPath().substring(1) :
            JDBCPhotoDAOTest.class.getClassLoader().getResource("db/testimages").getPath()).toString();
    @Autowired
    PhotoDAO photoDAO;
    @Autowired
    TestIOHandler ioHandler;
    private YearMonth expectedMonths[] = new YearMonth[]{
            YearMonth.of(2005, 9),
            YearMonth.of(2015, 3),
            YearMonth.of(2015, 5)
    };

    private Photo expectedPhotos[] = new Photo[]{
            new Photo(1, defaultPhotographer, Paths.get(dataDir, "2015", "03", "06", "1.jpg").toString(), Rating.NONE, LocalDateTime.of(2015, 3, 6, 0, 0, 0), 41.5, 19.5, defaultPlace),
            new Photo(2, defaultPhotographer, Paths.get(dataDir, "2005", "09", "11", "2.jpg").toString(), Rating.NONE, LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9, defaultPlace),
            new Photo(3, defaultPhotographer, Paths.get(dataDir, "2005", "09", "11", "3.jpg").toString(), Rating.NONE, LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9, defaultPlace),
            new Photo(4, defaultPhotographer, Paths.get(dataDir, "2005", "09", "11", "4.jpg").toString(), Rating.NONE, LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9, defaultPlace),
            new Photo(5, defaultPhotographer, Paths.get(dataDir, "2015", "03", "04", "5.jpg").toString(), Rating.NONE, LocalDateTime.of(2015, 3, 4, 0, 0, 0), 12.0, 12.0, defaultPlace),
            new Photo(6, defaultPhotographer, Paths.get(dataDir, "2005", "09", "11", "4.jpg").toString(), Rating.NONE, LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9, defaultPlace),
            new Photo(7, defaultPhotographer, Paths.get(dataDir, "2015", "05", "17", "6.jpg").toString(), Rating.NONE, LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115, defaultPlace),
            new Photo(8, defaultPhotographer, Paths.get(dataDir, "2015", "05", "17", "7.jpg").toString(), Rating.NONE, LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115, defaultPlace),
            new Photo(9, defaultPhotographer, Paths.get(dataDir, "2015", "05", "17", "8.jpg").toString(), Rating.NONE, LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115, defaultPlace),
    };

    private Photo inputPhotos[] = new Photo[]{
            new Photo(7, defaultPhotographer, Paths.get(sourceDir, "6.jpg").toString(), Rating.NONE, LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115, defaultPlace),
            new Photo(8, defaultPhotographer, Paths.get(sourceDir, "7.jpg").toString(), Rating.NONE, LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115, defaultPlace),
            new Photo(9, defaultPhotographer, Paths.get(sourceDir, "8.jpg").toString(), Rating.NONE, LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115, defaultPlace),
    };

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
        for (Photo photo : photos) {
            setDataPrefixDir(photo);
        }

        return photos;
    }

    @Before
    public void setUp() throws Exception {
        ioHandler.reset();
        FileUtils.deleteDirectory(new File(dataDir));
        FileUtils.copyDirectory(new File(Paths.get(sourceDir, "prepared").toString()), new File(dataDir));
    }

    @Test
    public void testEmpty() throws DAOException {
        assertThat(countRows(), is(0));
    }

    @Test
    @WithData
    public void testWithData() throws DAOException {
        assertThat(countRows(), is(6));
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
        System.out.println(photo);
        Photo expected = getExpectedPhoto(photo.getId());

        Photo value = photoDAO.create(photo);

        expected.setId(value.getId());
        assertThat(expected, equalTo(value));
    }

    @Test
    @WithData
    public void testCreateImageFileIsCopied() throws DAOException, ValidationException {
        Photo photo = getInputPhoto(1);
        Photo expected = getExpectedPhoto(photo.getId());

        Photo value = photoDAO.create(photo);

        assertThat(expected.getPath(), equalTo(value.getPath()));

        Pair<Path, Path> copyOperation = ioHandler.copiedFiles.get(0);

        // ensure that the files are copied correctly
        assertThat(copyOperation.getKey().toString(), equalTo(getInputPhoto(1).getPath()));
        assertThat(copyOperation.getValue().toString(), equalTo(expected.getPath()));

    }

    @Test(expected = DAOException.class)
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

        for (Photo photo : photos) {
            setDataPrefixDir(photo);
            Photo expected = getExpectedPhoto(photo.getId());
            assertThat(expected, equalTo(photo));
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

        assertThat(countRows(), is(initial + 1));
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
        assertThat(photoDAO.getMonthsWithPhotos().size(), is(0));
    }

    @Test
    @WithData
    public void testReadMonthsWithData() throws DAOException {
        assertThat(photoDAO.getMonthsWithPhotos().size(), is(2));

        for (YearMonth month : photoDAO.getMonthsWithPhotos()) {
            assertThat(Arrays.asList(expectedMonths), hasItem(month));
        }
    }

    @Test
    @WithData
    public void testCreateNewMonthsAffectsReadMonths() throws DAOException, ValidationException {
        Photo photo = getInputPhoto(0);

        int initial = photoDAO.getMonthsWithPhotos().size();
        photoDAO.create(photo);
        assertThat(photoDAO.getMonthsWithPhotos().size(), is(initial + 1));
    }

    @Test
    public void testReadPhotosByMonthEmpty() throws DAOException {
        for (YearMonth month : expectedMonths) {
            assertThat(photoDAO.readPhotosByMonth(month).size(), is(0));
        }
    }

    @Test
    @WithData
    public void testReadPhotosByMonthWithData() throws DAOException {
        assertThat(photoDAO.readPhotosByMonth(expectedMonths[0]).size(), is(4));
        assertThat(photoDAO.readPhotosByMonth(expectedMonths[1]).size(), is(2));
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

        boolean didThrow = false;
        try {
            photoDAO.delete(photo);
        } catch (DAOException ex) {
            didThrow = true;
        }

        if (!didThrow) {
            throw new AssertionError("Expected DAOException");
        }

        // ensure that no files were deleted
        assertThat(ioHandler.deletedFiles, empty());

        // ensure that the number of photos did not change
        assertThat(countRows(), is(initial));
    }

    @Test(expected = ValidationException.class)
    @WithData
    public void testDeleteWithInvalidIdThrows() throws DAOException, ValidationException {
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

        // ensure that entry was deleted
        assertThat(countRows(), is(initial - 1));
        assertThat(setPrefix(photoDAO.readAll()), not(hasItem(photo)));

        // ensure that file was deleted
        assertThat(ioHandler.deletedFiles, hasItem(Paths.get(getExpectedPhoto(3).getPath())));
    }
}
