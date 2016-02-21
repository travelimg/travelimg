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

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.gui.control.GoogleMapScene;
import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import javafx.beans.Observable;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
    @FXML
    private Button deleteButton;

    @Autowired
    private SlideService slideService;

    @FXML
    private void initialize() {
        captionField.textProperty().addListener(this::handleCaptionChange);
        zoomSlider.valueProperty().addListener(this::handleZoomChange);

        map.setClickCallback(this::handleMapClicked);
        deleteButton.setOnAction(this::handleDelete);
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

        if (slide == null
                || ((Double.compare(latitude, slide.getLatitude()) == 0)
                && (Double.compare(longitude, slide.getLongitude()) == 0))) {
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
        String caption = captionField.getText();

        if (slide == null || slide.getCaption().equals(caption)) {
            return;
        }

        slide.setCaption(caption);

        try {
            slideService.update(slide);
            onUpdate();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Ändern des Textes", "");
        }
    }

    private void handleZoomChange(Observable observable) {
        MapSlide slide = getSlide();
        int zoomLevel = (int)zoomSlider.getValue();

        if (slide == null || zoomLevel == slide.getZoomLevel()) {
            return;
        }

        slide.setZoomLevel(zoomLevel);
        map.setZoom(zoomLevel);

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

    private void handleDelete(Event event) {
        MapSlide slide = getSlide();

        if (slide == null) {
            return;
        }

        try {
            slideService.delete(slide);
            onDelete(slide);
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Löschen der Folie", "");
        }
    }
}
