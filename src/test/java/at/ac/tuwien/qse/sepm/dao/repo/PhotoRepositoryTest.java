package at.ac.tuwien.qse.sepm.dao.repo;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public abstract class PhotoRepositoryTest {

    protected abstract PhotoRepository getObject();

    @Test
    public void check_nonExisting_returnsNull() throws PersistenceException {
        PhotoRepository object = getObject();

        PhotoInfo info = object.check(Paths.get("some/path.jpg"));
        assertNull(info);
    }

    @Test
    public void check_existing_returnsNotNull() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = Paths.get("some/path.jpg");
        object.create(file, getPhotoStream1());

        PhotoInfo info = object.check(file);
        assertNotNull(info);
    }

    @Test
    public void checkAll_nothingAdded_empty() throws PersistenceException {
        PhotoRepository object = getObject();

        assertTrue(object.checkAll().isEmpty());
    }

    @Test
    public void checkAll_someExisting_returnsInfos() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file1 = Paths.get("some/path.jpg");
        Path file2 = Paths.get("other/path.jpg");
        object.create(file1, getPhotoStream1());
        object.create(file2, getPhotoStream2());

        Collection<Path> files = object.checkAll().stream()
                .map(PhotoInfo::getFile)
                .collect(Collectors.toList());
        assertEquals(2, files.size());
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file2));
    }

    @Test(expected = PhotoNotFoundException.class)
    public void read_nonExisting_throws() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = Paths.get("some/path.jpg");

        object.read(file);
    }

    @Test
    public void read_existing_returnsPhoto() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = Paths.get("some/path.jpg");
        object.create(file, getPhotoStream1());

        assertEquals(getPhoto1(file), object.read(file));
    }

    @Test
    public void readAll_nothingAdded_empty() throws PersistenceException {
        PhotoRepository object = getObject();

        assertTrue(object.readAll().isEmpty());
    }

    @Test
    public void readAll_someExisting_returnsPhotos() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file1 = Paths.get("some/path.jpg");
        Path file2 = Paths.get("other/path.jpg");
        object.create(file1, getPhotoStream1());
        object.create(file2, getPhotoStream2());

        Collection<Photo> photos = object.readAll();
        assertEquals(2, photos.size());
        assertTrue(photos.contains(getPhoto1(file1)));
        assertTrue(photos.contains(getPhoto2(file2)));
    }

    @Test(expected = PhotoNotFoundException.class)
    public void create_invalidPath_throws() {
        // TODO
    }

    @Test(expected = PhotoAlreadyExistsException.class)
    public void create_existing_throws() {
        // TODO
    }

    @Test
    public void create_valid_persists() {
        // TODO
    }

    @Test
    public void create_valid_updatesModificationTime() {
        // TODO
    }

    @Test
    public void create_valid_notifiesListener() {
        // TODO
    }

    @Test(expected = PhotoNotFoundException.class)
    public void update_nonExisting_throws() {
        // TODO
    }

    @Test
    public void update_existing_persists() {
        // TODO
    }

    @Test
    public void update_existing_updatesModificationTime() {
        // TODO
    }

    @Test
    public void update_existing_notifiesListener() {
        // TODO
    }

    @Test(expected = PhotoNotFoundException.class)
    public void delete_nonExisting_throws() {
        // TODO
    }

    @Test
    public void delete_existing_persists() {
        // TODO
    }

    @Test
    public void delete_existing_notifiesListener() {
        // TODO
    }

    private Photo getPhoto1(Path file) {
        Photo photo = new Photo(file);
        // TODO: set test data for photo
        return photo;
    }

    private Photo getPhoto2(Path file) {
        Photo photo = new Photo(file);
        // TODO: set test data for photo
        return photo;
    }

    private InputStream getPhotoStream1() {
        return new ByteArrayInputStream(new byte[] { 1 });
    }

    private InputStream getPhotoStream2() {
        return new ByteArrayInputStream(new byte[] { 2 });
    }
}
