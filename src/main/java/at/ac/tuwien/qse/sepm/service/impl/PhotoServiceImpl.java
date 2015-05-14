package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.CancelableTask;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PhotoServiceImpl implements PhotoService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired private PhotoDAO photoDAO;
    @Autowired private PhotoTagDAO photoTagDAO;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    public List<Photo> getAllPhotos() throws ServiceException {
        try {
            return photoDAO.readAll();
        } catch (DAOException e) {
            throw new ServiceException(e);
        } catch (ValidationException e) {
            throw new ServiceException("Failed to validate entity", e);
        }
    }

    public List<Tag> getAllTags() throws ServiceException {
        return null;
    }

    public void requestFullscreenMode(List<Photo> photos) throws ServiceException {

    }

    /**
     * delete the delivered List of Photos
     *
     * @param photos the list of photos
     * @throws ServiceException
     */
    public void deletePhotos(List<Photo> photos) throws ServiceException {
        if (photos == null) {
            throw new ServiceException("List<Photo> photos is null");
        }
        for (Photo p : photos) {
            try {
                photoDAO.delete(p);
            } catch (DAOException e) {
                throw new ServiceException(e);
            } catch (ValidationException e) {
                throw new ServiceException("Failed to validate entity", e);
            }
        }
    }

    /**
     * Add Tag <tt>tag</tt> to every photo in list <tt>photos</tt>. If a photo already has this tag,
     * then it will keep it.
     *
     * @param photos must not be null; all elements must not be null; no element.id must be null
     * @param tag    must not be null; tag.id must not be null
     * @throws ServiceException         if an Exception in this or an underlying
     *                                  layer occurs
     */
    public void addTagToPhotos(List<Photo> photos, Tag tag) throws ServiceException {
        LOGGER.debug("Entering addTagToPhotos with {}, {}", photos, tag);
        if (photos == null) {
            throw new ServiceException("List<Photo> photos is null");
        }
        for (Photo photo : photos) {
            try {
                photoTagDAO.createPhotoTag(photo, tag);
            } catch (DAOException ex) {
                LOGGER.error("Photo-Tag-creation with {}, {} failed.", photo, tag);
                throw new ServiceException("Creation of Photo-Tag failed.", ex);
            } catch (ValidationException e) {
                throw new ServiceException("Failed to validate entity", e);
            }
        }
        LOGGER.debug("Leaving addTagToPhotos");
    }

    /**
     * Remove Tag <tt>tag</tt> from all photos in list <tt>photos</tt>. If a photo in the list
     * does not have this tag, then no action will be taken for this photo.
     *
     * @param photos must not be null; all elements must not be null; no element.id must be null
     * @param tag    must not be null; tag.id must not be null
     * @throws ServiceException         if an Exception in this or an underlying
     *                                  layer occurs
     */
    public void removeTagFromPhotos(List<Photo> photos, Tag tag) throws ServiceException {
        LOGGER.debug("Entering removeTagFromPhotos with {}, {}", photos, tag);
        if (photos == null) {
            throw new ServiceException("List<Photo> photos is null");
        }
        for (Photo photo : photos) {
            try {
                photoTagDAO.removeTagFromPhoto(photo, tag);
            } catch (DAOException ex) {
                LOGGER.error("Removal of Photo-Tag with {}, {} failed.", photo, tag);
                throw new ServiceException("Photo-Tag removal failed.", ex);
            } catch (ValidationException e) {
                throw new ServiceException("Failed to validate entity", e);
            }
        }
        LOGGER.debug("Leaving removeTagFromPhotos");
    }

    /**
     * Return list of all tags which are currently set for <tt>photo</tt>.
     *
     * @param photo must not be null; photo.id must not be null;
     * @return List with all tags which are linked to <tt>photo</tt> as a PhotoTag;
     * If no tag exists, return an empty List.
     * @throws ServiceException         if an exception occurs on this or an underlying layer
     */
    public List<Tag> getTagsForPhoto(Photo photo) throws ServiceException {
        LOGGER.debug("Entering getTagsForPhoto with {}", photo);
        List<Tag> tagList;
        try {
            tagList = photoTagDAO.readTagsByPhoto(photo);
            LOGGER.info("Successfully retrieved tags for {}", photo);
        } catch (DAOException ex) {
            LOGGER.error("Retrieving tags for {} failed due to DAOException", photo);
            throw new ServiceException("Could not retrieve tags for photo.", ex);
        } catch (ValidationException e) {
            throw new ServiceException("Failed to validate entity", e);
        }
        LOGGER.debug("Leaving getTagsForPhoto with {}", photo);
        return tagList;
    }

    public void editPhotos(List<Photo> photos, Photo photo) throws ServiceException {

    }

    @Override
    public Cancelable loadPhotosByMonth(YearMonth month, Consumer<Photo> callback,
            ErrorHandler<ServiceException> errorHandler) {
        LOGGER.debug("Loading photos");
        AsyncLoader loader = new AsyncLoader(month, callback, errorHandler);
        executorService.submit(loader);

        return loader;
    }

    private class AsyncLoader extends CancelableTask {
        private YearMonth month;
        private Consumer<Photo> callback;
        private ErrorHandler<ServiceException> errorHandler;

        public AsyncLoader(YearMonth month, Consumer<Photo> callback,
                ErrorHandler<ServiceException> errorHandler) {
            super();
            this.month = month;
            this.callback = callback;
            this.errorHandler = errorHandler;
        }

        @Override protected void execute() {
            List<Photo> photos;
            try {
                photos = photoDAO.readPhotosByMonth(month);
                for (Photo p : photos) {
                    if (!getIsRunning())
                        return;
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    callback.accept(p);
                }
            } catch (DAOException e) {
                errorHandler.propagate(new ServiceException("Failed to load photos", e));
                return;
            }
        }
    }

    @Override public List<YearMonth> getMonthsWithPhotos() throws ServiceException {
        try {
            return photoDAO.getMonthsWithPhotos();
        } catch (DAOException ex) {
            throw new ServiceException(ex);
        }
    }



    public void close() {
        executorService.shutdown();
    }
}
