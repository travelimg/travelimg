package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class MapSlideInspectorImpl extends SlideInspectorImpl<MapSlide> {

    @FXML
    private InspectorPane root;
    @FXML
    private TextField captionField;

    @Autowired
    private SlideService slideService;

    @FXML
    private void initialize() {
        captionField.textProperty().addListener(this::handleCaptionChange);
    }

    public void setPreview(Node node) {

    }

    @Override
    public void setEntities(Collection<MapSlide> entities) {
        super.setEntities(entities);

        if (getEntities().size() > 0) {
            MapSlide slide = getEntities().iterator().next();
            captionField.setText(slide.getCaption());
        }
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
}
