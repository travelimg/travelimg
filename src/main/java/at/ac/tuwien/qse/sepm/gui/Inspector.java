package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Controller for the inspector view which is used for modifying meta-data of a photo.
 */
public class Inspector {

    @FXML private BorderPane root;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;

    @FXML private Label proofOfConceptLabel;

    private Photo photo = null;

    public Inspector() {

    }

    /**
     * Set the active photo.
     *
     * The photos metadate will be displayed in the inspector widget.
     *
     * @param photo The active photo for which to show further information
     */
    public void setActivePhoto(Photo photo) {
        this.photo = photo;

        proofOfConceptLabel.setText("Selected photo is: " + photo.getPath());
    }

    @FXML
    private void initialize() {
        deleteButton.setOnAction(this::handleDelete);
        cancelButton.setOnAction(this::handleCancel);
        confirmButton.setOnAction(this::handleConfirm);
    }

    private void handleDelete(Event event) {
        // TODO
    }

    private void handleCancel(Event event) {
        // TODO
    }

    private void handleConfirm(Event event) {
        // TODO
    }
}
