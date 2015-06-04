package at.ac.tuwien.qse.sepm.gui;


import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class HighlightsViewController {

    @FXML private BorderPane borderPane;
    @FXML private GoogleMapsScene mapsScene;
    private GoogleMapView mapView;
    private GoogleMap googleMap;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private ArrayList<Polyline> polylines = new ArrayList<Polyline>();
    private Marker actualMarker;
    private int pos = 0;

    public void initialize(){
        //this is just for testing atm

        HBox vBox = new HBox();
        Button drawButton = new Button("draw polyline!");
        drawButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                clearMap();
                drawDestinationsAsPolyline(getDestinations());
            }
        });
        Button playButton = new Button("play!");
        playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if(pos==0){
                    clearMap();
                }
                playTheJourney(pos);
                pos++;
            }
        });
        vBox.getChildren().add(drawButton);
        vBox.getChildren().add(playButton);
        borderPane.setBottom(vBox);
    }

    public void setMap(GoogleMapsScene map) {
        this.mapsScene = map;
        mapView = map.getMapView();
        mapView.addMapInializedListener(new MapComponentInitializedListener() {
            @Override
            public void mapInitialized() {
                //wait for the map to initialize.
                googleMap = mapView.getMap();
            }
        });
        borderPane.setTop(map.getMapView());
    }

    private void drawDestinationsAsPolyline(LatLong[] path){
        //TODO note: this method will expect a list of destinations.

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.path(new MVCArray(path))
                .clickable(false)
                .draggable(false)
                .editable(false)
                .strokeColor("#ff4500")
                .strokeWeight(2)
                .visible(true);
        Polyline polyline = new Polyline(polylineOptions);
        googleMap.addMapShape(polyline);
        polylines.add(polyline);
        fitMarkersToScreen(path, 0, path.length - 1);
        for(int i = 0; i<path.length;i++) {
            Marker m = new Marker(new MarkerOptions().position(path[i]).icon("https://maps.gstatic.com/intl/en_us/mapfiles/markers2/measle_blue.png"));
            googleMap.addMarker(m);
            markers.add(m);
        }
    }

    private void playTheJourney(int pos) {

        LatLong [] path = getDestinations();

        fitMarkersToScreen(path, pos, pos+1);
        if(actualMarker!=null){
            googleMap.removeMarker(actualMarker);
        }
        Marker m = new Marker(new MarkerOptions().position(path[pos]).icon("https://maps.gstatic.com/intl/en_us/mapfiles/markers2/measle_blue.png"));
        googleMap.addMarker(m);
        markers.add(m);
        if (pos < path.length - 1) {

            PolylineOptions polylineOptions = new PolylineOptions();
            MVCArray mvcArray = new MVCArray();
            mvcArray.push(path[pos]);
            mvcArray.push(path[pos + 1]);
            polylineOptions.path(mvcArray)
                    .clickable(false)
                    .draggable(false)
                    .editable(false)
                    .strokeColor("#ff4500")
                    .strokeWeight(2)
                    .visible(true);
            Polyline polyline = new Polyline(polylineOptions);
            googleMap.addMapShape(polyline);
            polylines.add(polyline);
            actualMarker = new Marker(new MarkerOptions().position(path[pos+1]));
            googleMap.addMarker(actualMarker);
        }

    }

    private void fitMarkersToScreen(LatLong[] subpath, int from, int to) {
        Double ne_lat = null;
        Double ne_long = null;
        Double sw_lat = null;
        Double sw_long = null;
        for(int i = from; i<=to && i<subpath.length;i++) {
            if (ne_lat == null) {
                ne_lat = subpath[i].getLatitude();
            }
            if (ne_long == null) {
                ne_long = subpath[i].getLongitude();
            }
            if (sw_lat == null) {
                sw_lat = subpath[i].getLatitude();
            }
            if (sw_long == null) {
                sw_long = subpath[i].getLongitude();
            }
            if (subpath[i].getLatitude() > ne_lat) {
                ne_lat = subpath[i].getLatitude();
            }
            if (subpath[i].getLongitude() > ne_long) {
                ne_long = subpath[i].getLongitude();
            }
            if (subpath[i].getLatitude() < sw_lat) {
                sw_lat = subpath[i].getLatitude();
            }
            if (subpath[i].getLongitude() < sw_long) {
                sw_long = subpath[i].getLongitude();
            }
        }
        LatLong ne = new LatLong(ne_lat,ne_long);
        LatLong sw = new LatLong(sw_lat,sw_long);
        double latFraction = ((ne.latToRadians()) - sw.latToRadians()) / Math.PI;
        double lngDiff = ne.getLongitude() - sw.getLongitude();
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
        double latZoom = Math.floor(Math.log(borderPane.getHeight() / 256 / latFraction) / 0.6931472);
        double lngZoom = Math.floor(Math.log(borderPane.getWidth() / 256 / lngFraction) / 0.6931472);
        double min = Math.min(latZoom, lngZoom);
        min = Math.min(min,21);
        mapsScene.setZoom((int)min);
        mapsScene.setCenter((ne.getLatitude()+sw.getLatitude())/2,(ne.getLongitude()+sw.getLongitude())/2);
    }

    private LatLong[] getDestinations(){
        LatLong[] path={
                new LatLong(48.2363038,16.3478819),
                new LatLong( 48.236299,16.3478708),
                new LatLong(48.232022, 16.376037),
                new LatLong(48.216240, 16.396758),
                new LatLong(48.197900, 16.415591),
                new LatLong(48.189453, 16.403740),
                new LatLong(48.185354, 16.362832),
                new LatLong(48.194362, 16.343365),
                new LatLong(48.215007, 16.338477),
                new LatLong(48.161529, 16.369028)
        };
        return path;
    }

    private void clearMap(){
        for(Marker m : markers){
            googleMap.removeMarker(m);
        }
        markers.clear();
        for(Polyline p : polylines){
            googleMap.removeMapShape(p);
        }
        polylines.clear();
        pos = 0;
        if(actualMarker!=null){
            googleMap.removeMarker(actualMarker);
        }
    }
}
