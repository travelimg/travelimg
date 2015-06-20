package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.*;
import at.ac.tuwien.qse.sepm.gui.control.RatingPicker;
import at.ac.tuwien.qse.sepm.gui.control.TagSelector;
import at.ac.tuwien.qse.sepm.gui.controller.Inspector;
import at.ac.tuwien.qse.sepm.gui.dialogs.DeleteDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.dialogs.ExportDialog;
import at.ac.tuwien.qse.sepm.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the inspector view which is used for modifying meta-data of a photo.
 */
public class InspectorImpl implements Inspector {

    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Photo> activePhotos = new ArrayList<>();
    @FXML
    private BorderPane root;
    @FXML
    private Node details;
    @FXML
    private Node multiAlert;
    @FXML
    private VBox tagSelectionContainer;
    @FXML
    private VBox mapContainer;
    @FXML
    private VBox placeholder;
    @FXML
    private VBox ratingPickerContainer;
    @FXML
    private TableColumn<String, String> exifName;
    @FXML
    private TableColumn<String, String> exifValue;
    @FXML
    private ComboBox<Slideshow> slideshowsCombobox;
    @FXML
    private Button addToSlideshowButton;
    @FXML
    private TableView<Pair<String, String>> exifTable;
    private TagSelector tagSelector;
    private GoogleMapsScene mapsScene;
    private Runnable updateHandler;

    @Autowired
    private SlideshowViewImpl slideshowView;

    @Autowired
    private DropboxService dropboxService;
    @Autowired
    private PhotoService photoservice;
    @Autowired
    private ExifService exifService;
    @Autowired
    private TagService tagService;

    private RatingPicker ratingPicker = new RatingPicker();

    @Override public Collection<Photo> getActivePhotos() {
        return new ArrayList<>(activePhotos);
    }

    @Override public void setActivePhotos(Collection<Photo> photos) {
        if (photos == null) photos = new LinkedList<>();
        activePhotos.clear();
        // mapsScene.removeAktiveMarker();
        activePhotos.addAll(photos);
        activePhotos.forEach(photo -> mapsScene.addMarker(photo));
        showDetails(new ArrayList<>(activePhotos));
    }

    @Override public void setUpdateHandler(Runnable updateHandler) {
        this.updateHandler = updateHandler;
    }

    @Override public GoogleMapsScene getMap() {
        return this.mapsScene;
    }

    @Override public void setMap(GoogleMapsScene map) {
        this.mapsScene = map;
        this.mapsScene = new GoogleMapsScene();
        this.mapsScene.removeAktiveMarker();
        mapContainer.getChildren().clear();
        mapContainer.getChildren().add(mapsScene.getMapView());
        details.setVisible(false);
    }

    @Override public void refresh() {
        if (tagSelector != null) {
            tagSelector.initializeTagList();
        }
    }

    @FXML
    private void initialize() {
        mapsScene = new GoogleMapsScene();
        tagSelector = new TagSelector(new TagListChangeListener(), photoservice, tagService, root);
        ratingPicker.setRatingChangeHandler(this::handleRatingChange);
        ratingPickerContainer.getChildren().add(ratingPicker);

        mapContainer.getChildren().add(mapsScene.getMapView());
        tagSelectionContainer.getChildren().add(tagSelector);
        addToSlideshowButton.setOnAction(this::handleAddToSlideshow);

        slideshowsCombobox.setConverter(new SlideshowStringConverter());
        slideshowsCombobox.setItems(slideshowView.getSlideshows());
    }

    private void handleRatingChange(Rating newRating) {
        LOGGER.debug("rating picker changed to {}", newRating);

        if (activePhotos.size() == 0) {
            LOGGER.debug("No photo selected.");
            return;
        }

        for (Photo photo : activePhotos) {

            if (photo.getData().getRating() == newRating) {
                LOGGER.debug("photo already has rating of {}", newRating);
                continue;
            }

            Rating oldRating = photo.getData().getRating();
            LOGGER.debug("setting photo rating from {} to {}", oldRating, newRating);
            photo.getData().setRating(newRating);

            try {
                photoservice.editPhoto(photo);
            } catch (ServiceException ex) {
                LOGGER.error("Failed saving photo rating.", ex);
                LOGGER.debug("Resetting rating from {} to {}.", newRating, oldRating);

                // Undo changes.
                photo.getData().setRating(oldRating);
                // FIXME: Reset the RatingPicker.
                // This is not as simple as expected. Calling ratingPicker.getData().setRating(oldValue) here
                // will complete and finish. But once the below dialog is closed ANOTHER selection-
                // change will occur in RatingPicker that is the same as the once that caused the error.
                // That causes an infinite loop of error dialogs.

                ErrorDialog.show(root,
                        "Bewertung fehlgeschlagen",
                        "Die Bewertung für das Foto konnte nicht gespeichert werden."
                );
            }
        }

        onUpdate();
    }

    private void showDetails(List<Photo> photos) {

        // Depending on the number of photos selected show different elements.
        boolean noneActive = photos.size() == 0;
        boolean hasActive = !noneActive;
        boolean singleActive = photos.size() == 1;
        boolean multipleActive = photos.size() > 1;
        placeholder.setVisible(noneActive);
        placeholder.setManaged(noneActive);
        details.setVisible(hasActive);
        details.setManaged(hasActive);
        multiAlert.setVisible(multipleActive);
        multiAlert.setManaged(multipleActive);
        exifTable.setVisible(singleActive);
        exifTable.setManaged(singleActive);
        tagSelectionContainer.setVisible(singleActive);
        tagSelectionContainer.setManaged(singleActive);

        // Nothing to show.
        if (noneActive) return;

        // Only set a non-null rating if all active photos have the same rating.
        // Otherwise the rating picker should be indetermined.
        ratingPicker.setRating(null);
        List<Rating> ratings = photos.stream()
                .map(photo -> photo.getData().getRating())
                .distinct()
                .collect(Collectors.toList());
        if (ratings.size() == 1) {
            ratingPicker.setRating(ratings.get(0));
        }

        // Add the map markers for each photo.
        mapsScene.removeAktiveMarker();
        mapsScene.addMarkerList(activePhotos);


        // Show additional details for a single selected photo.
        if (singleActive) {
            Photo photo = photos.get(0);
            tagSelector.showCurrentlySetTags(photo);

            try {
                Exif exif = exifService.getExif(photo);

                List<Pair<String, String>> exifList = new ArrayList<Pair<String, String>>() {{
                    add(new Pair<>("Aufnahmedatum", photo.getData().getDatetime().toString().replace("T", " ")));
                    add(new Pair<>("Kamerahersteller", exif.getMake()));
                    add(new Pair<>("Kameramodell", exif.getModel()));
                    add(new Pair<>("Belichtungszeit", exif.getExposure() + " Sek."));
                    add(new Pair<>("Blende", "f/" + exif.getAperture()));
                    add(new Pair<>("Brennweite", "" + exif.getFocalLength()));
                    add(new Pair<>("ISO", "" + exif.getIso()));
                    add(new Pair<>("Blitz", exif.isFlash() ? "wurde ausgelöst" : "wurde nicht ausgelöst"));
                    add(new Pair<>("Höhe", "" + exif.getAltitude()));
                }};

                ObservableList<Pair<String, String>> exifData = FXCollections.observableArrayList(exifList);

                exifName.setCellValueFactory(new PropertyValueFactory<>("Key"));
                exifValue.setCellValueFactory(new PropertyValueFactory<>("Value"));
                exifTable.setItems(exifData);
            } catch (ServiceException e) {
                ErrorDialog.show(root, "Fehler beim Laden der Exif Daten", "Fehlermeldung: " + e.getMessage());
            }
        }
    }

    private void onUpdate() {
        if (updateHandler != null) {
            updateHandler.run();
        }
    }

    private void handleAddToSlideshow(Event event) {
        Slideshow slideshow = slideshowsCombobox.getSelectionModel().getSelectedItem();

        if (slideshow == null) {
            return;
        }

        slideshowView.addPhotosToSlideshow(activePhotos, slideshow);
    }

    private class TagListChangeListener implements ListChangeListener<Tag> {

        public void onChanged(Change<? extends Tag> change) {

            boolean updateNeeded = false;

            while (change.next()) {
                if (change.wasAdded()) {
                    Tag added = change.getAddedSubList().get(0);
                    try {
                        tagService.addTagToPhotos(activePhotos, added);
                        updateNeeded = true;
                    } catch (ServiceException ex) {
                        LOGGER.error("failed adding tag", ex);
                        ErrorDialog.show(root, "Speichern fehlgeschlagen", "Die Kategorien für das Foto konnten nicht gespeichert werden.");
                    }
                }
                if (change.wasRemoved()) {
                    Tag removed = change.getRemoved().get(0);
                    try {
                        tagService.removeTagFromPhotos(activePhotos, removed);
                        updateNeeded = true;
                    } catch (ServiceException ex) {
                        LOGGER.error("failed removing tag", ex);
                        ErrorDialog.show(root, "Speichern fehlgeschlagen", "Die Kategorien für das Foto konnten nicht gespeichert werden.");
                    }
                }
            }

            if (updateNeeded)
                onUpdate();
        }
    }

    private static class SlideshowStringConverter extends StringConverter<Slideshow> {
        @Override
        public String toString(Slideshow object) {
            return object.getName();
        }

        @Override
        public Slideshow fromString(String string) {
            return null;
        }
    }
}
