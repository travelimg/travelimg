package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.GoogleMapsScene;
import at.ac.tuwien.qse.sepm.gui.util.GeoUtils;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TravelRouteMap {

    private GoogleMapsScene googleMapsScene;

    private final List<Marker> markers = new ArrayList<>();
    private final List<Polyline> lines = new ArrayList<>();

    public TravelRouteMap(GoogleMapsScene googleMapsScene) {
        this.googleMapsScene = googleMapsScene;
    }

    private GoogleMapView getMapView() {
        return googleMapsScene.getMapView();
    }

    private GoogleMap getGoogleMap() {
        return getMapView().getMap();
    }

    public void clear() {
        markers.forEach(marker -> getGoogleMap().removeMarker(marker));
        lines.forEach(line -> getGoogleMap().removeMapShape(line));

        markers.clear();
        lines.clear();
    }


    public void drawJourney(List<Place> places) {
        clear();

        if (places.isEmpty()) {
            return;
        }

        List<LatLong> path = Arrays.asList(GeoUtils.toLatLong(places));

        if (places.size() == 1) {
            Marker marker = createMarkerAt(path.get(0));
            getGoogleMap().addMarker(marker);
            markers.add(marker);

            // fit marker to screen
        } else {
            MVCArray mvcArray = new MVCArray();

            path.forEach((pos) -> {
                mvcArray.push(pos);

                Marker marker = createMarkerAt(pos);
                getGoogleMap().addMarker(marker);
                markers.add(marker);
            });

            Polyline polyline = createPolylineForPath(mvcArray);

            getGoogleMap().addMapShape(polyline);
            lines.add(polyline);

            // fit markers to screen
        }
    }

    private Marker createMarkerAt(LatLong pos) {
        MarkerOptions options = new MarkerOptions()
                .position(pos)
                .icon("https://maps.gstatic.com/intl/en_us/mapfiles/markers2/measle_blue.png");

        return new Marker(options);
    }

    private Polyline createPolylineForPath(MVCArray mvcArray) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.path(mvcArray)
                .clickable(false)
                .draggable(false)
                .editable(false)
                .strokeColor("#ff4500")
                .strokeWeight(2)
                .visible(true);

        return new Polyline(polylineOptions);
    }
}
