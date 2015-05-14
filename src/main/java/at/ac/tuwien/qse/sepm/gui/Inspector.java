package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
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

    @FXML private TableColumn<String, String> exifValue;

    @FXML private TableColumn<String, String> exifName;

    @FXML private TableView<Pair<String, String>> exifTable;

    @FXML private VBox contentBox2;

    private GoogleMapsScene mapsScene;

    @FXML private Label proofOfConceptLabel;

    @Autowired private Organizer organizer;

    private Photo photo = null;
    @Autowired private PhotoService photoservice;

    public Inspector() {

    }

    /**
     * Set the active photo.
     * <p>
     * The photos metadate will be displayed in the inspector widget.
     *
     * @param photo The active photo for which to show further information
     */
    public void setActivePhoto(Photo photo) {
        this.photo = photo;

        proofOfConceptLabel.setText("Selected photo is: " + photo.getPath());

        Exif exif = photo.getExif();
        ObservableList<Pair<String, String>> exifData = FXCollections.observableArrayList(
                new Pair<String, String>("Aufnahmedatum", photo.getDate().toString()),
                new Pair<String, String>("Kamerahersteller", exif.getMake()),
                new Pair<String, String>("Kameramodell", exif.getModel()),
                new Pair<String, String>("Belichtungszeit", exif.getExposure() + " Sek."),
                new Pair<String, String>("Blende", "f/" + exif.getAperture()),
                new Pair<String, String>("Brennweite", "" + exif.getFocalLength()),
                new Pair<String, String>("ISO", "" + exif.getIso()),
                new Pair<String, String>("Blitz", exif.isFlash()? "wurde ausgelöst" : "wurde nicht ausgelöst"),
                new Pair<String, String>("Höhe", "" + exif.getAltitude()));

        exifTable.setEditable(true);
        exifName.setCellValueFactory(new PropertyValueFactory<String, String>("Key"));
        exifValue.setCellValueFactory(new PropertyValueFactory<String, String>("Value"));
        exifTable.setItems(exifData);
        //this.mapsScene = new GoogleMapsScene(photo.getExif());
        //contentBox2.getChildren().clear();

        //contentBox2.getChildren().add(mapsScene.getMapView());
        mapsScene.setMaxSize(200,200);
        mapsScene.addMarker(photo);

    }

    @FXML private void initialize() {
        deleteButton.setOnAction(this::handleDelete);
        cancelButton.setOnAction(this::handleCancel);
        confirmButton.setOnAction(this::handleConfirm);
        this.mapsScene = new GoogleMapsScene();
        contentBox2.getChildren().add(mapsScene.getMapView());

    }

    private void handleDelete(Event event) {
        if (photo != null) {

            List<Photo> photolist = new ArrayList<Photo>();
            photolist.add(photo);
            try {
                photoservice.deletePhotos(photolist);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            organizer.reloadPhotos();
        }
    }

    private void handleCancel(Event event) {
        // TODO
    }

    private void handleConfirm(Event event) {
        // TODO
    }
}
