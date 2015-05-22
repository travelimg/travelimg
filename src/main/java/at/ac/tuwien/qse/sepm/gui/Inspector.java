package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.TagService;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    @FXML private VBox tagSelectionContainer;
    @FXML private VBox mapContainer;
    @FXML private HBox ratingPickerContainer;
    @FXML private TableColumn<String, String> exifName;
    @FXML private TableColumn<String, String> exifValue;
    @FXML private TableView<Pair<String, String>> exifTable;

    private final RatingPicker ratingPicker = new RatingPicker();
    private TagSelector tagSelector;
    private GoogleMapsScene mapsScene;

    private ArrayList<Photo> activePhotos = new ArrayList<Photo>();

    @Autowired private Organizer organizer;
    @Autowired private PhotoService photoservice;
    @Autowired private ExifService exifService;
    @Autowired private TagService tagService;
    @Autowired private MainController mainController;

    /**
     * Adds a new photo to the selected ones.
     * <p/>
     * The photos metadata will be displayed in the inspector widget.
     *
     * @param photo The new added photo.
     */
    public void addActivePhoto(Photo photo) {
        LOGGER.debug("addActivePhoto({})", photo);
        activePhotos.add(photo);
        mapsScene.addMarker(photo);
        showDetails(activePhotos.get(0));
    }

    public void removeActivePhoto(Photo photo){
        LOGGER.debug("removeActivePhoto({})", photo);
        activePhotos.remove(photo);
        mapsScene.removeMarker(photo);
    }

    @FXML private void initialize() {
        mapsScene = new GoogleMapsScene();
        tagSelector = new TagSelector(new TagListChangeListener(), photoservice, tagService);
        // if placeholder is hidden then it should not take up any space
        placeholder.managedProperty().bind(placeholder.visibleProperty());
        // hide placeholder when details are visible
        placeholder.visibleProperty().bind(Bindings.not(details.visibleProperty()));

        deleteButton.setOnAction(this::handleDelete);
        cancelButton.setOnAction(this::handleCancel);
        confirmButton.setOnAction(this::handleConfirm);
        ratingPickerContainer.getChildren().add(ratingPicker);
        ratingPicker.ratingProperty().addListener(this::handleRatingChanged);
        mapContainer.getChildren().add(mapsScene.getMapView());
        //addActivePhoto(null);
        tagSelectionContainer.getChildren().add(tagSelector);
    }

    private void handleDelete(Event event) {
        if (activePhotos.get(0) == null) {
            return;
        }

        try {
            photoservice.deletePhotos(activePhotos);
            activePhotos.clear();
        } catch (ServiceException e) {

        }
        mainController.deletePhotos();
        mapsScene.clearMarkers();
    }

    private void handleCancel(Event event) {
        // TODO
    }

    private void handleConfirm(Event event) {
        // TODO
    }

    private void handleRatingChanged(ObservableValue<? extends Rating> observable, Rating oldValue, Rating newValue) {
        LOGGER.debug("handleRatingChanged(~, {}, {})", oldValue, newValue);

        if (activePhotos.get(0) == null) {
            LOGGER.debug("No photo selected.");
            return;
        }

        if (activePhotos.get(0).getRating() == newValue) {
            LOGGER.debug("Photo already has rating of {}.", newValue);
            return;
        }

        LOGGER.debug("Setting photo rating from {} to {}.", activePhotos.get(0).getRating(), newValue);
        activePhotos.get(0).setRating(newValue);

        try {
            photoservice.savePhotoRating(activePhotos.get(0));
        } catch (ServiceException ex) {
            LOGGER.error("Failed saving photo rating.", ex);
            LOGGER.debug("Resetting rating from {} to {}.", activePhotos.get(0).getRating(), oldValue);

            // Undo changes.
            activePhotos.get(0).setRating(oldValue);
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
            details.setVisible(false);
            return;
        }

        details.setVisible(true);
        tagSelector.showCurrentlySetTags(photo);
        ratingPicker.setRating(photo.getRating());

        try {
            Exif exif = exifService.getExif(photo);
            ObservableList<Pair<String, String>> exifData = FXCollections.observableArrayList(
                    new Pair<>("Aufnahmedatum", photo.getDatetime().toString()),
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

    private class TagListChangeListener implements ListChangeListener<Tag> {
        public void onChanged(ListChangeListener.Change<? extends Tag> change) {
            while(change.next()) {
                List<Photo> photoList = new ArrayList<>();
                if (activePhotos.get(0) != null) {
                    photoList.add(activePhotos.get(0));
                }

                if (change.wasAdded()) {
                    Tag added = change.getAddedSubList().get(0);
                    try {
                        photoservice.addTagToPhotos(photoList, added);
                    } catch (ServiceException ex) {
                        //TODO Dialog
                    }
                }
                if (change.wasRemoved()) {
                    Tag removed = change.getRemoved().get(0);
                    try {
                        photoservice.removeTagFromPhotos(photoList, removed);
                    } catch (ServiceException ex) {
                        //TODO Dialog
                    }
                }
            }
        }
    }
}
