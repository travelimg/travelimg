package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.service.*;
import at.ac.tuwien.qse.sepm.service.wrapper.TimeWrapper;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 15.05.2015.
 */
public class ClusterServiceImpl implements ClusterService {

    private static final org.apache.logging.log4j.Logger logger = LogManager
            .getLogger(ClusterServiceImpl.class);
    private static final DBSCANClusterer dbscan = new DBSCANClusterer(604800, 0);

    @Autowired private GeoService geoService;

    @Override public List<Place> cluster(List<Photo> photos) throws ServiceException {
        logger.debug("photoList-size: " + photos.size());

        List<TimeWrapper> pointList = new ArrayList<TimeWrapper>();
        List<Place> reiseList = new ArrayList<Place>();

        for (Photo photo : photos) {
            pointList.add(new TimeWrapper(photo));
        }

        logger.debug("pointList-size: " + pointList.size());

        List<Cluster<TimeWrapper>> clusterResults = dbscan.cluster(pointList);

        for (int i = 0; i < clusterResults.size(); i++) {
            double latitude, longitude;
            int count;
            latitude = longitude = count = 0;

            for (TimeWrapper timeWrapper : clusterResults.get(i).getPoints()) {
                latitude += timeWrapper.getPhoto().getLatitude();
                longitude += timeWrapper.getPhoto().getLongitude();
                count++;
            }
            logger.debug("latCentroid: " + latitude/count + " longCentroid: " + longitude/count);
            reiseList.add(geoService.getPlaceByGeoData(latitude / count, longitude / count));
            logger.debug("Reise " + 1 + " " + reiseList.get(i).getCountry());
        }

        return reiseList;
    }
}