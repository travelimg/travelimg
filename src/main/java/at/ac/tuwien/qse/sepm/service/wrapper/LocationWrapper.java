package at.ac.tuwien.qse.sepm.service.wrapper;

import at.ac.tuwien.qse.sepm.entities.Photo;
import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * Created by David on 18.05.2015.
 */
public class LocationWrapper implements Clusterable {
    private double[] points;
    private Photo photo;

    public LocationWrapper(Photo photo) {
        this.photo = photo;
        this.points = new double[]{photo.getLatitude(), photo.getLongitude()};
    }

    @Override
    public double[] getPoint() {
        return points;
    }

    public Photo getPhoto() {
        return photo;
    }
}
