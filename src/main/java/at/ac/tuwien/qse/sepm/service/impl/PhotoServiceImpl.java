package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class PhotoServiceImpl implements PhotoService {

    private static final Logger LOGGER = LogManager.getLogger();

    private PhotoDAO photoDAO;
    private PhotoTagDAO photoTagDAO;

    public PhotoServiceImpl() {

    }

    @Autowired
    public void setPhotoDAO(PhotoDAO photoDAO) {
        this.photoDAO = photoDAO;
    }
    @Autowired
    public void setPhotoTagDAO(PhotoTagDAO photoTagDAO) {
        this.photoTagDAO = photoTagDAO;
    }

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

    public void deletePhotos(List<Photo> photos) throws ServiceException {

    }

    /**
     * Add Tag <tt>tag</tt> to every photo in list <tt>photos</tt>. If a photo already has this tag,
     * then it will keep it.
     *
     * @param photos must not be null; all elements must not be null; no element.id must be null
     * @param tag    must not be null; tag.id must not be null
     * @throws ServiceException         if an unhandled Exception in this or an underlying
     *                                  layer occurs
     * @throws IllegalArgumentException if any precondition is violated
     */
    public void addTagToPhotos(List<Photo> photos, Tag tag) throws ServiceException {
        LOGGER.debug("Entering addTagToPhotos with {}, {}", photos, tag);
        if (photos == null) {
            throw new IllegalArgumentException("List<Photo> photos is null");
        }
        if (tag == null || tag.getId() == null) {
            throw new IllegalArgumentException("tag is null or does not have an id");
        }
        for (Photo photo : photos) {
            if (photo == null || photo.getId() == null) {
                throw new IllegalArgumentException("List element is null or does not have an id");
            }
        }

        for (Photo photo : photos) {
            try {
                photoTagDAO.createPhotoTag(photo, tag);
            } catch (DAOException ex) {
                LOGGER.error("Photo-Tag-creation with {}, {} failed.", photo, tag);
                throw new ServiceException("DAOException was thrown: " + ex.getMessage());
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
     * @throws ServiceException         if an unhandled Exception in this or an underlying
     *                                  layer occurs
     * @throws IllegalArgumentException if any precondition is violated
     */
    public void removeTagFromPhotos(List<Photo> photos, Tag tag) throws ServiceException {
        LOGGER.debug("Entering removeTagFromPhotos with {}, {}", photos, tag);
        if (photos == null) {
            throw new IllegalArgumentException("List<Photo> photos is null");
        }
        if (tag == null || tag.getId() == null) {
            throw new IllegalArgumentException("tag is null or does not have an id");
        }
        for (Photo photo : photos) {
            if (photo == null || photo.getId() == null) {
                throw new IllegalArgumentException("List element is null or does not have an id");
            }
        }

        for (Photo photo : photos) {
            try {
                photoTagDAO.removeTagFromPhoto(photo, tag);
            } catch (DAOException ex) {
                LOGGER.error("Removal of Photo-Tag with {}, {} failed.", photo, tag);
                throw new ServiceException("DAOException was thrown: " + ex.getMessage());
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
     * @throws IllegalArgumentException if any precondition is violated
     */
    public List<Tag> getTagsForPhoto(Photo photo) throws ServiceException {
        LOGGER.debug("Entering getTagsForPhoto with {}", photo);
        if (photo == null || photo.getId() == null) {
            throw new IllegalArgumentException("Photo is null or does not have an id");
        }

        List<Tag> tagList;
        try {
            tagList = photoTagDAO.readTagsByPhoto(photo);
            LOGGER.info("Successfully retrieved tags for {}", photo);
        } catch (DAOException ex) {
            LOGGER.error("Retrieving tags for {} failed due to DAOException", photo);
            throw new ServiceException("Could not retrieve tags for photo: " + ex.getMessage());
        }
        LOGGER.debug("Leaving getTagsForPhoto with {}", photo);
        return tagList;
    }
}
