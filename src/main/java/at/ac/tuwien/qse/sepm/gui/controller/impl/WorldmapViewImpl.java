package at.ac.tuwien.qse.sepm.gui.controller.impl;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

import java.util.LinkedList;
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
    private List<Place> places = new LinkedList<>();

    @FXML
    private void initialize() {
        mapScene.setOnLoaded(this::showPlaces);
        mapScene.setMarkerClickCallback(this::handleMarkerClicked);
        clusterService.subscribePlaceChanged(place -> {
            Platform.runLater(() -> {
                this.addPlace(place);
                mapScene.fitToMarkers();
            });
        });
    }

    private void addPlace(Place place) {
        String caption = String.format("%s, %s", place.getCity(), place.getCountry());
        mapScene.addMarker(new LatLong(place.getLatitude(), place.getLongitude()), caption);

        places.add(place);
    }

    private void showPlaces() {
        try {
            clusterService.getAllPlaces().forEach(this::addPlace);
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden aller Orte", "");
            return;
        }

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
