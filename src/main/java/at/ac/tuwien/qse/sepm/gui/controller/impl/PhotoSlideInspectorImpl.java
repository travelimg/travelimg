package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.gui.control.InspectorPane;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PhotoSlideInspectorImpl extends SlideInspectorImpl<PhotoSlide> {

    @FXML
    private InspectorPane root;

    @FXML
    private void initialize() {

        Label captionLabel = new Label();
        captionLabel.setText("Beschriftung");

        TextField captionField = new TextField();

        root.getChildren().addAll(captionLabel, captionField);
    }

    public void setPreview(Node node) {

    }
}
