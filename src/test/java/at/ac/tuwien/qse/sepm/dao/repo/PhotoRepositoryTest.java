package at.ac.tuwien.qse.sepm.dao.repo;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public abstract class PhotoRepositoryTest {

    protected abstract PhotoRepository getObject();
    protected abstract Context getContext();

    @Test
    public void check_nonExisting_returnsNull() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();

        PhotoInfo info = object.check(file);
        assertNull(info);
    }

    @Test
    public void check_existing_returnsNotNull() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());

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
        Path file1 = getContext().getFile1();
        Path file2 = getContext().getFile2();
        object.create(file1, getContext().getStream1());
        object.create(file2, getContext().getStream2());

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
        Path file = getContext().getFile1();

        object.read(file);
    }

    @Test
    public void read_existing_returnsPhoto() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());

        Photo photo = getContext().getPhoto1();
        assertEquals(photo, object.read(file));
    }

    @Test
    public void readAll_nothingAdded_empty() throws PersistenceException {
        PhotoRepository object = getObject();

        assertTrue(object.readAll().isEmpty());
    }

    @Test
    public void readAll_someExisting_returnsPhotos() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file1 = getContext().getFile1();
        Path file2 = getContext().getFile2();
        Photo photo1 = getContext().getPhoto1();
        Photo photo2 = getContext().getPhoto2();
        object.create(file1, getContext().getStream1());
        object.create(file2, getContext().getStream2());

        Collection<Photo> photos = object.readAll();
        assertEquals(2, photos.size());
        assertTrue(photos.contains(photo1));
        assertTrue(photos.contains(photo2));
    }

    @Test(expected = PhotoNotFoundException.class)
    public void create_invalidPath_throws() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = Paths.get("X:/yz");

        object.create(file, getContext().getStream1());
    }

    @Test(expected = PhotoAlreadyExistsException.class)
    public void create_existing_throws() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());

        object.create(file, getContext().getStream2());
    }

    @Test
    public void create_valid_persists() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());

        Photo photo = getContext().getPhoto1();
        assertEquals(photo, object.read(file));
    }

    @Test
    public void create_valid_updatesModificationTime() throws PersistenceException {
        PhotoRepository object = getObject();
        LocalDateTime now = LocalDateTime.now();
        Path file = getContext().getFile1();

        object.create(file, getContext().getStream1());
        assertTrue(!object.check(file).getModified().isBefore(now));
    }

    @Test
    public void create_valid_notifiesListener() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());
        MockListener listener = new MockListener();
        object.addListener(listener);
        Photo modified = getContext().getPhoto1Modified();

        object.update(modified);

        assertTrue(listener.getCreateNotifications().isEmpty());
        assertTrue(listener.getDeleteNotifications().isEmpty());
        assertTrue(listener.getErrorNotifications().isEmpty());
        assertEquals(1, listener.getUpdateNotifications().size());
        assertEquals(object, listener.getUpdateNotifications().get(0).getRepository());
        assertEquals(file, listener.getUpdateNotifications().get(0).getFile());
    }

    @Test(expected = PhotoNotFoundException.class)
    public void update_nonExisting_throws() throws PersistenceException {
        PhotoRepository object = getObject();
        Photo photo = getContext().getPhoto1();

        object.update(photo);
    }

    @Test
    public void update_existing_persists() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());
        Photo modified = getContext().getPhoto1Modified();

        object.update(modified);
        assertEquals(modified, object.read(file));
    }

    @Test
    public void update_existing_updatesModificationTime() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());
        LocalDateTime now = LocalDateTime.now();
        Photo modified = getContext().getPhoto1Modified();

        object.update(modified);
        assertTrue(!object.check(file).getModified().isBefore(now));
    }

    @Test
    public void update_existing_notifiesListener() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        MockListener listener = new MockListener();
        object.addListener(listener);

        object.create(file, getContext().getStream1());

        assertTrue(listener.getUpdateNotifications().isEmpty());
        assertTrue(listener.getDeleteNotifications().isEmpty());
        assertTrue(listener.getErrorNotifications().isEmpty());
        assertEquals(1, listener.getCreateNotifications().size());
        assertEquals(object, listener.getCreateNotifications().get(0).getRepository());
        assertEquals(file, listener.getCreateNotifications().get(0).getFile());
    }

    @Test(expected = PhotoNotFoundException.class)
    public void delete_nonExisting_throws() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();

        object.delete(file);
    }

    @Test
    public void delete_existing_persists() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());

        object.delete(file);
        assertNull(object.check(file));
    }

    @Test
    public void delete_existing_notifiesListener() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());
        MockListener listener = new MockListener();
        object.addListener(listener);

        object.delete(file);

        assertTrue(listener.getCreateNotifications().isEmpty());
        assertTrue(listener.getUpdateNotifications().isEmpty());
        assertTrue(listener.getErrorNotifications().isEmpty());
        assertEquals(1, listener.getDeleteNotifications().size());
        assertEquals(object, listener.getDeleteNotifications().get(0).getRepository());
        assertEquals(file, listener.getDeleteNotifications().get(0).getFile());
    }

    @Test
    public void removeListener_removed_doesNotNotify() throws PersistenceException {
        PhotoRepository object = getObject();
        MockListener listener = new MockListener();
        object.addListener(listener);
        Path file = getContext().getFile1();
        Photo modified = getContext().getPhoto1Modified();

        object.removeListener(listener);
        object.create(file, getContext().getStream1());
        object.update(modified);
        object.delete(file);

        assertTrue(listener.getCreateNotifications().isEmpty());
        assertTrue(listener.getUpdateNotifications().isEmpty());
        assertTrue(listener.getDeleteNotifications().isEmpty());
        assertTrue(listener.getErrorNotifications().isEmpty());
    }
}
