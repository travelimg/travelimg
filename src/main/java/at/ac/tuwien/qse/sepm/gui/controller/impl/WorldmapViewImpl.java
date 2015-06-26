package at.ac.tuwien.qse.sepm.gui.controller.impl;


import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.gui.MainController;
import at.ac.tuwien.qse.sepm.gui.control.GoogleMapScene;
import at.ac.tuwien.qse.sepm.gui.controller.Organizer;
import at.ac.tuwien.qse.sepm.gui.controller.WorldmapView;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import jdk.nashorn.internal.codegen.CompilerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class WorldmapViewImpl implements WorldmapView {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private BorderPane root;
    @FXML
    private GoogleMapScene mapScene;

    @Autowired
    private ClusterService clusterService;
    @Autowired
    private Organizer organizer;
    @Autowired
    private MainController mainController;
    private HashMap<LatLong,Place> markerPlaces = new HashMap<>();
    private LatLong selectedMarker;
    @FXML
    private void initialize() {

        mapScene.setOnLoaded(this::showPlaces);
        mapScene.setMarkerClickCallback((position) -> clickMarker(position));
    }
    @Override
    public void ListenLatLong(LatLong latLong){
        CallbackHandler callbackHandler = new CallbackHandler() {
            @Override public void handle(Callback[] callbacks)
                    throws IOException, UnsupportedCallbackException {

            }
        };
    }
    private void showPlaces() {
        List<Place> places;
        try {
            places = clusterService.getAllPlaces();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Laden aller Orte", "");
            return;
        }

        places.forEach((place) -> mapScene
                .addMarker(new LatLong(place.getLatitude(), place.getLongitude())));
        places.forEach((place) -> markerPlaces
                .put(new LatLong(place.getLatitude(), place.getLongitude()), place));
        mapScene.fitToMarkers();



    }

    /**
     * handle click on Marker 
     * @param ll the LatLong from Marker
     */
    public void clickMarker(LatLong ll){
        for(LatLong l : markerPlaces.keySet()){
            // Math.round .. because the koord s did not match
            if(Math.round(l.getLatitude())==Math.round(ll.getLatitude()) && Math.round(l.getLongitude())==Math.round(
                    ll.getLongitude())){
                Place p = markerPlaces.get(l);
                mainController.worldMapKlick(p);
            }
        }


    }
}
