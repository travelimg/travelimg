package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.control.AwesomeMapScene;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import com.lynden.gmapsfx.javascript.object.LatLong;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
    private AwesomeMapScene map;
    @FXML
    private ToggleButton chooseLocationButton;

    @Autowired
    private SlideService slideService;

    @FXML
    private void initialize() {
        captionField.textProperty().addListener(this::handleCaptionChange);

        map.setClickCallback(this::handleMapClicked);
    }

    public void setPreview(Node node) {

    }

    @Override
    public void setEntities(Collection<MapSlide> entities) {
        super.setEntities(entities);

        if (getEntities().size() > 0) {
            MapSlide slide = getEntities().iterator().next();
            captionField.setText(slide.getCaption());

            map.clear();
            map.addMarker(slide.getLatitude(), slide.getLongitude());
            map.center(slide.getLatitude(), slide.getLongitude());
        }
    }

    private void updateCoordinates(double latitude, double longitude) {
        MapSlide slide = getEntities().iterator().next();

        slide.setLatitude(latitude);
        slide.setLongitude(longitude);

        try {
            slideService.update(slide);
            onUpdate();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Ändern der Koordinaten", "");
        }

        map.clear();
        map.addMarker(latitude, longitude);
        map.center(latitude, longitude);
    }

    private void handleCaptionChange(Observable observable) {
        if (getEntities().size() == 0) {
            return;
        }

        MapSlide slide = getEntities().iterator().next();
        slide.setCaption(captionField.getText());

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
