package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import com.lynden.gmapsfx.javascript.object.LatLong;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class WorldmapView {
    @FXML private BorderPane border;
    private GoogleMapsScene worldMap;
    @Autowired private PhotoService photoService;

    public WorldmapView(){

    }
    @FXML private void initialize() {

    }
    public void setMap(GoogleMapsScene map){
        this.worldMap = map;

        worldMap.removeAktiveMarker();
        try {
            worldMap.addMarkerList(photoService.getAllPhotos());
//            border.getChildren().add(worldMap.getMapView());
        } catch (ServiceException e) {
            //TODO
        }
        worldMap.setCenter(70.7385, -90.9871);
        worldMap.setZoom(2);
        border.setCenter(worldMap.getMapView());
    }
    public GoogleMapsScene getMap(){
        return this.worldMap;
    }

}
