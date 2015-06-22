package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.TitleSlide;
import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.util.ColorUtils;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.SlideService;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class TitleSlideInspectorImpl extends SlideInspectorImpl<TitleSlide> {

    @FXML
    private InspectorPane root;
    @FXML
    private TextField captionField;
    @FXML
    private ColorPicker colorPicker;

    @Autowired
    private SlideService slideService;

    @FXML
    private void initialize() {
        captionField.textProperty().addListener(this::handleCaptionChange);
        colorPicker.valueProperty().addListener(this::handleColorChange);
    }

    public void setPreview(Node node) {

    }

    @Override
    public void setEntities(Collection<TitleSlide> entities) {
        super.setEntities(entities);

        if (getEntities().size() > 0) {
            TitleSlide slide = getEntities().iterator().next();
            captionField.setText(slide.getCaption());
            colorPicker.setValue(ColorUtils.fromInt(slide.getColor()));
        }
    }

    private void handleCaptionChange(Observable observable) {
        if (getEntities().size() == 0) {
            return;
        }

        TitleSlide slide = getEntities().iterator().next();
        slide.setCaption(captionField.getText());

        try {
            slideService.update(slide);
            onUpdate();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Ändern des Textes", "");
        }
    }

    private void handleColorChange(Observable observable) {
        if (getEntities().size() == 0) {
            return;
        }

        TitleSlide slide = getEntities().iterator().next();
        slide.setColor(ColorUtils.toInt(colorPicker.getValue()));

        try {
            slideService.update(slide);
            onUpdate();
        } catch (ServiceException ex) {
            ErrorDialog.show(root, "Fehler beim Ändern der Farbe", "");
        }
    }
}
