package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.service.impl.ExifServiceImpl;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import javafx.application.Application;
import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by christoph on 08.05.15.
 */
public class GoogleMapsScene implements MapComponentInitializedListener {

    private static final Logger logger = LogManager.getLogger(ExifServiceImpl.class);

    private GoogleMapView mapView;
    private GoogleMap map;
    private double x = Double.MIN_VALUE;
    private double y = Double.MIN_VALUE;
    private Scene gmScene;
    private Exif marker;


    /**
     * Default Constructor
     *
     *
     */
    public GoogleMapsScene(){
        this.mapView = new GoogleMapView();
        this.mapView.addMapInializedListener(this);
        this.marker =null;
    }

    /**
     * Constructor of a new GoogleMapsScene-Objekt
     *
     * @param marker a Exif-Objekt to be displayed as a marker
     */
    public GoogleMapsScene(Exif marker){
        this.mapView = new GoogleMapView();
        this.mapView.addMapInializedListener(this);
        this.marker =marker;
    }

    /**
     * Constructor of a new GoogleMapsScene-Objekt
     *
     *
     * @param x the X-Koordinate of the Country/city
     * @param y the Y-Koordinate of the Country/city
     * @param marker a Exif-Objekt to be displayed as a marker
     */
    public GoogleMapsScene(double x, double y, Exif marker){
        this.x=x;
        this.y=y;
        this.marker=marker;
        this.mapView = new GoogleMapView();
        this.mapView.addMapInializedListener(this);

    }


    /**
     * Initialising GoogleMap
     * default --> show the global view (x and y = Double_MinValue)
     * Consturctor (exif) --> a marker will be placed at the exif-Koordinate
     * Constructor (x,y,exif)--> focus Position x,y ;  a marker will be placed at the exif-Koordinate
     *
     *
     * mapOption
     *  overviewMapControl (true --> switch between Map and Satelit)
     *  panControl(true --> show Navigation-element from GoogleMaps )
     *  rotateControl(true--> no effect)
     *  scaleControl (true--> no effect)
     *  streetViewControl(no effect)
     *  zoomControl (true--> show zoom-element from GoogleMaps)
     *  zoom(zoomfactor)
     */
    @Override
    public void mapInitialized() {
        //Set the initial properties of the map.
        logger.debug("Initializing Map ");
        if(this.x==Double.MIN_VALUE) {

            MapOptions mapOptions = new MapOptions();
            mapOptions.center(new LatLong(40.7033127, -73.979681))
                    .overviewMapControl(true)
                    .panControl(true)
                    .rotateControl(false)
                    .scaleControl(true)
                    .streetViewControl(false)
                    .zoomControl(true)
                    .zoom(2);
            map = mapView.createMap(mapOptions);

        }else{
            MapOptions mapOptions = new MapOptions();

            mapOptions.center(new LatLong(x, y))
                    .overviewMapControl(true)
                    .panControl(true)
                    .rotateControl(true)
                    .scaleControl(true)
                    .streetViewControl(false)
                    .zoomControl(true)
                    .zoom(12);

            map = mapView.createMap(mapOptions);
        }
        if(this.marker!=null){

            map.addMarker(new Marker(new MarkerOptions().position(new LatLong(Double.parseDouble(marker.getLongitude()),Double.parseDouble(marker.getLatitude()) ))
                    .visible(Boolean.TRUE)));

        }
    }

    /**
     *
     * @return GoogleMapView as a Scene
     */
    public Scene getScene(){
        logger.debug("returning Scene");
        this.gmScene = new Scene(this.mapView);
        return this.gmScene;
    }


}
