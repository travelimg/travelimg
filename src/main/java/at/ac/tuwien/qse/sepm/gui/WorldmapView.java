package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;

import javafx.fxml.FXML;

import javafx.scene.layout.BorderPane;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;


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
