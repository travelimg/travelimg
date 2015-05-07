package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Exif;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;

/**
 * Created by christoph on 06.05.15.
 */
public interface GoogleMapsService extends MapComponentInitializedListener {

    public GoogleMapView getMap();
    public GoogleMapView getMarker(Exif e);
    @Override
    void mapInitialized();
}
