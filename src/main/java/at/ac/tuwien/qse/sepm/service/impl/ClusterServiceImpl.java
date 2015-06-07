package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.JourneyDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.PlaceDAO;
import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import at.ac.tuwien.qse.sepm.service.GeoService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClusterServiceImpl implements ClusterService {

    private static final Logger logger = LogManager.getLogger(ClusterServiceImpl.class);

    @Autowired private GeoService geoService;
    @Autowired private PhotoDAO photoDAO;
    @Autowired private JourneyDAO journeyDAO;
    @Autowired private PlaceDAO placeDAO;
    @Autowired private PhotoService photoService;

    @Override public List<Journey> getAllJourneys() throws ServiceException {
        try {
            return journeyDAO.readAll();
        } catch (DAOException ex) {
            logger.error("Failed to get all journeys", ex);
            throw new ServiceException("Failed to get all journeys", ex);
        }
    }

    @Override
    public List<Place> getAllPlaces() throws ServiceException {
        try {
            return placeDAO.readAll();
        } catch (DAOException ex) {
            logger.error("Failed to get all places", ex);
            throw new ServiceException("Failed to get all places", ex);
        }
    }

    @Override
    public List<Place> getPlacesByJourney(Journey journey) throws ServiceException {
        try {
            return placeDAO.readByJourney(journey);
        } catch (DAOException e) {
            throw new ServiceException("Failed to get places", e);
        } catch (ValidationException e) {
            throw new ServiceException("Failed to validate journey id", e);
        }
    }

    @Override public void addPlace(Place place) throws ServiceException {
        try {
            placeDAO.create(place);
        } catch (DAOException ex) {
            logger.error("Place-creation with {}, {} failed.", place);
            throw new ServiceException("Creation of Place failed.", ex);
        } catch (ValidationException e) {
            throw new ServiceException("Failed to validate entity", e);
        }
    }

    @Override public List<Place> clusterJourney(Journey journey) throws ServiceException {
        logger.debug("clusteringJourney" + journey);
        List<Photo> photos;
        List<Place> places = new ArrayList<Place>();
        Place place = new Place(1, "Unknown place", "Unknown place");
        double latitude = 1000;
        double longitude = 1000;

        try {
            photos = photoDAO.readPhotosByJourney(journey);
        } catch (DAOException e) {
            logger.error("Failed to read photos of journey", e);
            throw new ServiceException("Failed to read photos of journey", e);
        }

            photoService.addJourneyToPhotos(photos, journey);

            for (Photo element : photos) {
                if (Math.abs(element.getLatitude() - latitude) > 1
                    && Math.abs(element.getLongitude() - longitude) > 1) {
                place = geoService.getPlaceByGeoData(element.getLatitude(), element.getLongitude());
                place.setLatitude(element.getLatitude());
                place.setLongitude(element.getLongitude());
                place.setJourney(journey);
                logger.debug("New place-cluster: " + place.getId() + " " + place.getCity());
                addPlace(place);
            }
            latitude = element.getLatitude();
            longitude = element.getLongitude();
            photoService.addPlaceToPhotos(Arrays.asList(element), place);
            places.add(place);
        }
        return places;
    }
}