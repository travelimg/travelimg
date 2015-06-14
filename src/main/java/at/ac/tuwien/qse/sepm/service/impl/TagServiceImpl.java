package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoTagDAO;
import at.ac.tuwien.qse.sepm.dao.TagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.TagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TagServiceImpl implements TagService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private TagDAO tagDAO;
    @Autowired
    private PhotoTagDAO photoTagDAO;


    @Override
    public Tag create(Tag tag) throws ServiceException {
        try {
            return tagDAO.create(tag);
        } catch (DAOException | ValidationException ex) {
            LOGGER.error("Failed to create tag", ex);
            throw new ServiceException("Failed to create tag", ex);
        }
    }

    @Override
    public void delete(Tag tag) throws ServiceException {
        try {
            tagDAO.delete(tag);
        } catch (DAOException ex) {
            LOGGER.error("Failed to delete tag", ex);
            throw new ServiceException("Failed to delete tag", ex);
        }
    }

    @Override
    public Tag readName(Tag tag) throws ServiceException {
        try {
            return tagDAO.readName(tag);
        } catch (DAOException e) {
            LOGGER.error("Failed to read tag by name", e);
            throw new ServiceException("Failed to read tag by name", e);
        }
    }

    @Override
    public List<Tag> getAllTags() throws ServiceException {
        LOGGER.debug("Retrieving all tags...");
        try {
            return tagDAO.readAll();
        } catch (DAOException e) {
            LOGGER.error("Failed to retrieve all tags", e);
            throw new ServiceException("Failed to retrieve all tags", e);
        }
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
                photo.getData().getTags().add(tag);
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
                photo.getData().getTags().remove(tag);
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

    @Override
    public List<Tag> getMostFrequentTags(List<Photo> photos) throws ServiceException {
        LOGGER.debug("Entering getMostFrequentTags with {}", photos);

        HashMap<Tag, Integer> counter = new HashMap<>();

        // count the frequency of each tag
        for (Photo photo : photos) {
            for (Tag tag : photo.getData().getTags()) {
                if (counter.containsKey(tag)) {
                    counter.put(tag, counter.get(tag) + 1);
                } else {
                    counter.put(tag, 1);
                }
            }
        }

        if (counter.size() == 0) {
            throw new ServiceException("No Tags found");
        }

        // return the most frequent tags
        return counter.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
