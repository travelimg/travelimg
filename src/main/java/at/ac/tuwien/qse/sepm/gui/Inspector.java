package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.gui.dialogs.DeleteDialog;
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
import javafx.scene.control.Label;
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

import java.util.*;
import java.util.function.Consumer;

/**
 * Controller for the inspector view which is used for modifying meta-data of a photo.
 */
public class Inspector {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML private BorderPane root;
    @FXML private Label placeholder;
    @FXML private Node details;
    @FXML private Button deleteButton;
    @FXML private Button dropboxButton;
    @FXML private VBox tagSelectionContainer;
    @FXML private VBox mapContainer;
    @FXML private HBox ratingPickerContainer;
    @FXML private TableColumn<String, String> exifName;
    @FXML private TableColumn<String, String> exifValue;
    @FXML private TableView<Pair<String, String>> exifTable;

    private final RatingPicker ratingPicker = new RatingPicker();
    private TagSelector tagSelector;
    private GoogleMapsScene mapsScene;

    private final List<Photo> activePhotos = new ArrayList<>();

    private Consumer<Collection<Photo>>  updateHandler;
    private Consumer<Collection<Photo>>  deleteHandler;

    @Autowired private PhotoService photoservice;
    @Autowired private ExifService exifService;
    @Autowired private TagService tagService;

    /**
     * Get the photos the inspector currently operates on.
     *
     * @return collection of photos
     */
    public Collection<Photo> getActivePhotos() {
        return new ArrayList<>(activePhotos);
    }

    /**
     * Get the photos the inspector currently operates on.
     *
     * @return collection of photos
     */
    public void setActivePhotos(Collection<Photo> photos) {
        if (photos == null) photos = new LinkedList<>();
        activePhotos.clear();
       // mapsScene.removeAktiveMarker();
        activePhotos.addAll(photos);
        activePhotos.forEach(photo -> mapsScene.addMarker(photo));
        showDetails(activePhotos);
    }

    /**
     * Set a function that is invoked when the active photos are modified.
     *
     * @param updateHandler
     */
    public void setUpdateHandler(Consumer<Collection<Photo>>  updateHandler) {
        this.updateHandler = updateHandler;
    }

    /**
     * Set a function that is invoked when the active photos are deleted.
     *
     * @param deleteHandler
     */
    public void setDeleteHandler(Consumer<Collection<Photo>> deleteHandler) {
        this.deleteHandler = deleteHandler;
    }
    public GoogleMapsScene getMap(){
        return this.mapsScene;
    }
    @FXML private void initialize() {
        mapsScene = new GoogleMapsScene();
        tagSelector = new TagSelector(new TagListChangeListener(), photoservice, tagService);
        // if placeholder is hidden then it should not take up any space
        placeholder.managedProperty().bind(placeholder.visibleProperty());
        // hide placeholder when details are visible
        placeholder.visibleProperty().bind(Bindings.not(details.visibleProperty()));

        deleteButton.setOnAction(this::handleDelete);
        dropboxButton.setOnAction(this::handleDropbox);
        ratingPickerContainer.getChildren().add(ratingPicker);
        ratingPicker.ratingProperty().addListener(this::handleRatingChanged);
        mapContainer.getChildren().add(mapsScene.getMapView());
        //addActivePhoto(null);
        tagSelectionContainer.getChildren().add(tagSelector);
    }
    public void setMap(GoogleMapsScene map){
        this.mapsScene = map;
        this.mapsScene = new GoogleMapsScene();
        this.mapsScene.removeAktiveMarker();
        mapContainer.getChildren().add(mapsScene.getMapView());
        details.setVisible(false);
    }
    private void handleDelete(Event event) {
        if (activePhotos.isEmpty()) {
            return;
        }

        DeleteDialog deleteDialog = new DeleteDialog(root,activePhotos);
        Optional<List<Photo>> photos = deleteDialog.showForResult();
        if (!photos.isPresent()) return;

        List<Photo> photoss = new ArrayList<>();
        //photoss.add(photo);
        //organizer.reloadPhotos();
        try {
            photoservice.deletePhotos(activePhotos);
            onDelete();
        } catch (ServiceException ex) {
            LOGGER.error("failed deleting photos", ex);
            InfoDialog dialog = new InfoDialog(root, "Fehler");
            dialog.setError(true);
            dialog.setHeaderText("Fehler beim Löschen");
            dialog.setContentText("Die ausgewählten Fotos konnten nicht gelöscht werden.");
            dialog.showAndWait();
        }

    }

    private void handleDropbox(Event event) {
        // TODO
    }

    private void handleRatingChanged(ObservableValue<? extends Rating> observable, Rating oldValue, Rating newValue) {
        LOGGER.debug("handleRatingChanged(~, {}, {})", oldValue, newValue);

        if (activePhotos.size() == 0) {
            LOGGER.debug("No photo selected.");
            return;
        }

        for (Photo photo : activePhotos) {

            if (photo.getRating() == newValue) {
                LOGGER.debug("Photo already has rating of {}.", newValue);
                return;
            }

            LOGGER.debug("Setting photo rating from {} to {}.", photo.getRating(),
                    newValue);
            activePhotos.get(0).setRating(newValue);

            try {
                photoservice.savePhotoRating(photo);
                onUpdate();

            } catch (ServiceException ex) {
                LOGGER.error("Failed saving photo rating.", ex);
                LOGGER.debug("Resetting rating from {} to {}.", activePhotos.get(0).getRating(),
                        oldValue);

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
    }

    private void showDetails(List<Photo> photos) {
        if (photos.size() == 0) {
            details.setVisible(false);
            placeholder.setText("Kein Foto ausgewählt.");
            return;
        }

        if (photos.size() > 1) {
            details.setVisible(false);
            placeholder.setText("Mehrere Foto ausgewählt.");
            return;
        }

        details.setVisible(true);

        // TODO: show details for all photos
        Photo photo = photos.get(0);
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

    private void onUpdate() {
        if (updateHandler != null) {
            updateHandler.accept(getActivePhotos());
        }
    }

    private void onDelete() {
        if (deleteHandler != null) {
            deleteHandler.accept(getActivePhotos());
        }
        setActivePhotos(null);
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
                        photoservice.addTagToPhotos(activePhotos, added);
                        onUpdate();
                    } catch (ServiceException ex) {
                        LOGGER.error("failed adding tag", ex);
                        InfoDialog dialog = new InfoDialog(root, "Fehler");
                        dialog.setError(true);
                        dialog.setHeaderText("Speichern fehlgeschlagen");
                        dialog.setContentText("Die Kategorien für das Foto konnten nicht gespeichert werden.");
                        dialog.showAndWait();
                    }
                }
                if (change.wasRemoved()) {
                    Tag removed = change.getRemoved().get(0);
                    try {
                        photoservice.removeTagFromPhotos(activePhotos, removed);
                        onUpdate();
                    } catch (ServiceException ex) {
                        LOGGER.error("failed removing tag", ex);
                        InfoDialog dialog = new InfoDialog(root, "Fehler");
                        dialog.setError(true);
                        dialog.setHeaderText("Speichern fehlgeschlagen");
                        dialog.setContentText("Die Kategorien für das Foto konnten nicht gespeichert werden.");
                        dialog.showAndWait();
                    }
                }
            }
        }
    }
}
