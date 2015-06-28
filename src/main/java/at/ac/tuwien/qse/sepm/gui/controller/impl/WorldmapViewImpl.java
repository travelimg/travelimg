package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.control.GoogleMapScene;
import at.ac.tuwien.qse.sepm.gui.controller.WorldmapView;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class WorldmapViewImpl implements WorldmapView {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private BorderPane root;
    @FXML
    private GoogleMapScene mapScene;

    @Autowired
    private ClusterService clusterService;
    @Autowired
    private MainControllerImpl mainController;
    private List<Place> places;

    @FXML
    private void initialize() {
        mapScene.setOnLoaded(this::showPlaces);
        mapScene.setMarkerClickCallback(this::handleMarkerClicked);
        clusterService.subscribePlaceChanged(place -> {
            Platform.runLater(() -> this.addPlace(place));
        });
    }

    private void addPlace(Place place) {
        String caption = String.format("%s, %s", place.getCity(), place.getCountry());
        mapScene.addMarker(new LatLong(place.getLatitude(), place.getLongitude()), caption);
        mapScene.fitToMarkers();
    }

    private void showPlaces() {
        LOGGER.debug("new Places");
        try {
            places = clusterService.getAllPlaces();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden aller Orte", "");
            return;
        }

        places.forEach(place -> {
            String caption = String.format("%s, %s", place.getCity(), place.getCountry());
            mapScene.addMarker(new LatLong(place.getLatitude(), place.getLongitude()), caption);
        });
        mapScene.fitToMarkers();
    }

    private void handleMarkerClicked(LatLong position) {
        // find place whose marker was selected
        Optional<Place> selected = places.stream()
                .filter(p -> Math.abs(p.getLatitude() - position.getLatitude()) < 0.9)
                .filter(p -> Math.abs(p.getLongitude() - position.getLongitude()) < 0.9)
                .findFirst();

        if (selected.isPresent()) {
            mainController.showGridWithPlace(selected.get());
        }
    }
}
