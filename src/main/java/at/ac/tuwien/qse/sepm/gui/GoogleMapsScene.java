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
    //new LatLong(40.7033127, -73.979681); // the default Location
    private Marker actualMarker;
    private Marker m;
    private StateEventHandler h;

    /**
     * Default Constructor
     *
     *
     */
    public GoogleMapsScene(){

        this.mapView = new GoogleMapView();
        this.mapView.addMapInializedListener(this);

    }

    /**
     * WorldMap Constructor
     * @param l
     */
    public GoogleMapsScene(ArrayList<Photo> l){
        this.mapView = new GoogleMapView();
        markers = l;
        aktivMarker = new ArrayList<Marker>();
        displayedMarker = new HashMap<>();
        this.mapView.addMapInializedListener(this);
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
            //System.out.println("LatLong: lat: " + ll.getLatitude() + " lng: " + ll.getLongitude());
            System.out.println(ll.toString());
        });


        if(markers!=null){
            for(Photo photo : markers){
                Marker m = new Marker(new MarkerOptions().position(new LatLong(photo.getLatitude(),
                        photo.getLongitude())).visible(Boolean.TRUE).animation(Animation.BOUNCE));
                m.setTitle("Marker");
                m.setAnimation(Animation.BOUNCE);
               aktivMarker.add(m);
                displayedMarker.put(m.getVariableName(),new LatLong(photo.getLatitude(),
                        photo.getLongitude()));
                map.addMarker(m);
                map.addUIEventHandler(m,UIEventType.click,(JSObject obj) ->{System.out.println(m.getVariableName());
                    System.out.println("AKTIVE MARKER");

                    System.out.println(displayedMarker.get(m.getVariableName()));

                });

            }


        }



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
        displayedMarker = new HashMap<>();
    }
    public void addMarker(Photo photo){
        if(actualMarker!=null)
            map.removeMarker(actualMarker);
        actualMarker = new Marker(new MarkerOptions().position(new LatLong(photo.getLatitude(),
                photo.getLongitude())).visible(Boolean.TRUE).animation(Animation.BOUNCE));
        mapView.setCenter(photo.getLatitude(), photo.getLongitude());
        mapView.setZoom(1);//workaround to remove the old marker from the view
        mapView.setZoom(12);
        //actualMarker.setTitle(photo.getPhotographer().getName());
        map.addMarker(actualMarker);

    }
    public void addMarkerList(ArrayList<Photo> list){
            if(aktivMarker.size()!=0){
                aktivMarker = new ArrayList<>();
            }
            if(displayedMarker.size()!=0){
                displayedMarker = new HashMap<>();
            }
        ArrayList<LatLong> centerPoint = new ArrayList<>();
        for(Photo photo:list){
            Marker m = new Marker(new MarkerOptions().position(new LatLong(photo.getLatitude(),
                    photo.getLongitude())).visible(Boolean.TRUE).animation(Animation.BOUNCE));
            m.setTitle("Marker");
            aktivMarker.add(m);
            displayedMarker.put(m.getVariableName(),
                    new LatLong(photo.getLatitude(), photo.getLongitude()));
            centerPoint.add(new LatLong(photo.getLatitude(),
                    photo.getLongitude()));
            map.addMarker(m);
        }
        double [] center = calculateCenter(centerPoint);
        mapView.setCenter(center[0],center[1]);
        mapView.setZoom(10);


    }
    public double[] calculateCenter(ArrayList<LatLong> centerPoint){
        double x=0;
        double y=0;
        double area=0;
        double[] erg = new double[2];
        for(int i =0; i<centerPoint.size()-1;i++){
            LatLong point = centerPoint.get(i);
            LatLong pointN = centerPoint.get(i+1);
            double help = point.getLatitude() * pointN.getLongitude() + pointN.getLatitude()*point.getLongitude();

            x += (point.getLatitude() + pointN.getLatitude())*help;
            y += (point.getLongitude() + pointN.getLongitude())*help;

            area += help;
        }
        area /= 2.0;
        x *= 1/6.0 *area;
        y *= 1/6.0 *area;

        erg[0]=x;
        erg[1]=y;
        return erg;

    }


}
