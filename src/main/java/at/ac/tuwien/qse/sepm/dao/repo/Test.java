package at.ac.tuwien.qse.sepm.dao.repo;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.repo.impl.*;
import at.ac.tuwien.qse.sepm.entities.Photo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test {

    private static final int REFRESH_RATE = 5;
    private static final Path DIRECTORY = Paths.get("D:/travelimg");

    private static final Logger LOGGER = LogManager.getLogger();
    public static void main(String[] args) {

        // Setup the file watcher.
        PollingFileWatcher watcher = new PollingFileWatcher();
        watcher.register(DIRECTORY);
        watcher.getExtensions().add("jpeg");
        watcher.getExtensions().add("jpg");
        watcher.getExtensions().add("JPEG");
        watcher.getExtensions().add("JPG");

        // Create the source, cache and top repository service.
        PhotoSerializer serializer = new JpegSerializer();
        PhotoRepository repository = new PhotoFileRepository(watcher, serializer);
        PhotoCache cache = new MemoryPhotoCache();
        AsyncPhotoRepository service = new CachedPhotoRepository(repository, cache);
        Listener listener = new Listener();
        service.addListener((AsyncPhotoRepository.AsyncListener)listener);
        service.addListener((PhotoRepository.Listener)listener);

        // Run the watcher.
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(watcher::refresh, REFRESH_RATE, REFRESH_RATE, TimeUnit.SECONDS);

        // Run the service.
        (new Thread(() -> {
            while (true) {
                service.completeNext();
            }
        })).run();
    }

    private static class Listener implements
            PhotoRepository.Listener,
            AsyncPhotoRepository.AsyncListener {

        @Override public void onCreate(PhotoRepository repository, Path file) {
            LOGGER.info("created {}", file);
            try {
                Collection<Photo> photos = repository.readAll();
                LOGGER.info("repository contains {} photos", photos.size());
                photos.forEach(p -> LOGGER.info(p));
            } catch (DAOException ex) {
                LOGGER.warn("failed read all");
                LOGGER.error(ex);
            }
        }

        @Override public void onUpdate(PhotoRepository repository, Path file) {
            LOGGER.info("updated {}", file);
            try {
                Collection<Photo> photos = repository.readAll();
                LOGGER.info("repository contains {} photos", photos.size());
                photos.forEach(p -> LOGGER.info(p));
            } catch (DAOException ex) {
                LOGGER.warn("failed read all");
                LOGGER.error(ex);
            }
        }

        @Override public void onDelete(PhotoRepository repository, Path file) {
            LOGGER.info("deleted {}", file);
            try {
                Collection<Photo> photos = repository.readAll();
                LOGGER.info("repository contains {} photos", photos.size());
                photos.forEach(p -> LOGGER.info(p));
            } catch (DAOException ex) {
                LOGGER.warn("failed read all");
                LOGGER.error(ex);
            }
        }

        @Override public void onError(PhotoRepository repository, DAOException error) {
            LOGGER.error(error);
        }

        @Override public void onQueue(AsyncPhotoRepository repository, Operation operation) {
            LOGGER.info("queued {}", operation);
            LOGGER.info("queue length {}", repository.getQueue().size());
            repository.getQueue().forEach(op -> LOGGER.info(op));
        }

        @Override public void onComplete(AsyncPhotoRepository repository, Operation operation) {
            LOGGER.info("completed {}", operation);
            LOGGER.info("queue length {}", repository.getQueue().size());
            repository.getQueue().forEach(op -> LOGGER.info(op));
        }

        @Override public void onError(AsyncPhotoRepository repository, Operation operation, DAOException error) {
            LOGGER.error("failed {}", operation);
            LOGGER.error(error);
            LOGGER.info("queue length {}", repository.getQueue().size());
            repository.getQueue().forEach(op -> LOGGER.info(op));
        }
    }
}
