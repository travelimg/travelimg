package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 15.05.2015.
 */
public class ClusterServiceImport implements ClusterService {
    @Override public void cluster(List<Photo> photos) {
        List<DoublePoint> pointList = new ArrayList<DoublePoint>();
        for(Photo photo: photos) {
//            LocalDate.getLong(photo.getExif().getDate());
//            DoublePoint point = new DoublePoint()
        }
    }
}
