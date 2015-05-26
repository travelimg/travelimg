package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import com.lynden.gmapsfx.javascript.object.LatLong;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import netscape.javascript.JSException;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WorldmapView {
    @FXML private BorderPane border;
    private GoogleMapsScene worldMap;
    @Autowired private PhotoService photoService;
    private static final org.apache.logging.log4j.Logger logger = LogManager
            .getLogger(WorldmapView.class);

    public WorldmapView(){

    }
    @FXML private void initialize() {

    }
    public void setMap(GoogleMapsScene map){
        logger.debug("Worldmap wird erstellt");


               this.worldMap = map;

        worldMap.removeAktiveMarker();
        try {
            worldMap.addMarkerList(photoService.getAllPhotos());

        } catch (ServiceException e) {
            logger.debug(e);
        }
        worldMap.setCenter(70.7385, -90.9871);
        worldMap.setZoom(2);
        border.setCenter(worldMap.getMapView());
    }
    public GoogleMapsScene getMap(){
        return this.worldMap;
    }

}
