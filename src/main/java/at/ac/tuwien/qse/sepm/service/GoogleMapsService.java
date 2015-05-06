package at.ac.tuwien.qse.sepm.service;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;

/**
 * Created by christoph on 06.05.15.
 */
public interface GoogleMapsService extends MapComponentInitializedListener {

    public GoogleMapView getMap();

    @Override
    void mapInitialized();
}
