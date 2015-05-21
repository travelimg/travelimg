package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;
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
import java.util.List;

/**
 * Created by David on 15.05.2015.
 */
public class ClusterServiceImpl implements ClusterService {

    private static final Logger logger = LogManager
            .getLogger(ClusterServiceImpl.class);

    // TODO: find good parameter intervalls for the GUI
    private static final DBSCANClusterer dbscanTime = new DBSCANClusterer(604800, 1);
    private static final DBSCANClusterer dbscanLocation = new DBSCANClusterer(0.00000009, 1);

    @Autowired private GeoService geoService;

    @Override public void cluster(List<Photo> photos) throws ServiceException {
        logger.debug("photoList-size: " + photos.size());

        List<TimeWrapper> timeList = new ArrayList<TimeWrapper>();
        List<LocationWrapper> locationList = new ArrayList<LocationWrapper>();
        List<Journey> journeyList = new ArrayList<Journey>();

        for (Photo photo : photos) {
            timeList.add(new TimeWrapper(photo));
        }

        logger.debug("pointList-size: " + timeList.size());

        // cluster for journeys
        List<Cluster<TimeWrapper>> clusterResultsTime = dbscanTime.cluster(timeList);

        for (int i = 0; i < clusterResultsTime.size(); i++) {
            double latitude;
            double longitude;
            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            int count;
            latitude = longitude = count = 0;
            locationList.clear();

            for (TimeWrapper timeWrapper : clusterResultsTime.get(i).getPoints()) {
                // get the start and end date of a journey
                if (startDate == null)
                    startDate = timeWrapper.getPhoto().getDatetime();
                else if (timeWrapper.getPhoto().getDatetime().isBefore(startDate))
                    startDate = timeWrapper.getPhoto().getDatetime();

                if (endDate == null)
                    endDate = timeWrapper.getPhoto().getDatetime();
                else if (timeWrapper.getPhoto().getDatetime().isAfter(endDate))
                    endDate = timeWrapper.getPhoto().getDatetime();

                latitude += timeWrapper.getPhoto().getLatitude();
                longitude += timeWrapper.getPhoto().getLongitude();
                count++;
                locationList.add(new LocationWrapper(timeWrapper.getPhoto()));
            }
            logger.debug(
                    "latCentroid: " + latitude / count + " longCentroid: " + longitude / count);
            journeyList.add(new Journey(startDate, endDate,
                    geoService.getPlaceByGeoData(latitude / count, longitude / count)
                            .getCountry()));
            logger.debug(
                    "Reise " + (i+1)  + " " + journeyList.get(i).getName() + " Start: " + journeyList
                            .get(i).getStartDate() + " Ende: " + journeyList.get(i).getEndDate());

            // cluster for places
            List<Cluster<LocationWrapper>> clusterResultsLocation = dbscanTime
                    .cluster(locationList);

            for (int j = 0; j < clusterResultsLocation.size(); j++) {
                latitude = longitude = count = 0;
                for (LocationWrapper locationWrapper : clusterResultsLocation.get(j).getPoints()) {
                    latitude += locationWrapper.getPhoto().getLatitude();
                    longitude += locationWrapper.getPhoto().getLongitude();
                    count++;
                }
                Place place = geoService.getPlaceByGeoData(latitude / count, longitude / count);
                logger.debug("Land: " + place.getCountry() + " Ort: " + place.getCity());
            }

        }
    }

    //    private Point getCenter(Cluster<Clusterable> input) {
    //    }
}