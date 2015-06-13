package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.JourneyDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PhotoServiceImpl implements PhotoService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private ExecutorService executorService;
    @Autowired
    private PhotoDAO photoDAO;
    @Autowired
    private JourneyDAO journeyDAO;

    @Override
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
                p.setPlace(photo.getPlace());
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

    @Override
    @Deprecated
    public void addJourneyToPhotos(List<Photo> photos, Journey journey)
            throws ServiceException {
        LOGGER.debug("Entering addJourneyToPhotos with {}, {}", photos, journey);
        if (photos == null) {
            throw new ServiceException("List<Photo> photos is null");
        }
        try {
            journeyDAO.create(journey);
        } catch (DAOException ex) {
            LOGGER.error("Journey-creation with {}, {} failed.", journey);
            throw new ServiceException("Creation of Journey failed.", ex);
        } catch (ValidationException e) {
            throw new ServiceException("Failed to validate entity", e);
        }

        for (Photo photo : photos) {
            photo.getPlace().setJourney(journey);
//            exifService.exportMetaToExif(photo);
            LOGGER.debug("Leaving addJourneyToPhotos");
        }
    }

    @Override
    @Deprecated
    public void addPlaceToPhotos(List<Photo> photos, Place place)
            throws ServiceException {
        LOGGER.debug("Entering addPlaceToPhotos with {}, {}", photos, place);
        if (photos == null) {
            throw new ServiceException("List<Photo> photos is null");
        }

        for (Photo photo : photos) {
//            exifService.exportMetaToExif(photo);
        }
        Photo p = new Photo();
        p.setPlace(place);
        editPhotos(photos, p);
        LOGGER.debug("Leaving addPlaceToPhotos");
    }
}
