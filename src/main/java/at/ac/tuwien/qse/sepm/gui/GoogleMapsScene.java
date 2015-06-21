package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.controller.WorldmapView;
import at.ac.tuwien.qse.sepm.service.GeoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
                m.setTitle(photo.getData().getPlace().getCountry());

                aktivMarker.add(m);
                displayedMarker.put(m.getVariableName(),
                        new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude()));
                map.addUIEventHandler(m, UIEventType.dblclick, (JSObject obj) -> {
                    System.out.println(displayedMarker.get(m.getVariableName()).toString());


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
        if(aktivMarker.size()==0) return;
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
            m.setTitle(photo.getPath());
            aktivMarker.add(m);
            displayedMarker.put(m.getVariableName(), new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude()));

            map.addUIEventHandler(m, UIEventType.click, (JSObject obj) -> {
                System.out.println(displayedMarker.get(m.getVariableName()).toString());



            });
            mapView.setCenter(photo.getData().getLatitude(), photo.getData().getLongitude());
            mapView.setZoom(12);
            map.addMarker(m);
        } catch (JSException ex) {
            logger.debug("Error by initializing Map");
       // } catch (ServiceException e) {
            //TODO Exceptionhandling
        }
    }

    /**
     * try to add some marker
     *
     * @param list list of photos to be displayed on the map
     */
    public void addMarkerList(List<Photo> list) {
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


                m.setTitle(photo.getPath());
                aktivMarker.add(m);
                displayedMarker.put(m.getVariableName(),
                        new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude()));
                map.addUIEventHandler(m, UIEventType.click, (JSObject obj) -> {
                    System.out.println(displayedMarker.get(m.getVariableName()).toString());


                });

                map.addMarker(m);
            }
        } catch (JSException ex) {
            logger.debug("Error by initializing Map");
        //} catch (ServiceException e) {
            //TODO ExceptionHandling
            System.out.println("FEHLER");
        }
    }
}
