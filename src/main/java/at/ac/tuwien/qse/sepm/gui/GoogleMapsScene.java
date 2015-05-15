package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.dialogs.ImportDialog;
import at.ac.tuwien.qse.sepm.service.impl.ExifServiceImpl;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by christoph on 08.05.15.
 */
public class GoogleMapsScene implements MapComponentInitializedListener {

    private static final Logger logger = LogManager.getLogger(ExifServiceImpl.class);

    private GoogleMapView mapView;
    private GoogleMap map;
    //new LatLong(40.7033127, -73.979681); // the default Location
    private Marker actualMarker;

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
     * calculate a GPS-Coordinate (Grad,Minutes,Seconds) to GPS-Coordinate(Decimal)
     * @param gps GPS-Coordinate (Grad,Minutes,Seconds)
     * @return GPS-Coordinate (Decimal)
     */
    private double calculate(String gps){
        String[] longi = gps.split(" ");

        double grad = Double.parseDouble(longi[0]);

        double min = Double.parseDouble(longi[1]);

        double sec = Double.parseDouble(longi[2]);

        double erg= (((sec/60)+min)/60)+grad;



       return erg;
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
                .zoom(2);
        map = mapView.createMap(mapOptions);
    }

    /**
     *
     * @return the GoogleMapView
     */
    public GoogleMapView getMapView() {

        return mapView;
    }

    /**
     * set the Size of the map-window
     * @param x width
     * @param y height
     */
    public void setMaxSize(double x, double y){
        mapView.setMaxSize(x,y);
    }

    public void addMarker(Photo photo){
        if(actualMarker!=null)
            map.removeMarker(actualMarker);
        actualMarker = new Marker(new MarkerOptions().position(new LatLong(photo.getExif().getLatitude(),
                photo.getExif().getLongitude())).visible(Boolean.TRUE));
        mapView.setCenter(photo.getExif().getLatitude(), photo.getExif().getLongitude());
        mapView.setZoom(12);
        //actualMarker.setTitle(photo.getPhotographer().getName());
        map.addMarker(actualMarker);

    }

}
