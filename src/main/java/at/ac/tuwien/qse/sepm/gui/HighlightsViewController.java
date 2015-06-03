package at.ac.tuwien.qse.sepm.gui;


import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class HighlightsViewController {

    private GoogleMapView mapView;
    private GoogleMap googleMap;
    @FXML
    private BorderPane borderPane;

    public void initialize(){
        GoogleMapsScene mapsScene = new GoogleMapsScene();
        this.mapView = mapsScene.getMapView();
        this.borderPane.setTop(mapsScene.getMapView());
        mapView.addMapInializedListener(new MapComponentInitializedListener() {
            @Override
            public void mapInitialized() {
                //wait for the map to initialize.
                googleMap = mapView.getMap();
            }
        });
    }
}
