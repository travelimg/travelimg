package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.MapReadyListener;
import com.lynden.gmapsfx.javascript.event.MapStateEventType;
import com.lynden.gmapsfx.javascript.event.StateEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.sun.media.jfxmedia.events.MarkerEvent;
import javafx.event.*;

import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import javafx.beans.binding.Bindings;

import java.awt.*;
import java.awt.Event;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanInstantiationException;

import javafx.scene.Node;

import static java.awt.event.MouseEvent.MOUSE_CLICKED;

/**
 * Created by christoph on 08.05.15.
 */
public class GoogleMapsScene implements MapComponentInitializedListener {

    private static final Logger logger = LogManager.getLogger(GoogleMapsScene.class);

    private GoogleMapView mapView;
    private GoogleMap map;
    private ArrayList<Photo> markers =null;
    private ArrayList<Marker> aktivMarker;
    private HashMap<String,LatLong> displayedMarker;




    /**
     * Default Constructor
     *
     *
     */
    public GoogleMapsScene(){
        logger.debug("GoogleMapsScene wird erzeugt ");
        this.mapView = new GoogleMapView();
        this.mapView.addMapInializedListener(this);
        aktivMarker = new ArrayList<Marker>();
        displayedMarker = new HashMap<>();
    }


    /**
     * WorldMap Constructor
     * @param l
     */
    public GoogleMapsScene(ArrayList<Photo> l){
        logger.debug("GoogleMapsScene wird erzeugt");
        this.mapView = new GoogleMapView();
        markers = l;
        aktivMarker = new ArrayList<Marker>();
        displayedMarker = new HashMap<>();
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

        map.addUIEventHandler(UIEventType.dblclick, (JSObject obj) -> {
            LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
            // the Coordinate on the Map
            //TODO Markerhandling
        });


        if(markers!=null){
            for(Photo photo : markers) {
                if (!checkDouble(photo)) {
                    Marker m = new Marker(new MarkerOptions()
                            .position(new LatLong(photo.getLatitude(), photo.getLongitude()))
                            .visible(Boolean.TRUE).animation(Animation.BOUNCE));
                    m.setTitle("Marker");

                    aktivMarker.add(m);
                    displayedMarker.put(m.getVariableName(),
                            new LatLong(photo.getLatitude(), photo.getLongitude()));
                    map.addUIEventHandler(m, UIEventType.click, (JSObject obj) -> {
                        //TODO Markerhandling

                    });
                    map.addMarker(m);

                }

            }
        }



    }

    /**
     * set the Center of the MapView
     * @param x the latitude
     * @param y the longitude
     */
    public void setCenter(double x, double y){
        this.mapView.setCenter(x, y);
    }

    /**
     *
     * @return the GoogleMapView
     */
    public GoogleMapView getMapView() {

        return this.mapView;
    }

    /**
     * set the Size of the map-window
     * @param x width
     * @param y height
     */
    public void setMaxSize(double x, double y){
        mapView.setMaxSize(x, y);
    }

    /**
     * removes all Marker from Map
     */
    public void removeAktiveMarker(){
      for(Marker m : aktivMarker){
           map.removeMarker(m);
        }
        displayedMarker.clear();
    }

    /**
     *  add one marker, which represents the foto, to the map
     * @param photo the photo
     */
    public void addMarker(Photo photo){
        if(aktivMarker.size()!=0){
            removeAktiveMarker();
            aktivMarker = new ArrayList<>();
        }
        if(displayedMarker.size()!=0){

            displayedMarker = new HashMap<>();
        }
        Marker m =new Marker(new MarkerOptions().position(new LatLong(photo.getLatitude(),
                photo.getLongitude())).visible(Boolean.TRUE).animation(Animation.BOUNCE));
        m.setTitle("Marker");
        aktivMarker.add(m);
        displayedMarker.put(m.getVariableName(),
                new LatLong(photo.getLatitude(), photo.getLongitude()));

        map.addUIEventHandler(m, UIEventType.click, (JSObject obj) -> {
            //TODO Markerhandler

        });
        mapView.setCenter(photo.getLatitude(), photo.getLongitude());
        mapView.setZoom(12);
        map.addMarker(m);
    }

    /**
     *  try to add some marker
     * @param list list of photos to be displayed on the map
     */
    public void addMarkerList(List<Photo> list){
            if(aktivMarker.size()!=0){
                removeAktiveMarker();
                aktivMarker = new ArrayList<>();
            }
            if(displayedMarker.size()!=0){
                displayedMarker = new HashMap<>();
            }


        for(Photo photo:list){
            if(!checkDouble(photo)){
                Marker m = new Marker(new MarkerOptions().position(new LatLong(photo.getLatitude(),
                        photo.getLongitude())).visible(Boolean.TRUE).animation(Animation.BOUNCE));
                m.setTitle("Marker");
                aktivMarker.add(m);
                displayedMarker.put(m.getVariableName(),
                        new LatLong(photo.getLatitude(), photo.getLongitude()));
                map.addUIEventHandler(m, UIEventType.click, (JSObject obj) -> {
                   //TODO Markerhandler

                });

                map.addMarker(m);
            }else{
                logger.debug("Marker wird nicht gesetzt");
            }

        }


        mapView.setZoom(10);


    }

    /**
     * checks whether a marker representing a photo and is already available
     * @param p the photo
     * @return true if a marker representing the photo , false if there ist no marker representing the photo
     */
    public boolean checkDouble(Photo p){

        for(String key:displayedMarker.keySet()){
            if(p.getLatitude()==displayedMarker.get(key).getLatitude() && p.getLongitude() ==displayedMarker.get(key).getLongitude()){
                logger.debug("Marker schon vorhanden");
                return true;

            }
            double latrpos = displayedMarker.get(key).getLatitude()+0.1;
            double latrneg = displayedMarker.get(key).getLatitude()-0.1;
            double lonrneg = displayedMarker.get(key).getLongitude()-0.1;
            double lonrpos = displayedMarker.get(key).getLongitude()+0.1;
            if(latrpos-p.getLatitude()>0 || p.getLatitude()-latrneg>0 && lonrpos-p.getLongitude()<0 || p.getLongitude()-lonrneg<0){
                logger.debug("Marker im Raduis +- 0.1 schon vorhanden");
                return true;
            }
        }
        logger.debug("Marker wird gesetzt");
        return false;
    }


}
