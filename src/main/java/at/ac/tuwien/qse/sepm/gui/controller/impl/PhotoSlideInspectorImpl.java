package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class PhotoSlideInspectorImpl extends SlideInspectorImpl<PhotoSlide> {

    @FXML
    private InspectorPane root;

    @Autowired
    private SlideService slideService;

    private TextField captionField;

    @FXML
    private void initialize() {
        System.out.println("balu");
        Label captionLabel = new Label();
        captionLabel.setText("Beschriftung");

        captionField = new TextField();

        root.getChildren().addAll(captionLabel, captionField);

        captionField.textProperty().addListener(this::handleCaptionChange);
    }

    public void setPreview(Node node) {

    }

    @Override
    public void setEntities(Collection<PhotoSlide> entities) {
        super.setEntities(entities);

        if (getEntities().size() > 0) {
            PhotoSlide slide = getEntities().iterator().next();
            captionField.setText(slide.getCaption());
        }
    }

    private void handleCaptionChange(Observable observable) {
        if (getEntities().size() == 0) {
            return;
        }

        PhotoSlide slide = getEntities().iterator().next();
        slide.setCaption(captionField.getText());

        try {
            slideService.update(slide);
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Ändern des Textes", "");
        }
    }
}
