package at.ac.tuwien.qse.sepm.dao.repo;

import at.ac.tuwien.qse.sepm.entities.Rating;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public abstract class PhotoRepositoryTest {

    protected abstract PhotoRepository getObject();

    @Test
    public void check_nonExisting_returnsNull() throws PersistenceException {
        PhotoRepository object = getObject();

        PhotoInfo info = object.check(getPhotoFile1());
        assertNull(info);
    }

    @Test
    public void check_existing_returnsNotNull() throws PersistenceException {
        PhotoRepository object = getObject();
        object.create(getPhotoFile1(), getPhotoStream1());

        PhotoInfo info = object.check(getPhotoFile1());
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
        object.create(getPhotoFile1(), getPhotoStream1());
        object.create(getPhotoFile2(), getPhotoStream2());

        Collection<Path> files = object.checkAll().stream()
                .map(PhotoInfo::getFile)
                .collect(Collectors.toList());
        assertEquals(2, files.size());
        assertTrue(files.contains(getPhotoFile1()));
        assertTrue(files.contains(getPhotoFile2()));
    }

    @Test(expected = PhotoNotFoundException.class)
    public void read_nonExisting_throws() throws PersistenceException {
        PhotoRepository object = getObject();

        object.read(getPhotoFile1());
    }

    @Test
    public void read_existing_returnsPhoto() throws PersistenceException {
        PhotoRepository object = getObject();
        object.create(getPhotoFile1(), getPhotoStream1());

        assertEquals(getPhoto1(), object.read(getPhotoFile1()));
    }

    @Test
    public void readAll_nothingAdded_empty() throws PersistenceException {
        PhotoRepository object = getObject();

        assertTrue(object.readAll().isEmpty());
    }

    @Test
    public void readAll_someExisting_returnsPhotos() throws PersistenceException {
        PhotoRepository object = getObject();
        object.create(getPhotoFile1(), getPhotoStream1());
        object.create(getPhotoFile2(), getPhotoStream2());

        Collection<Photo> photos = object.readAll();
        assertEquals(2, photos.size());
        assertTrue(photos.contains(getPhoto1()));
        assertTrue(photos.contains(getPhoto2()));

    }

    @Test(expected = PhotoNotFoundException.class)
    public void create_invalidPath_throws() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = Paths.get("X:/yz");

        object.create(file, getPhotoStream1());
    }

    @Test(expected = PhotoAlreadyExistsException.class)
    public void create_existing_throws() throws PersistenceException {
        PhotoRepository object = getObject();
        object.create(getPhotoFile1(), getPhotoStream1());

        object.create(getPhotoFile1(), getPhotoStream2());
    }

    @Test
    public void create_valid_persists() throws PersistenceException {
        PhotoRepository object = getObject();
        object.create(getPhotoFile1(), getPhotoStream1());

        assertEquals(getPhoto1(), object.read(getPhotoFile1()));
    }

    @Test
    public void create_valid_updatesModificationTime() throws PersistenceException {
        PhotoRepository object = getObject();
        LocalDateTime now = LocalDateTime.now();

        object.create(getPhotoFile1(), getPhotoStream1());
        assertTrue(!object.check(getPhotoFile1()).getModified().isBefore(now));
    }

    @Test
    public void create_valid_notifiesListener() throws PersistenceException {
        PhotoRepository object = getObject();
        object.addListener(new PhotoRepository.Listener() {
            @Override public void onCreate(PhotoRepository repository, Path file) {
                assertEquals(getPhotoFile1(), file);
                // TODO: test if repository is equal to the repository object
            }

            @Override public void onUpdate(PhotoRepository repository, Path file) {
                fail();
            }

            @Override public void onDelete(PhotoRepository repository, Path file) {
                fail();
            }

            @Override public void onError(PhotoRepository repository, PersistenceException error) {
                throw new RuntimeException(error);
            }
        });
        object.create(getPhotoFile1(), getPhotoStream1());
        // TODO: test if onCreate was actually called
    }

    @Test(expected = PhotoNotFoundException.class)
    public void update_nonExisting_throws() throws PersistenceException {
        PhotoRepository object = getObject();

        object.update(getPhoto1());
    }

    @Test
    public void update_existing_persists() throws PersistenceException {
        PhotoRepository object = getObject();
        object.create(getPhotoFile1(), getPhotoStream1());

        object.update(getPhoto2(getPhotoFile1()));
        assertEquals(getPhoto2(getPhotoFile1()), object.read(getPhotoFile1()));
    }

    @Test
    public void update_existing_updatesModificationTime() throws PersistenceException {
        PhotoRepository object = getObject();
        object.create(getPhotoFile1(), getPhotoStream1());
        LocalDateTime now = LocalDateTime.now();

        object.update(getPhoto2(getPhotoFile1()));
        assertTrue(!object.check(getPhotoFile1()).getModified().isBefore(now));
    }

    @Test
    public void update_existing_notifiesListener() throws PersistenceException {
        PhotoRepository object = getObject();
        object.create(getPhotoFile1(), getPhotoStream1());
        object.addListener(new PhotoRepository.Listener() {
            @Override public void onCreate(PhotoRepository repository, Path file) {
                fail();
            }

            @Override public void onUpdate(PhotoRepository repository, Path file) {
                assertEquals(getPhotoFile1(), file);
                // TODO: test if repository is equal to the repository object
            }

            @Override public void onDelete(PhotoRepository repository, Path file) {
                fail();
            }

            @Override public void onError(PhotoRepository repository, PersistenceException error) {
                throw new RuntimeException(error);
            }
        });
        object.update(getPhoto2(getPhotoFile1()));
        // TODO: test if onUpdate was actually called
    }

    @Test(expected = PhotoNotFoundException.class)
    public void delete_nonExisting_throws() throws PersistenceException {
        PhotoRepository object = getObject();

        object.delete(getPhotoFile1());
    }

    @Test
    public void delete_existing_persists() throws PersistenceException {
        PhotoRepository object = getObject();
        object.create(getPhotoFile1(), getPhotoStream1());

        object.delete(getPhotoFile1());
        assertNull(object.check(getPhotoFile1()));
    }

    @Test
    public void delete_existing_notifiesListener() throws PersistenceException {
        PhotoRepository object = getObject();
        object.create(getPhotoFile1(), getPhotoStream1());
        object.addListener(new PhotoRepository.Listener() {
            @Override public void onCreate(PhotoRepository repository, Path file) {
                fail();
            }

            @Override public void onUpdate(PhotoRepository repository, Path file) {
                fail();
            }

            @Override public void onDelete(PhotoRepository repository, Path file) {
                assertEquals(getPhotoFile1(), file);
                // TODO: test if repository is equal to the repository object
            }

            @Override public void onError(PhotoRepository repository, PersistenceException error) {
                throw new RuntimeException(error);
            }
        });
        object.delete(getPhotoFile1());
        // TODO: test if onUpdate was actually called
    }

    @Test
    public void removeListener_removed_doesNotNotify() throws PersistenceException {
        PhotoRepository object = getObject();
        PhotoRepository.Listener listener = new PhotoRepository.Listener() {
            @Override public void onCreate(PhotoRepository repository, Path file) {
                fail();
            }

            @Override public void onUpdate(PhotoRepository repository, Path file) {
                fail();
            }

            @Override public void onDelete(PhotoRepository repository, Path file) {
                fail();
            }

            @Override public void onError(PhotoRepository repository, PersistenceException error) {
                throw new RuntimeException(error);
            }
        };
        object.addListener(listener);

        object.removeListener(listener);
        object.create(getPhotoFile1(), getPhotoStream1());
        object.update(getPhoto2(getPhotoFile1()));
        object.delete(getPhotoFile1());
    }

    protected abstract Path getPhotoFile1();
    protected abstract Path getPhotoFile2();

    protected Photo read(Path file, InputStream stream) throws IOException {
        int index = stream.read();
        switch (index) {
            case 1 : return getPhoto1(file);
            case 2 : return getPhoto2(file);
            default : throw new IOException("Invalid format.");
        }
    }

    protected void update(Photo photo, OutputStream stream) throws IOException {
        switch (photo.getRating()) {
            case GOOD :
                stream.write(1);
                break;
            case NEUTRAL :
                stream.write(2);
                break;
            default :
                stream.write(0);
                break;
        }
    }

    protected Photo getPhoto1() {
        return getPhoto1(getPhotoFile1());
    }
    protected Photo getPhoto2() {
        return getPhoto2(getPhotoFile2());
    }
    protected Photo getPhoto1(Path file) {
        Photo photo = new Photo(file);
        photo.setDate(LocalDateTime.of(1993, 3, 30, 7, 5));
        photo.setLongitude(17);
        photo.setLatitude(18);
        photo.setAltitude(19);
        photo.setRating(Rating.GOOD);
        photo.setPhotographer("Kris");
        photo.getTags().add("architecture");
        photo.getTags().add("people");
        return photo;
    }
    protected Photo getPhoto2(Path file) {
        Photo photo = new Photo(file);
        photo.setDate(LocalDateTime.of(2014, 8, 14, 15, 36));
        photo.setLongitude(42);
        photo.setLatitude(43);
        photo.setAltitude(44);
        photo.setRating(Rating.NEUTRAL);
        photo.setPhotographer("Lukas");
        photo.getTags().add("food");
        photo.getTags().add("india");
        return photo;
    }
    protected InputStream getPhotoStream1() {
        return new ByteArrayInputStream(new byte[] { 1 });
    }
    protected InputStream getPhotoStream2() {
        return new ByteArrayInputStream(new byte[] { 2 });
    }
}
