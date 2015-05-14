package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;

import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import javafx.scene.layout.Pane;
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

    @FXML private Pane ratingPickerContainer;

    private final RatingPicker ratingPicker = new RatingPicker();

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
                new Pair<String, String>("Aufnahmedatum", exif.getDate().toString()),
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
        mapsScene.setMaxSize(200, 200);
        mapsScene.addMarker(photo);

        ratingPicker.setRating(photo.getRating());
    }

    @FXML private void initialize() {
        deleteButton.setOnAction(this::handleDelete);
        cancelButton.setOnAction(this::handleCancel);
        confirmButton.setOnAction(this::handleConfirm);
        this.mapsScene = new GoogleMapsScene();
        contentBox2.getChildren().add(mapsScene.getMapView());

        ratingPickerContainer.getChildren().add(ratingPicker);
        ratingPicker.ratingProperty().addListener(this::handleRatingChanged);
    }

    private void handleDelete(Event event) {
        if (photo != null) {

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

    private void handleRatingChanged(ObservableValue<? extends Rating> observable, Rating oldValue, Rating newValue) {
        if (photo == null) return;
        if (photo.getRating() == newValue) return;
        photo.setRating(newValue);
        try {
            photoservice.savePhotoRating(photo);
            throw new ServiceException();
        } catch (ServiceException ex) {

            InfoDialog dialog = new InfoDialog(root, "Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Bewertung fehlgeschlagen");
            dialog.setContentText("Die Bewertung für das Foto konnte nicht gespeichert werden.");
            dialog.showAndWait();

            // Undo changes.
            photo.setRating(oldValue);
            ratingPicker.setRating(oldValue);
        }
    }
}
