package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.repo.*;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.PhotoMetadata;
import at.ac.tuwien.qse.sepm.entities.Rating;
import org.junit.*;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class PhotoFileRepositoryTest extends PhotoRepositoryTest {

    private static final Path DATA_DIR = Paths.get("test/path");
    private static final Path SOURCE_DIR = System.getProperty("os.name").contains("indow")
            ? Paths.get(PhotoFileRepositoryTest.class.getClassLoader().getResource("db/testimages").getPath().substring(1))
            : Paths.get(PhotoFileRepositoryTest.class.getClassLoader().getResource("db/testimages").getPath());

    private PhotoFileRepository object;
    private PollingFileWatcher watcher;

    @Before
    public void setUp() {
        FileManager fileManager = new MockFileManager();
        watcher = new PollingFileWatcher(fileManager);
        watcher.register(DATA_DIR);
        watcher.getExtensions().add("jpg");
        PhotoSerializer serializer = new JpegSerializer();
        object = new PhotoFileRepository(watcher, serializer, fileManager);
    }

    @Override protected PhotoFileRepository getObject() {
        return object;
    }

    @Test
    @Override public void create_valid_notifiesListener() throws DAOException {
        Path file = getContext().getFile1();
        MockListener listener = new MockListener();
        object.addListener(listener);

        object.create(file, getContext().getStream1());
        watcher.refresh();

        assertTrue(listener.getUpdateNotifications().isEmpty());
        assertTrue(listener.getDeleteNotifications().isEmpty());
        assertTrue(listener.getErrorNotifications().isEmpty());
        assertEquals(1, listener.getCreateNotifications().size());
        assertEquals(object, listener.getCreateNotifications().get(0).getRepository());
        assertEquals(file, listener.getCreateNotifications().get(0).getFile());
    }

    @Test
    @Override public void update_existing_notifiesListener() throws DAOException {
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());
        watcher.refresh();
        MockListener listener = new MockListener();
        object.addListener(listener);
        Photo modified = getContext().getModified1();

        object.update(modified);
        watcher.refresh();

        assertTrue(listener.getCreateNotifications().isEmpty());
        assertTrue(listener.getDeleteNotifications().isEmpty());
        assertTrue(listener.getErrorNotifications().isEmpty());
        assertEquals(1, listener.getUpdateNotifications().size());
        assertEquals(object, listener.getUpdateNotifications().get(0).getRepository());
        assertEquals(file, listener.getUpdateNotifications().get(0).getFile());
    }

    @Test
    @Override public void delete_existing_notifiesListener() throws DAOException {
        Path file = getContext().getFile1();
        object.create(file, getContext().getStream1());
        watcher.refresh();
        MockListener listener = new MockListener();
        object.addListener(listener);

        object.delete(file);
        watcher.refresh();

        assertTrue(listener.getCreateNotifications().isEmpty());
        assertTrue(listener.getUpdateNotifications().isEmpty());
        assertTrue(listener.getErrorNotifications().isEmpty());
        assertEquals(1, listener.getDeleteNotifications().size());
        assertEquals(object, listener.getDeleteNotifications().get(0).getRepository());
        assertEquals(file, listener.getDeleteNotifications().get(0).getFile());
    }

    @Override protected Context getContext() {
        return new Context() {
            @Override public Path getUnacceptedPath() {
                return Paths.get("X:yz.jpg");
            }

            @Override public Path getFile1() {
                return DATA_DIR.resolve("1.jpg");
            }

            @Override public Path getFile2() {
                return DATA_DIR.resolve("2.jpg");
            }

            @Override public InputStream getStream1() {
                try {
                    return Files.newInputStream(SOURCE_DIR.resolve("6.jpg"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override public InputStream getStream2() {
                try {
                    return Files.newInputStream(SOURCE_DIR.resolve("7.jpg"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override public InputStream getStream(Photo photo) throws DAOException {
                if (photo.equals(getPhoto1())) {
                    return getStream1();
                }
                if (photo.equals(getPhoto2())) {
                    return getStream2();
                }
                throw new DAOException();
            }

            @Override public PhotoMetadata getPhotoData1() {
                PhotoMetadata data = new PhotoMetadata();
                data.setDate(LocalDateTime.of(2005, 9, 11, 15, 43, 55));
                data.setLongitude(-104.99156111111111);
                data.setLatitude(39.73934166666667);
                data.setRating(Rating.NONE);
                data.setPhotographer(null);
                return data;
            }

            public PhotoMetadata getPhotoData2() {
                PhotoMetadata data = new PhotoMetadata();
                data.setDate(LocalDateTime.of(2005, 9, 11, 15, 44, 8));
                data.setLongitude(-104.9916361111111);
                data.setLatitude(39.739336111111116);
                data.setRating(Rating.NONE);
                data.setPhotographer(null);
                return data;
            }
        };
    }
}
