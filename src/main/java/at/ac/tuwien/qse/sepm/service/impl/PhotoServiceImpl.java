package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.JourneyDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.repo.AsyncPhotoRepository;
import at.ac.tuwien.qse.sepm.dao.repo.Operation;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoRepository;
import at.ac.tuwien.qse.sepm.dao.repo.impl.CachedPhotoRepository;
import at.ac.tuwien.qse.sepm.dao.repo.impl.PollingFileWatcher;
import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PhotoServiceImpl implements PhotoService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private PhotoDAO photoDAO;
    @Autowired
    private CachedPhotoRepository photoRepository;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    private void initializeWatcher(PollingFileWatcher watcher) {
        Listener listener = new Listener();
        photoRepository.addListener((AsyncPhotoRepository.AsyncListener)listener);
        photoRepository.addListener((PhotoRepository.Listener)listener);

        int REFRESH_RATE = 5;
        scheduler.scheduleAtFixedRate(watcher::refresh, REFRESH_RATE, REFRESH_RATE, TimeUnit.SECONDS);
    }

    public void close() {
        scheduler.shutdown();
    }

    public void deletePhotos(List<Photo> photos) throws ServiceException {
        if (photos == null) {
            throw new ServiceException("List<Photo> photos is null");
        }
        for (Photo p : photos) {
            LOGGER.debug("Deleting photo {}", p);
            try {
                photoDAO.delete(p);
            } catch (DAOException e) {
                throw new ServiceException(e);
            } catch (ValidationException e) {
                throw new ServiceException("Failed to validate entity", e);
            }
        }
    }


    @Override
    public void editPhotos(List<Photo> photos, Photo photo) throws ServiceException {
        if (photos == null) {
            throw new ServiceException("List<Photo> photos is null");
        }
        for (Photo p : photos) {
            LOGGER.debug("Updating photo {}", p);
            try {
                //TODO update all attributes
                p.getData().setPlace(photo.getData().getPlace());
                photoDAO.update(p);
            } catch (DAOException e) {
                throw new ServiceException(e);
            } catch (ValidationException e) {
                throw new ServiceException("Failed to validate entity", e);
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
            photoDAO.update(photo);
            LOGGER.info("Successfully updated {}", photo);
        } catch (DAOException ex) {
            LOGGER.error("Updating {} failed due to DAOException", photo);
            throw new ServiceException("Could update photo.", ex);
        } catch (ValidationException ex) {
            LOGGER.error("Updating {} failed due to ValidationException", photo);
            throw new ServiceException("Could not update photo.", ex);
        }

        LOGGER.debug("Leaving editPhoto with {}", photo);
    }

    private static class Listener implements
            AsyncPhotoRepository.AsyncListener,
            PhotoRepository.Listener {

        private static final ExecutorService executor = Executors.newCachedThreadPool();

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
            executor.execute(repository::completeNext);
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
