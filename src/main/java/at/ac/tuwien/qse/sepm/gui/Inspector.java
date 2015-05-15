package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import com.lynden.gmapsfx.GoogleMapView;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.controlsfx.tools.Platform;

import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the inspector view which is used for modifying meta-data of a photo.
 */
public class Inspector {

    @FXML private Button deleteButton;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private VBox mapContainer;
    @FXML private TableColumn<String, String> exifValue;
    @FXML private TableColumn<String, String> exifName;
    @FXML private TableView<Pair<String, String>> exifTable;

    private final GoogleMapsScene mapsScene = new GoogleMapsScene();
    private Photo photo = null;

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
        this.photo = photo;

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

        mapsScene.addMarker(photo);
    }

    @FXML private void initialize() {
        deleteButton.setOnAction(this::handleDelete);
        cancelButton.setOnAction(this::handleCancel);
        confirmButton.setOnAction(this::handleConfirm);
        mapContainer.getChildren().add(mapsScene.getMapView());
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
}
