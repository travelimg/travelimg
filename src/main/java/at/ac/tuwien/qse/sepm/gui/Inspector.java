package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.impl.PhotoServiceImpl;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;

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
    private PhotoService photoservice;

    public Inspector() {
        photoservice = new PhotoServiceImpl();
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
        if(photo!=null){
            System.out.println(photo.toString());
            List<Photo> photolist = new ArrayList<Photo>();
            photolist.add(photo);
            try {
                photoservice.deletePhotos(photolist);
            } catch (ServiceException e) {
                System.out.println("Fehler beim löschen");
            }
        }
    }

    private void handleCancel(Event event) {
        // TODO
    }

    private void handleConfirm(Event event) {
        // TODO
    }
}
