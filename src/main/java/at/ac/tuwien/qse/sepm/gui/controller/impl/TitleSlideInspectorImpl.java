package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.TitleSlide;
import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.util.ColorUtils;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import javafx.beans.Observable;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;

public class TitleSlideInspectorImpl extends SlideInspectorImpl<TitleSlide> {

    @FXML
    private InspectorPane root;
    @FXML
    private TextField captionField;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Button deleteButton;

    @Autowired
    private SlideService slideService;

    @FXML
    private void initialize() {
        captionField.textProperty().addListener(this::handleCaptionChange);
        colorPicker.valueProperty().addListener(this::handleColorChange);
        deleteButton.setOnAction(this::handleDelete);
    }

    @Override
    public void setSlide(TitleSlide slide) {
        super.setSlide(slide);

        captionField.setText(slide.getCaption());
        colorPicker.setValue(ColorUtils.fromInt(slide.getColor()));
    }

    private void handleCaptionChange(Observable observable) {
        TitleSlide slide = getSlide();

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

    private void handleColorChange(Observable observable) {
        TitleSlide slide = getSlide();

        if (slide == null) {
            return;
        }

        slide.setColor(ColorUtils.toInt(colorPicker.getValue()));

        try {
            slideService.update(slide);
            onUpdate();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Ändern der Farbe", "");
        }
    }

    private void handleDelete(Event event) {
        TitleSlide slide = getSlide();

        if (slide == null) {
            return;
        }

        try {
            slideService.delete(slide);
            onUpdate();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Ändern des Textes", "");
        }
    }
}
