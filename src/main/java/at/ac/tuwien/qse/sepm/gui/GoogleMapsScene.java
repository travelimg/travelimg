package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class GoogleMapsScene implements MapComponentInitializedListener {

    private static final Logger logger = LogManager.getLogger(GoogleMapsScene.class);

    private GoogleMapView mapView;
    private GoogleMap map;
    private ArrayList<Photo> markers = null;
    private ArrayList<Marker> aktivMarker = new ArrayList<>();
    private HashMap<String, LatLong> displayedMarker = new HashMap<>();

    /**
     * Default Constructor
     */
    public GoogleMapsScene() {
        logger.debug("GoogleMapsScene will be created ");
        this.mapView = new GoogleMapView();
        this.mapView.addMapInializedListener(this);
    }

    public void setZoom(int zoom) {
        this.map.setZoom(zoom);
    }

    @Override
    public void mapInitialized() {
        //Set the initial properties of the map.

        logger.debug("Initializing Map ");
        MapOptions mapOptions = new MapOptions();
        mapOptions.center(new LatLong(39.7385, -104.9871))
                .overviewMapControl(true)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(true)
                .zoom(2)
                .mapMarker(true);
        map = mapView.createMap(mapOptions);
        map.addUIEventHandler(UIEventType.dblclick, (netscape.javascript.JSObject obj) -> {
            LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
            // the Coordinate on the Map
            //TODO Markerhandling
        });

        if (markers != null) {
            for (Photo photo : markers) {
                Marker m = new Marker(new MarkerOptions()
                        .position(new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude()))
                        .visible(Boolean.TRUE).animation(Animation.BOUNCE));
                m.setTitle("Marker");

                aktivMarker.add(m);
                displayedMarker.put(m.getVariableName(),
                        new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude()));
                map.addUIEventHandler(m, UIEventType.dblclick, (JSObject obj) -> {
                    //TODO //TODO Markerhandling
                });

                map.addMarker(m);
            }
        }
    }

    /**
     * set the Center of the MapView
     *
     * @param x the latitude
     * @param y the longitude
     */
    public void setCenter(double x, double y) {
        try {
            this.mapView.setCenter(x, y);
        } catch (JSException ex) {
            logger.debug("Error by initializing Map");
        }
    }

    /**
     * @return the GoogleMapView
     */
    public GoogleMapView getMapView() {
        try {
            return this.mapView;
        } catch (JSException ex) {
            logger.debug("Error by initializing Map");
        }
        return null;
    }

    /**
     * removes all Marker from Map
     */
    public void removeAktiveMarker() {
        try {
            for (Marker m : aktivMarker) {
                map.removeMarker(m);
            }
            displayedMarker.clear();
        } catch (JSException ex) {
            logger.debug("Error by initializing Map");
        }
    }

    /**
     * add one marker, which represents the foto, to the map
     *
     * @param photo the photo
     */
    public void addMarker(Photo photo) {
        try {
            if (aktivMarker.size() != 0) {
                removeAktiveMarker();
                aktivMarker = new ArrayList<>();
            }
            if (displayedMarker.size() != 0) {

                displayedMarker = new HashMap<>();
            }
            Marker m = new Marker(new MarkerOptions()
                    .position(new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude()))
                    .visible(Boolean.TRUE).animation(Animation.BOUNCE));
            m.setTitle("Marker");
            aktivMarker.add(m);
            displayedMarker.put(m.getVariableName(), new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude()));

            map.addUIEventHandler(m, UIEventType.click, (JSObject obj) -> {
                //TODO Markerhandler

            });
            mapView.setCenter(photo.getData().getLatitude(), photo.getData().getLongitude());
            mapView.setZoom(12);
            map.addMarker(m);
        } catch (JSException ex) {
            logger.debug("Error by initializing Map");
        }
    }

    /**
     * try to add some marker
     *
     * @param list list of photos to be displayed on the map
     */
    public void addMarkerList(Collection<Photo> list) {
        try {
            if (aktivMarker.size() != 0) {
                removeAktiveMarker();
                aktivMarker = new ArrayList<>();
            }
            if (displayedMarker.size() != 0) {
                displayedMarker = new HashMap<>();
            }
            map.setZoom(13);
            for (Photo photo : list) {
                Marker m = new Marker(new MarkerOptions()
                        .position(new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude()))
                        .visible(Boolean.TRUE).animation(Animation.BOUNCE));
                m.setTitle("Marker");
                aktivMarker.add(m);
                displayedMarker.put(m.getVariableName(),
                        new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude()));
                map.addUIEventHandler(m, UIEventType.click, (JSObject obj) -> {
                    //TODO Markerhandler
                });

                map.addMarker(m);
            }
        } catch (JSException ex) {
            logger.debug("Error by initializing Map");
        }
    }
}
