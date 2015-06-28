package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import javafx.beans.Observable;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;

public class PhotoSlideInspectorImpl extends SlideInspectorImpl<PhotoSlide> {

    @FXML
    private InspectorPane root;
    @FXML
    private TextField captionField;
    @FXML
    private Button deleteButton;

    @Autowired
    private SlideService slideService;

    @FXML
    private void initialize() {
        captionField.textProperty().addListener(this::handleCaptionChange);
        deleteButton.setOnAction(this::handleDelete);
    }

    @Override
    public void setSlide(PhotoSlide slide) {
        super.setSlide(slide);

        captionField.setText(slide.getCaption());
    }

    private void handleCaptionChange(Observable observable) {
        PhotoSlide slide = getSlide();
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

    private void handleDelete(Event event) {
        PhotoSlide slide = getSlide();

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
