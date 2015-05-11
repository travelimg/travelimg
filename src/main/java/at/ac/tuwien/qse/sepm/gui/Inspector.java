package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import com.lynden.gmapsfx.GoogleMapView;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.tools.Platform;

import org.springframework.beans.factory.annotation.Autowired;

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






//   private Photo photo = null;

    @FXML private VBox contentBox2;

   // @FXML private Label proofOfConceptLabel;

   private GoogleMapsScene mapsScene;

    @FXML private Label proofOfConceptLabel;

    @Autowired private Organizer organizer;

    private Photo photo = null;
    @Autowired private PhotoService photoservice;


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

        //this.mapsScene = new GoogleMapsScene(photo.getExif());
        //contentBox2.getChildren().clear();

        //contentBox2.getChildren().add(mapsScene.getMapView());

        mapsScene.addMarker(photo);


    }

    @FXML
    private void initialize() {
        deleteButton.setOnAction(this::handleDelete);
        cancelButton.setOnAction(this::handleCancel);
        confirmButton.setOnAction(this::handleConfirm);
        this.mapsScene= new GoogleMapsScene();
        contentBox2.getChildren().add(mapsScene.getMapView());

    }

    private void handleDelete(Event event) {
        if(photo!=null){

            List<Photo> photolist = new ArrayList<Photo>();
            photolist.add(photo);
           organizer.reloadPhotos();
            try {
                photoservice.deletePhotos(photolist);
            } catch (ServiceException e) {
                System.out.println(e);
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
