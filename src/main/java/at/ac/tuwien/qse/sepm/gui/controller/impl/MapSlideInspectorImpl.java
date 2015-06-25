package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.control.GoogleMapScene;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class MapSlideInspectorImpl extends SlideInspectorImpl<MapSlide> {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private InspectorPane root;
    @FXML
    private TextField captionField;
    @FXML
    private GoogleMapScene map;
    @FXML
    private ToggleButton chooseLocationButton;
    @FXML
    private Slider zoomSlider;

    @Autowired
    private SlideService slideService;

    @FXML
    private void initialize() {
        captionField.textProperty().addListener(this::handleCaptionChange);
        zoomSlider.valueProperty().addListener(this::handleZoomChange);

        map.setClickCallback(this::handleMapClicked);
    }

    @Override
    public void setSlide(MapSlide slide) {
        super.setSlide(slide);

        captionField.setText(slide.getCaption());
        zoomSlider.setValue(slide.getZoomLevel());

        map.clear();
        map.addMarker(new LatLong(slide.getLatitude(), slide.getLongitude()));
        map.center(new LatLong(slide.getLatitude(), slide.getLongitude()));
    }

    private void updateCoordinates(double latitude, double longitude) {
        MapSlide slide = getSlide();

        if (slide == null) {
            return;
        }

        slide.setLatitude(latitude);
        slide.setLongitude(longitude);

        try {
            slideService.update(slide);
            onUpdate();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Ändern der Koordinaten", "");
        }

        map.clear();
        map.addMarker(new LatLong(latitude, longitude));
        map.center(new LatLong(latitude, longitude));
    }

    private void handleCaptionChange(Observable observable) {
        MapSlide slide = getSlide();

        if (slide == null) {
            return;
        }

        slide.setCaption(captionField.getText());

        try {
            slideService.update(slide);
            onUpdate();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Ändern des Textes", "");
        }
    }

    private void handleZoomChange(Observable observable) {
        MapSlide slide = getSlide();

        if (slide == null) {
            return;
        }

        slide.setZoomLevel((int)zoomSlider.getValue());

        try {
            slideService.update(slide);
            onUpdate();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Ändern des Textes", "");
        }
    }

    private void handleMapClicked(LatLong position) {
        if (chooseLocationButton.isSelected()) {
            updateCoordinates(position.getLatitude(), position.getLongitude());

            chooseLocationButton.setSelected(false);
        }
    }
}
