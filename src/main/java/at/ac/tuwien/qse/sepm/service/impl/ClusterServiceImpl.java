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

    // TODO: find good parameter intervalls for the GUI
    private static final DBSCANClusterer dbscanTime = new DBSCANClusterer(604800, 1);
    private static final DBSCANClusterer dbscanLocation = new DBSCANClusterer(0.00000009, 1);

    @Autowired private GeoService geoService;
    @Autowired private PhotoDAO photoDAO;
    @Autowired private JourneyDAO journeyDAO;
    @Autowired private PlaceDAO placeDAO;
    @Autowired private PhotoService photoService;

//    @Deprecated public void cluster(List<Photo> photos) throws ServiceException {
//        logger.debug("photoList-size: " + photos.size());
//
//        List<TimeWrapper> timeList = new ArrayList<TimeWrapper>();
//        List<LocationWrapper> locationList = new ArrayList<LocationWrapper>();
//        List<Journey> journeyList = new ArrayList<Journey>();
//
//        for (Photo photo : photos) {
//            timeList.add(new TimeWrapper(photo));
//        }
//
//        logger.debug("pointList-size: " + timeList.size());
//
//        // cluster for journeys
//        List<Cluster<TimeWrapper>> clusterResultsTime = dbscanTime.cluster(timeList);
//
//        for (int i = 0; i < clusterResultsTime.size(); i++) {
//            double latitude;
//            double longitude;
//            LocalDateTime startDate = null;
//            LocalDateTime endDate = null;
//            int count;
//            latitude = longitude = count = 0;
//            locationList.clear();
//
//            for (TimeWrapper timeWrapper : clusterResultsTime.get(i).getPoints()) {
//                // get the start and end date of a journey
//                if (startDate == null)
//                    startDate = timeWrapper.getPhoto().getDatetime();
//                else if (timeWrapper.getPhoto().getDatetime().isBefore(startDate))
//                    startDate = timeWrapper.getPhoto().getDatetime();
//
//                if (endDate == null)
//                    endDate = timeWrapper.getPhoto().getDatetime();
//                else if (timeWrapper.getPhoto().getDatetime().isAfter(endDate))
//                    endDate = timeWrapper.getPhoto().getDatetime();
//
//                latitude += timeWrapper.getPhoto().getLatitude();
//                longitude += timeWrapper.getPhoto().getLongitude();
//                count++;
//                locationList.add(new LocationWrapper(timeWrapper.getPhoto()));
//            }
//            logger.debug(
//                    "latCentroid: " + latitude / count + " longCentroid: " + longitude / count);
//            journeyList.add(new Journey(-1,
//                    geoService.getPlaceByGeoData(latitude / count, longitude / count).getCountry(),
//                    startDate, endDate));
//            logger.debug("Reise " + (i + 1) + " " + journeyList.get(i).getName() + " Start: "
//                    + journeyList.get(i).getStartDate() + " Ende: " + journeyList.get(i)
//                    .getEndDate());
//
//            // cluster for places
//            List<Cluster<LocationWrapper>> clusterResultsLocation = dbscanTime
//                    .cluster(locationList);
//
//            for (int j = 0; j < clusterResultsLocation.size(); j++) {
//                latitude = longitude = count = 0;
//                for (LocationWrapper locationWrapper : clusterResultsLocation.get(j).getPoints()) {
//                    latitude += locationWrapper.getPhoto().getLatitude();
//                    longitude += locationWrapper.getPhoto().getLongitude();
//                    count++;
//                }
//                Place place = geoService.getPlaceByGeoData(latitude / count, longitude / count);
//                logger.debug("Land: " + place.getCountry() + " Ort: " + place.getCity());
//            }
//
//        }
//    }

//    @Deprecated
//    @Override public Journey addJourney(Journey journey) throws ServiceException {
//        try {
//            return journeyDAO.create(journey);
//        } catch (DAOException | ValidationException e) {
//            e.printStackTrace();
//            throw new ServiceException("Failed to create new Journey", e);
//        }
//    }
//
//    @Deprecated
//    @Override public Place addPlace(Place place) throws ServiceException {
//        try {
//            return placeDAO.create(place);
//        } catch (DAOException | ValidationException e) {
//            e.printStackTrace();
//            throw new ServiceException("Failed to create new Place", e);
//        }
//    }

    /**
     * Read and return all currently saved journeys.
     *
     * @return list of all currently saved journeys
     * @throws ServiceException propagates DAOExceptions
     */
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

//        Tag journeyTag = new Tag(null, "journey." + journey.getName());
//        tagService.create(journeyTag);
//        photoService.addTagToPhotos(photos, journeyTag);

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

//            Tag placeTag = new Tag(null, "place." + place.getCity() + "." + place.getCountry());
//            tagService.create(placeTag);

        }
        return places;
    }
}