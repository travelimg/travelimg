package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.JourneyDAO;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.PlaceDAO;
import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.wrapper.LocationWrapper;
import at.ac.tuwien.qse.sepm.service.wrapper.TimeWrapper;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            ex.printStackTrace();
            throw new ServiceException("Failed to get all journeys", ex);
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
                latitude = element.getLatitude();
                longitude = element.getLongitude();
                logger.debug("New place-cluster: " + place.getId() + " " + place.getCity());
            }

            latitude = element.getLatitude();
            longitude = element.getLongitude();

            photoService.addPlaceToPhotos(photos, place);
            places.add(place);
        }
        return places;
    }
}