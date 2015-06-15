package at.ac.tuwien.qse.sepm.dao.repo;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.repo.impl.MockPhotoSerializer;
import at.ac.tuwien.qse.sepm.entities.*;
import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public abstract class PhotoRepositoryTest extends PhotoProviderTest {

    protected abstract PhotoRepository getObject();
    protected abstract Context getContext();

    @Override protected void add(PhotoProvider object, Photo photo) throws DAOException {
        ((PhotoRepository)object).create(photo.getFile(), getContext().getStream(photo));
    }

    @Test
    public void accept_accepted_returnsTrue() throws DAOException {
        PhotoRepository object = getObject();
        assertTrue(object.accepts(getContext().getFile1()));
    }

    @Test
    public void accept_unaccepted_returnsFalse() throws DAOException {
        PhotoRepository object = getObject();
        assertFalse(object.accepts(getContext().getUnacceptedPath()));
    }

    @Test(expected = DAOException.class)
    public void create_unacceptedPath_throws() throws DAOException {
        PhotoRepository object = getObject();
        Path file = getContext().getUnacceptedPath();
        assertFalse(object.accepts(file));
        object.create(file, getContext().getStream1());
    }

    @Test(expected = PhotoAlreadyExistsException.class)
    public void create_existing_throws() throws DAOException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());

        object.create(file, getContext().getStream2());
    }

    @Test
    public void create_valid_persists() throws DAOException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());

        Photo photo = getContext().getPhoto1();
        assertEquals(photo, object.read(file));
    }

    @Test
    public void create_valid_notifiesListener() throws DAOException {
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
    public void update_nonExisting_throws() throws DAOException {
        PhotoRepository object = getObject();
        Photo photo = getContext().getPhoto1();

        object.update(photo);
    }

    @Test
    public void update_minimalData_persists() throws DAOException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());
        Photo modified = new Photo(file, getContext().getMinimalData());

        object.update(modified);
        assertEquals(modified, object.read(file));
    }

    @Test
    public void update_maximalData_persists() throws DAOException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());
        Photo modified = new Photo(file, getContext().getMaximalData());

        object.update(modified);
        assertEquals(modified, object.read(file));
    }

    @Test
    public void update_existing_notifiesListener() throws DAOException {
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
    public void delete_nonExisting_throws() throws DAOException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();

        object.delete(file);
    }

    @Test
    public void delete_existing_persists() throws DAOException {
        PhotoRepository object = getObject();
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());

        object.delete(file);
        assertFalse(object.contains(file));
    }

    @Test
    public void delete_existing_notifiesListener() throws DAOException {
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
    public void removeListener_removed_doesNotNotify() throws DAOException {
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

        public PhotoSerializer getSerializer() {
            MockPhotoSerializer serializer = new MockPhotoSerializer();
            serializer.put(1, getPhotoData1());
            serializer.put(2, getPhotoData2());
            serializer.put(3, getMinimalData());
            serializer.put(4, getMaximalData());
            return serializer;
        }

        public InputStream getStream(Photo photo) throws DAOException {
            ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            getSerializer().update(is, os, photo.getData());
            byte[] data = os.toByteArray();
            ByteArrayInputStream input = new ByteArrayInputStream(data);
            return input;
        }

        public InputStream getStream1() {
            return new ByteArrayInputStream(new byte[] { 1 });
        }

        public InputStream getStream2() {
            return new ByteArrayInputStream(new byte[] { 2 });
        }

        public abstract Path getUnacceptedPath();
    }
}
