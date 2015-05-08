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
     * Default Konstruktor
     * eine neue GoogleMap wird erstellen
     * angezeigt wird die Welt --> zoom = 2
     */
    public GoogleMapsScene(){
        this.mapView = new GoogleMapView();
        this.mapView.addMapInializedListener(this);
        this.marker =null;
    }

    /**
     * eine neue GoogleMap wird erstellen
     * uebergeben wird eine Liste mit Koordinaten, welche auf der Map gekennzeichnet werden
     * @param marker
     */
    public GoogleMapsScene(Exif marker){
        this.mapView = new GoogleMapView();
        this.mapView.addMapInializedListener(this);
        this.marker =marker;
    }

    /**
     * eine neue GoogleMap wird erstellen
     * uebergeben wird eine Koordinate (x,y) welche das Land / die Stadt auf der Map in den focus rueckt
     * zoom = 12
     *
     * @param x die X-Koordinate des Landes/der Stadt
     * @param y die Y-Koordinate des Landes/der Stadt
     * @param marker eine Liste mit Koordinaten, welche auf der Map gekennzeichnet werden
     */
    public GoogleMapsScene(double x, double y, Exif marker){
        this.x=x;
        this.y=y;
        this.marker=marker;
        this.mapView = new GoogleMapView();
        this.mapView.addMapInializedListener(this);

    }


    /**
     * Inizialisiert die GoogleMap
     * wenn Default-Kontruktor --> Welt wird angezeigt (default --> x und y sind Double_MinValue)
     * sonst wird die X/Y-koordinate uebergeben (Stadt/Land)
     *
     * wenn die Koordinatenliste nicht leer ist, werden die Koordinaten ausgelesen und Marker gesetzt
     *
     *
     * mapOption
     *  overviewMapControl (true --> wechsel zwischen Map und Satelit)
     *  panControl(true --> Navigations-Steuerkreuz wird angezeigt )
     *  rotateControl(true--> keine Auswirkungen)
     *  scaleControl (true--> keine Auswirkungen)
     *  streetViewControl(bruachen wir nicht ?)
     *  zoomControl (true--> Zoom-Navigarion wird angezeigt)
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
   // public GoogleMapView getMarker(Exif e){
    //    return new GoogleMapsServiceImpl(e).getMap();
   // }
    /**
     * GoogleMapsServiceImpl muss existieren
     * @return GoogleMapView fuer eine Scene wird zurueckgegeben
     */
    public Scene getScene(){
        logger.debug("returning Scene");
        this.gmScene = new Scene(this.mapView);
        return this.gmScene;
    }


}
