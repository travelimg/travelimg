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
import java.util.List;
import java.util.Optional;

public class ClusterServiceImpl implements ClusterService {

    private static final Logger logger = LogManager.getLogger(ClusterServiceImpl.class);

    @Autowired
    private GeoService geoService;
    @Autowired
    private PhotoDAO photoDAO;
    @Autowired
    private JourneyDAO journeyDAO;
    @Autowired
    private PlaceDAO placeDAO;
    @Autowired
    private PhotoService photoService;

    @Override
    public List<Journey> getAllJourneys() throws ServiceException {
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
        List<Place> places= new ArrayList<>();

        try {
            for(Photo p: photoDAO.readPhotosByJourney(journey)){
               if(places.size()==0){
                   places.add(p.getData().getPlace());
               }else{
                   if(!places.contains(p.getData().getPlace())){
                       places.add(p.getData().getPlace());
                   }
               }
            }
        } catch (DAOException e) {
            throw new ServiceException("Failed to read Photos by journey");
        }
        /*try {
            return placeDAO.readByJourney(journey);
        } catch (DAOException e) {
            throw new ServiceException("Failed to get places", e);
        } catch (ValidationException e) {
            throw new ServiceException("Failed to validate journey id", e);
        }*/
        return places;
    }

    @Override
    public Place addPlace(Place place) throws ServiceException {
        try {
            return placeDAO.create(place);
        } catch (DAOException ex) {
            logger.error("Place-creation for {} failed.", place);
            throw new ServiceException("Creation of Place failed.", ex);
        } catch (ValidationException e) {
            throw new ServiceException("Failed to validate entity", e);
        }
    }

    @Override
    public Journey addJourney(Journey journey) throws ServiceException {
        try {
            return journeyDAO.create(journey);
        } catch (DAOException ex) {
            logger.error("Journey-creation for {} failed.", journey);
            throw new ServiceException("Creation of journey failed.", ex);
        } catch (ValidationException e) {
            throw new ServiceException("Failed to validate entity", e);
        }
    }

    @Override
    public List<Place> clusterJourney(Journey journey) throws ServiceException {
        logger.debug("clustering journey {}", journey);

        List<Photo> photos;
        List<Place> places = new ArrayList<>();

        addJourney(journey);

        try {
            photos = photoDAO.readPhotosBetween(journey.getStartDate(), journey.getEndDate());
        } catch (DAOException e) {
            logger.error("Failed to read photos of journey", e);
            throw new ServiceException("Failed to read photos of journey", e);
        }

        // attach a place to each photo
        for (Photo photo : photos) {
            final double latitude = photo.getData().getLatitude();
            final double longitude = photo.getData().getLongitude();

            double epsilon = 1.0;
            Optional<Place> place = places.stream()
                    .filter(p -> Math.abs(p.getLatitude() - latitude) < epsilon)   // find an existing place in lateral proximity
                    .filter(p -> Math.abs(p.getLongitude() - longitude) < epsilon) // find an existing place in longitudinal proximity
                    .findFirst();

            if (!place.isPresent()) {
                // if no we don't already know a place in close proximity look it up
                place = Optional.of(lookupPlace(photo));
                places.add(place.get());
            }

            photo.getData().setPlace(place.get());
            photo.getData().setJourney(journey);
            photoService.editPhoto(photo);
        }
        return places;
    }

    private Place lookupPlace(Photo photo) throws ServiceException {
        Place place = geoService.getPlaceByGeoData(photo.getData().getLatitude(), photo.getData().getLongitude());

        logger.debug("New unknown place cluster: {}", place);

        return addPlace(place);
    }
}