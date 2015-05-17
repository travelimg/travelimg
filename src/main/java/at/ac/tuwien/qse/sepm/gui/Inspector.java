package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.beans.binding.Bindings;
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
    @FXML private Node placeholder;
    @FXML private Node details;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private VBox mapContainer;
    @FXML private TableColumn<String, String> exifName;
    @FXML private TableColumn<String, String> exifValue;
    @FXML private TableView<Pair<String, String>> exifTable;

    private GoogleMapsScene mapsScene;
    private Photo photo;

    @Autowired private Organizer organizer;
    @Autowired private PhotoService photoservice;
    @Autowired private ExifService exifService;
    /**
     * Set the active photo.
     *
     * The photos metadata will be displayed in the inspector widget.
     *
     * @param photo The active photo for which to show further information
     */
    public void setActivePhoto(Photo photo) {
        this.photo = photo;

        showDetails(photo);
    }

    public void setMap(GoogleMapsScene mapsScene){
        this.mapsScene=mapsScene;
        if(mapContainer.getChildren().contains(mapsScene.getMapView())){
            mapContainer.getChildren().remove(mapsScene.getMapView());
        }
        mapContainer.getChildren().add(mapsScene.getMapView());

        this.mapsScene=mapsScene;
        mapsScene.removeAktiveMarker();
    }
    @FXML private void initialize() {


        // if placeholder is hidden then it should not take up any space
        placeholder.managedProperty().bind(placeholder.visibleProperty());
        // hide placeholder when details are visible
        placeholder.visibleProperty().bind(Bindings.not(details.visibleProperty()));

        deleteButton.setOnAction(this::handleDelete);
        cancelButton.setOnAction(this::handleCancel);
        confirmButton.setOnAction(this::handleConfirm);

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

    private void showDetails(Photo photo) {
        if (photo == null) {
            details.setVisible(false);
            return;
        }

        details.setVisible(true);

        mapsScene.addMarker(photo);

        Exif exif = null;
        try {
            exif = exifService.getExif(photo);
            ObservableList<Pair<String, String>> exifData = FXCollections.observableArrayList(
                    new Pair<>("Aufnahmedatum", photo.getDate().toString()),
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
        } catch (ServiceException e) {
            //TODO Dialog
        }

    }
}