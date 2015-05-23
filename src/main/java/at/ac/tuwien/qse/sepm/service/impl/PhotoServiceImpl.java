package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.dao.TagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PhotoServiceImpl implements PhotoService {

    private static final Logger LOGGER = LogManager.getLogger();
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    @Autowired private PhotoDAO photoDAO;
    @Autowired private TagDAO tagDAO;
    @Autowired private PhotoTagDAO photoTagDAO;

    @Override
    public List<YearMonth> getMonthsWithPhotos() throws ServiceException {
        LOGGER.debug("Retrieving list of months...");
        try {
            return photoDAO.getMonthsWithPhotos();
        } catch (DAOException ex) {
            throw new ServiceException(ex);
        }
    }

    @Override
    public void deletePhotos(List<Photo> photos) throws ServiceException {
        if (photos == null) {
            throw new ServiceException("List<Photo> photos is null");
        }
        for (Photo p : photos) {
            LOGGER.debug("Deleting photo {}",p);
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
        //TODO
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
    public void requestFullscreenMode(List<Photo> photos) throws ServiceException {
        //TODO
    }

    @Override
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

    @Override
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

    @Override
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

    @Override public void savePhotoRating(Photo photo) throws ServiceException {
        if (photo == null) throw new IllegalArgumentException();
        LOGGER.debug("Entering savePhotoRating with {}", photo);
        try {
            photoDAO.update(photo);
            LOGGER.info("Successfully saved rating for {}", photo);
        } catch (DAOException ex) {
            LOGGER.error("Saving rating for {} failed to to DAOException", photo);
            throw new ServiceException("Could not store rating of photo.", ex);
        } catch (ValidationException ex) {
            LOGGER.error("Saving rating for {} failed to to ValidationException", photo);
            throw new ServiceException("Could not store rating of photo.", ex);
        }
        LOGGER.debug("Leaving savePhotoRating with {}", photo);
    }
}
