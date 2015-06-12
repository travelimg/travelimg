package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.*;
import at.ac.tuwien.qse.sepm.entities.Rating;
import org.apache.commons.io.FileUtils;
import org.junit.Before;

import java.io.File;
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
        watcher = new PollingFileWatcher();
        watcher.register(DATA_DIR);
        watcher.getExtensions().add("jpg");
        PhotoSerializer serializer = new JpegSerializer();
        object = new PhotoFileRepository(watcher, serializer, fileManager);
    }

    @Override protected PhotoFileRepository getObject() {
        return object;
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

            @Override public InputStream getStream(Photo photo) throws PersistenceException {
                if (photo.equals(getPhoto1())) {
                    return getStream1();
                }
                if (photo.equals(getPhoto2())) {
                    return getStream2();
                }
                throw new PersistenceException();
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
                data.setDate(LocalDateTime.of(2014, 8, 14, 15, 36));
                data.setLongitude(42.0);
                data.setLatitude(43.0);
                data.setRating(Rating.NEUTRAL);
                data.setPhotographer("Lukas");
                data.getTags().clear();
                data.getTags().add("usa");
                data.getTags().add("nature");
                return data;
            }
        };
    }
}
