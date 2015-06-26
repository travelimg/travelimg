package at.ac.tuwien.qse.sepm.gui.controller.impl;


import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.control.GoogleMapScene;
import at.ac.tuwien.qse.sepm.gui.controller.WorldmapView;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

public class WorldmapViewImpl implements WorldmapView {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private BorderPane root;
    @FXML
    private GoogleMapScene mapScene;

    @Autowired
    private ClusterService clusterService;
    private HashMap<LatLong,Place> markerPlaces = new HashMap<>();
    @FXML
    private void initialize() {
        mapScene.setOnLoaded(this::showPlaces);
    }
    @Override
    public void ListenLatLong(LatLong latLong){
        System.out.println(latLong.toString());
    }
    private void showPlaces() {
        List<Place> places;
        try {
            places = clusterService.getAllPlaces();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden aller Orte", "");
            return;
        }

        places.forEach((place) -> mapScene.addMarker(
                new LatLong(place.getLatitude(), place.getLongitude())));
        places.forEach((place) -> markerPlaces
                .put(new LatLong(place.getLatitude(), place.getLongitude()), place));
        mapScene.fitToMarkers();

    }
}
