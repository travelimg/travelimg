package at.ac.tuwien.qse.sepm.service.wrapper;

import at.ac.tuwien.qse.sepm.entities.Photo;
import org.apache.commons.math3.ml.clustering.Clusterable;

import java.time.ZoneId;

/**
 * Created by David on 18.05.2015.
 */
public class TimeWrapper implements Clusterable {
    private double[] points;
    private Photo photo;

    public TimeWrapper(Photo photo) {
        this.photo = photo;
        this.points = new double[]{photo.getDatetime().atZone(ZoneId.systemDefault()).toEpochSecond()};
    }

    @Override
    public double[] getPoint() {
        return points;
    }

    public Photo getPhoto() {
        return photo;
    }
}
