package at.ac.tuwien.qse.sepm.dao.repo;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public abstract class PhotoCacheTest {

    protected abstract PhotoCache getObject();

    @Test
    public void check_nonExisting_returnsNull() throws PersistenceException {
        PhotoCache object = getObject();

        PhotoInfo info = object.check(Paths.get("some/path.jpg"));
        assertNull(info);
    }

    @Test
    public void check_existing_returnsNotNull() throws PersistenceException {
        PhotoCache object = getObject();
        Path file = Paths.get("some/path.jpg");
        Photo photo = new Photo(file);
        object.put(photo);

        PhotoInfo info = object.check(file);
        assertNotNull(info);
    }

    @Test
    public void checkAll_nothingAdded_empty() throws PersistenceException {
        PhotoCache object = getObject();

        assertTrue(object.checkAll().isEmpty());
    }

    @Test
    public void checkAll_someExisting_returnsInfos() throws PersistenceException {
        PhotoCache object = getObject();
        Path file1 = Paths.get("some/path.jpg");
        Path file2 = Paths.get("other/path.jpg");
        Photo photo1 = new Photo(file1);
        Photo photo2 = new Photo(file2);
        object.put(photo1);
        object.put(photo2);

        Collection<Path> files = object.checkAll().stream()
                .map(PhotoInfo::getFile)
                .collect(Collectors.toList());
        assertEquals(2, files.size());
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file2));
    }

    @Test(expected = PhotoNotFoundException.class)
    public void read_nonExisting_throws() throws PersistenceException {
        PhotoCache object = getObject();
        Path file = Paths.get("some/path.jpg");

        object.read(file);
    }

    @Test
    public void read_existing_returnsPhoto() throws PersistenceException {
        PhotoCache object = getObject();
        Path file = Paths.get("some/path.jpg");
        Photo photo = new Photo(file);
        object.put(photo);

        assertEquals(photo, object.read(file));
    }

    @Test
    public void readAll_nothingAdded_empty() throws PersistenceException {
        PhotoCache object = getObject();

        assertTrue(object.readAll().isEmpty());
    }

    @Test
    public void readAll_someExisting_returnsPhotos() throws PersistenceException {
        PhotoCache object = getObject();
        Path file1 = Paths.get("some/path.jpg");
        Path file2 = Paths.get("other/path.jpg");
        Photo photo1 = new Photo(file1);
        Photo photo2 = new Photo(file2);
        object.put(photo1);
        object.put(photo2);

        Collection<Photo> photos = object.readAll();
        assertEquals(2, photos.size());
        assertTrue(photos.contains(photo1));
        assertTrue(photos.contains(photo2));
    }

    @Test
    public void put_valid_persists() throws PersistenceException {
        PhotoCache object = getObject();
        Path file = Paths.get("some/path.jpg");
        Photo photo = new Photo(file);

        object.put(photo);
        assertEquals(photo, object.read(file));
    }

    @Test
    public void put_valid_updatesModificationTime() throws PersistenceException {
        PhotoCache object = getObject();
        Path file = Paths.get("some/path.jpg");
        Photo photo1 = new Photo(file);
        Photo photo2 = new Photo(file);

        LocalDateTime now = LocalDateTime.now();
        object.put(photo1);
        object.put(photo2);
        PhotoInfo info = object.check(file);
        assertTrue(!info.getModified().isBefore(now));
    }

    @Test(expected = PhotoNotFoundException.class)
    public void remove_nonExisting_throws() throws PersistenceException {
        PhotoCache object = getObject();
        Path file = Paths.get("some/path.jpg");

        object.remove(file);
    }

    @Test
    public void remove_existing_persists() throws PersistenceException {
        PhotoCache object = getObject();
        Path file = Paths.get("some/path.jpg");
        Photo photo = new Photo(file);
        object.put(photo);

        object.remove(file);
        assertTrue(object.checkAll().isEmpty());
    }
}
