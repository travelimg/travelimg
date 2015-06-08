package at.ac.tuwien.qse.sepm.dao.repo;

import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public abstract class PhotoRepositoryTest extends PhotoProviderTest {

    protected abstract PhotoRepository getObject();
    protected abstract Context getContext();

    @Override protected void add(PhotoProvider object, Photo photo) throws PersistenceException {
        ((PhotoRepository)object).create(photo.getFile(), getContext().getStream(photo));
    }

    @Test(expected = PersistenceException.class)
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
    public void create_valid_notifiesListener() throws PersistenceException {
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
        Photo modified = getContext().getModified1();

        object.update(modified);
        assertEquals(modified, object.read(file));
    }

    @Test
    public void update_existing_notifiesListener() throws PersistenceException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());
        MockListener listener = new MockListener();
        object.addListener(listener);
        Photo modified = getContext().getModified1();

        object.update(modified);

        assertTrue(listener.getCreateNotifications().isEmpty());
        assertTrue(listener.getDeleteNotifications().isEmpty());
        assertTrue(listener.getErrorNotifications().isEmpty());
        assertEquals(1, listener.getUpdateNotifications().size());
        assertEquals(object, listener.getUpdateNotifications().get(0).getRepository());
        assertEquals(file, listener.getUpdateNotifications().get(0).getFile());
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
        assertFalse(object.contains(file));
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
        Photo modified = getContext().getModified1();

        object.removeListener(listener);
        object.create(file, getContext().getStream1());
        object.update(modified);
        object.delete(file);

        assertTrue(listener.getCreateNotifications().isEmpty());
        assertTrue(listener.getUpdateNotifications().isEmpty());
        assertTrue(listener.getDeleteNotifications().isEmpty());
        assertTrue(listener.getErrorNotifications().isEmpty());
    }

    public abstract class Context extends PhotoProviderTest.Context {

        public InputStream getStream(Photo photo) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                update(photo, output);
                byte[] data = output.toByteArray();
                ByteArrayInputStream input = new ByteArrayInputStream(data);
                return input;
            } catch (IOException ex) {
                // This kind of stream should not throw that.
                throw new RuntimeException(ex);
            }
        }

        public InputStream getStream1() {
            return new ByteArrayInputStream(new byte[] { 1 });
        }

        public InputStream getStream2() {
            return new ByteArrayInputStream(new byte[] { 2 });
        }

        public Photo read(Path file, InputStream stream) throws IOException {
            int index = stream.read();
            switch (index) {
                case 1 : return applyData1(new Photo(file));
                case 2 : return applyData2(new Photo(file));
                default : throw new IOException("Invalid format.");
            }
        }

        public void update(Photo photo, OutputStream stream) throws IOException {
            // Just use the rating to map photo to stream content.
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
    }
}
