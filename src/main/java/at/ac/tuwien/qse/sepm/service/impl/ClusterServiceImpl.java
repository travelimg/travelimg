package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Reise;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import at.ac.tuwien.qse.sepm.service.wrapper.TimeWrapper;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 15.05.2015.
 */
public class ClusterServiceImpl implements ClusterService {

    private static final org.apache.logging.log4j.Logger logger = LogManager
            .getLogger(ClusterServiceImpl.class);
    private static final DBSCANClusterer dbscan = new DBSCANClusterer(604800, 0);

    @Override public List<Reise> cluster(List<Photo> photos) {
        logger.debug("photoList-size: " + photos.size());

        List<TimeWrapper> pointList = new ArrayList<TimeWrapper>();
        List<Reise> reiseList = new ArrayList<Reise>();

        for(Photo photo: photos) {
            pointList.add(new TimeWrapper(photo));
        }

        logger.debug("pointList-size: " + pointList.size());

        List<Cluster<TimeWrapper>> clusterResults = dbscan.cluster(pointList);

        for (int i=0; i<clusterResults.size(); i++) {
            double latitude, longitude;
            int count;
            latitude = longitude = count = 0;
//            System.out.println("Cluster " + i);
            for (TimeWrapper timeWrapper : clusterResults.get(i).getPoints()) {
                latitude += timeWrapper.getPhoto().getLatitude();
                longitude += timeWrapper.getPhoto().getLongitude();
            }
            reiseList.add(new Reise(latitude/count, longitude/count, ""));
//                System.out.println(timeWrapper.getPhoto().getDate());
//            System.out.println();
        }


        return reiseList;
    }
}