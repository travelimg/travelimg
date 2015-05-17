package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 15.05.2015.
 */
public class ClusterServiceImpl implements ClusterService {

    private static final org.apache.logging.log4j.Logger logger = LogManager
            .getLogger(ClusterServiceImpl.class);
    private static final DBSCANClusterer dbscan = new DBSCANClusterer(.05, 1);

    @Override public void cluster(List<Photo> photos) {
        logger.debug("photoList-size: " + photos.size());
        List<DoublePoint> pointList = new ArrayList<DoublePoint>();
        ZoneId zoneId = ZoneId.systemDefault();
        for(Photo photo: photos) {
            double[] date ={photo.getDate().atStartOfDay(zoneId).toEpochSecond()};
            pointList.add(new DoublePoint(date));
        }
        logger.debug("pointList-size: " + pointList.size());

        List<Cluster<DoublePoint>> cluster = dbscan.cluster(pointList);

        for(Cluster<DoublePoint> c : cluster){
            logger.debug(c.getPoints().get(0));
        }

    }
}