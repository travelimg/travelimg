package at.ac.tuwien.qse.sepm.gui.controller.impl;


import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.control.AwesomeMapScene;
import at.ac.tuwien.qse.sepm.gui.controller.WorldmapView;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class WorldmapViewImpl implements WorldmapView {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private BorderPane root;
    @FXML
    private AwesomeMapScene mapScene;

    @Autowired
    private ClusterService clusterService;

    @FXML
    private void initialize() {
        mapScene.setOnLoaded(this::showPlaces);
    }

    private void showPlaces() {
        List<Place> places;
        try {
            places = clusterService.getAllPlaces();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden aller Orte", "");
            return;
        }

        places.forEach((place) -> mapScene.addMarker(place.getLatitude(), place.getLongitude()));
        mapScene.fitToMarkers();
    }
}
