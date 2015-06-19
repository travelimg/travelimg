package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.repo.AsyncPhotoRepository;
import at.ac.tuwien.qse.sepm.dao.repo.Operation;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoRepository;
import at.ac.tuwien.qse.sepm.dao.repo.impl.PollingFileWatcher;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PhotoServiceImpl implements PhotoService {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int REFRESH_RATE = 5;

    @Autowired
    private PhotoDAO photoDAO;
    @Autowired
    private PollingFileWatcher watcher;
    @Autowired
    private AsyncPhotoRepository photoRepository;

    private Listener listener;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> watcherSchedule = null;

    @Autowired
    private void initializeListeners(AsyncPhotoRepository repository) {
        listener = new Listener();
        repository.addListener((AsyncPhotoRepository.AsyncListener)listener);
        repository.addListener((PhotoRepository.Listener)listener);
    }

    @Override
    public void synchronize() {
        LOGGER.debug("Synchronizing repository");

        // schedule the update in a separate thread with a little delay
        scheduler.schedule(this::synchronizeAndSchedule, 2, TimeUnit.SECONDS);
    }

    private void synchronizeAndSchedule() {
        watcher.refresh();
        try {
            photoRepository.synchronize();
        } catch (DAOException ex) {
            LOGGER.error("Failed to synchronize files", ex);
        }

        // schedule watcher to notify about future changes
        watcherSchedule = scheduler.scheduleAtFixedRate(watcher::refresh, REFRESH_RATE, REFRESH_RATE, TimeUnit.SECONDS);
    }

    public void close() {
        if (watcherSchedule != null) {
            watcherSchedule.cancel(true);
        }
        scheduler.shutdown();
        listener.close();
    }

    @Override
    public void deletePhotos(List<Photo> photos) throws ServiceException {
        if (photos == null) {
            throw new ServiceException("List<Photo> photos is null");
        }
        for (Photo p : photos) {
            LOGGER.debug("Deleting photo {}", p);
            try {
                photoRepository.delete(p.getFile());
            } catch (DAOException e) {
                throw new ServiceException(e);
            }
        }
    }

    @Override
    public List<Photo> getAllPhotos() throws ServiceException {
        LOGGER.debug("Retrieving all photos...");
        try {
            return photoDAO.readAll();
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Photo> getAllPhotos(Predicate<Photo> filter) throws ServiceException {
        LOGGER.debug("Entering getAllPhotos with {}", filter);
        return getAllPhotos()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public void editPhoto(Photo photo) throws ServiceException {
        LOGGER.debug("Entering editPhoto with {}", photo);

        try {
            photoRepository.update(photo);
            LOGGER.info("Successfully updated {}", photo);
        } catch (DAOException ex) {
            LOGGER.error("Updating {} failed due to DAOException", photo);
            throw new ServiceException("Could update photo.", ex);
        }

        LOGGER.debug("Leaving editPhoto with {}", photo);
    }

    @Override
    public void subscribeCreate(Consumer<Photo> callback) {
        photoRepository.addListener(new PhotoRepository.Listener() {
            @Override public void onCreate(PhotoRepository repository, Path file) {
                LOGGER.info("created {}", file);
                try {
                    Photo photo = repository.read(file);
                    callback.accept(photo);
                } catch (DAOException ex) {
                    LOGGER.error("Failed to read photo {}", file);
                }
            }
        });
    }

    @Override
    public void subscribeUpdate(Consumer<Photo> callback) {
        photoRepository.addListener(new PhotoRepository.Listener() {
            @Override public void onUpdate(PhotoRepository repository, Path file) {
                LOGGER.info("updated {}", file);
                try {
                    Photo photo = repository.read(file);
                    callback.accept(photo);
                } catch (DAOException ex) {
                    LOGGER.error("Failed to read photo {}", file);
                }
            }
        });
    }

    @Override
    public void subscribeDelete(Consumer<Path> callback) {
        photoRepository.addListener(new PhotoRepository.Listener() {
            @Override public void onDelete(PhotoRepository repository, Path file) {
                LOGGER.info("deleted {}", file);
                callback.accept(file);
            }
        });
    }

    private class Listener implements
            AsyncPhotoRepository.AsyncListener,
            PhotoRepository.Listener {

        private final ExecutorService executor = Executors.newFixedThreadPool(1);

        public void close() {
            executor.shutdown();
        }

        @Override public void onError(PhotoRepository repository, DAOException error) {
            LOGGER.error("repository error {}", error);
        }

        @Override public void onError(AsyncPhotoRepository repository, Operation operation, DAOException error) {
            LOGGER.warn("failed operation {}", operation);
            LOGGER.error("operation error {}", error);
        }

        @Override public void onQueue(AsyncPhotoRepository repository, Operation operation) {
            LOGGER.info("queued {}", operation);
            LOGGER.info("queue length {}", repository.getQueue().size());
            executor.execute(repository::completeNext);
        }
    }
}
