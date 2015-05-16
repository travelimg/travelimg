package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the inspector view which is used for modifying meta-data of a photo.
 */
public class Inspector {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML private BorderPane root;
    @FXML private Node placeholder;
    @FXML private Node details;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private Pane mapContainer;
    @FXML private Pane ratingPickerContainer;
    @FXML private TableColumn<String, String> exifName;
    @FXML private TableColumn<String, String> exifValue;
    @FXML private TableView<Pair<String, String>> exifTable;

    // FIXME: Map throws NullPointerException if it is not immediately visible.
    // If no photo is selected a placeholder is displayed in place of the Inspector content. But it
    // seems that the map immediately tries to measure its parent. So if it is removed from the
    // tree in the beginning it has no parent and the error occurs.
    private final GoogleMapsScene mapsScene = new GoogleMapsScene();
    private final RatingPicker ratingPicker = new RatingPicker();
    private Photo photo;

    @Autowired private Organizer organizer;
    @Autowired private PhotoService photoservice;

    /**
     * Set the active photo.
     *
     * The photos metadata will be displayed in the inspector widget.
     *
     * @param photo The active photo for which to show further information
     */
    public void setActivePhoto(Photo photo) {
        LOGGER.debug("setActivePhoto({})", photo);
        this.photo = photo;
        showDetails(photo);
    }

    @FXML private void initialize() {
        deleteButton.setOnAction(this::handleDelete);
        cancelButton.setOnAction(this::handleCancel);
        confirmButton.setOnAction(this::handleConfirm);
        ratingPickerContainer.getChildren().add(ratingPicker);
        ratingPicker.ratingProperty().addListener(this::handleRatingChanged);
        mapContainer.getChildren().add(mapsScene.getMapView());
        setActivePhoto(null);
    }

    private void handleDelete(Event event) {
        if (photo == null) return;

        List<Photo> photos = new ArrayList<>();
        photos.add(photo);
        organizer.reloadPhotos();
        try {
            photoservice.deletePhotos(photos);
        } catch (ServiceException e) {
            System.out.println(e);
        }
    }

    private void handleCancel(Event event) {
        // TODO
    }

    private void handleConfirm(Event event) {
        // TODO
    }

    private void handleRatingChanged(ObservableValue<? extends Rating> observable, Rating oldValue, Rating newValue) {
        LOGGER.debug("handleRatingChanged(~, {}, {})", oldValue, newValue);

        if (photo == null) {
            LOGGER.debug("No photo selected.");
            return;
        }

        if (photo.getRating() == newValue) {
            LOGGER.debug("Photo already has rating of {}.", newValue);
            return;
        }

        LOGGER.debug("Setting photo rating from {} to {}.", photo.getRating(), newValue);
        photo.setRating(newValue);

        try {
            photoservice.savePhotoRating(photo);
        } catch (ServiceException ex) {
            LOGGER.error("Failed saving photo rating.", ex);
            LOGGER.debug("Resetting rating from {} to {}.", photo.getRating(), oldValue);

            // Undo changes.
            photo.setRating(oldValue);
            // FIXME: Reset the RatingPicker.
            // This is not as simple as expected. Calling ratingPicker.setRating(oldValue) here
            // will complete and finish. But once the below dialog is closed ANOTHER selection-
            // change will occur in RatingPicker that is the same as the once that caused the error.
            // That causes an infinite loop of error dialogs.

            InfoDialog dialog = new InfoDialog(root, "Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Bewertung fehlgeschlagen");
            dialog.setContentText("Die Bewertung für das Foto konnte nicht gespeichert werden.");
            dialog.showAndWait();
        }
    }

    private void showDetails(Photo photo) {
        if (photo == null) {
            root.setCenter(placeholder);
            return;
        }

        root.setCenter(details);
        mapsScene.addMarker(photo);
        ratingPicker.setRating(photo.getRating());

        Exif exif = photo.getExif();
        ObservableList<Pair<String, String>> exifData = FXCollections.observableArrayList(
                new Pair<>("Aufnahmedatum", exif.getDate().toString()),
                new Pair<>("Kamerahersteller", exif.getMake()),
                new Pair<>("Kameramodell", exif.getModel()),
                new Pair<>("Belichtungszeit", exif.getExposure() + " Sek."),
                new Pair<>("Blende", "f/" + exif.getAperture()),
                new Pair<>("Brennweite", "" + exif.getFocalLength()),
                new Pair<>("ISO", "" + exif.getIso()),
                new Pair<>("Blitz", exif.isFlash()? "wurde ausgelöst" : "wurde nicht ausgelöst"),
                new Pair<>("Höhe", "" + exif.getAltitude()));
        exifName.setCellValueFactory(new PropertyValueFactory<>("Key"));
        exifValue.setCellValueFactory(new PropertyValueFactory<>("Value"));
        exifTable.setItems(exifData);
    }
}
