package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.*;
import at.ac.tuwien.qse.sepm.gui.controller.SlideshowView;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import at.ac.tuwien.qse.sepm.service.*;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class PhotoInspectorImpl extends InspectorImpl<Photo> {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private InspectorPane root;

    @FXML
    private TagPicker tagPicker;

    @FXML
    private RatingPicker ratingPicker;


    /*@FXML
    private TableColumn<String, String> exifName;

    @FXML
    private TableColumn<String, String> exifValue;*/

    @FXML
    private ComboBox<Slideshow> slideshowsCombobox;

    @FXML
    private Button addToSlideshowButton;

    @FXML
    private GoogleMapScene mapScene;

    private Runnable updateHandler;

    @Autowired
    private SlideshowView slideshowView;

    @Autowired
    private PhotoService photoservice;

    @Autowired
    private ExifService exifService;

    @Override public void setEntities(Collection<Photo> photos) {
        super.setEntities(photos);

        getEntities().forEach(photo -> mapScene.addMarker(new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude())));

        showDetails(getEntities());
    }

    @FXML
    private void initialize() {
        ratingPicker.setRatingChangeHandler(this::handleRatingChange);
        addToSlideshowButton.setOnAction(this::handleAddToSlideshow);
        slideshowsCombobox.setConverter(new SlideshowStringConverter());
        slideshowsCombobox.setItems(slideshowView.getSlideshows());

        tagPicker.setOnUpdate(() -> getEntities().forEach(this::updateTags));
    }

    private void handleRatingChange(Rating newRating) {
        LOGGER.debug("rating picker changed to {}", newRating);

        Collection<Photo> photos = getEntities();
        if (photos.size() == 0) {
            LOGGER.debug("No photo selected.");
            return;
        }

        for (Photo photo : photos) {

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

    private void showDetails(Collection<Photo> photos) {

        // Depending on the number of photos selected show different elements.
        boolean noneActive = photos.size() == 0;
        boolean singleActive = photos.size() == 1;
        /*exifTable.setVisible(singleActive);
        exifTable.setManaged(singleActive);*/

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
        mapScene.clear();
        photos.forEach((photo) -> mapScene.addMarker(new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude())));
        mapScene.fitToMarkers();

        // Add tags to the tag picker.
        tagPicker.getEntities().clear();
        photos.forEach((photo) -> {
            Set<String> tags = photo.getData().getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet());
            tagPicker.getEntities().add(tags);
        });

        // Show additional details for a single selected photo.
        if (singleActive) {
            Photo photo = photos.iterator().next();

            /*try {
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

                /*exifName.setCellValueFactory(new PropertyValueFactory<>("Key"));
                exifValue.setCellValueFactory(new PropertyValueFactory<>("Value"));*
                //exifTable.setItems(exifData);
            } catch (ServiceException e) {
                ErrorDialog.show(root, "Fehler beim Laden der Exif Daten", "Fehlermeldung: " + e.getMessage());
            }*/
        }
    }

    private void handleAddToSlideshow(Event event) {
        Slideshow slideshow = slideshowsCombobox.getSelectionModel().getSelectedItem();

        if (slideshow == null) {
            return;
        }

        slideshowView.addPhotosToSlideshow(new ArrayList<>(getEntities()), slideshow);
    }

    private void updateTags(Photo photo) {
        LOGGER.debug("updating tags of {}", photo);

        // Find the new tags.
        Set<String> oldTags = photo.getData().getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        Set<String> newTags = tagPicker.filter(oldTags);
        LOGGER.debug(tagPicker.getTags());
        LOGGER.debug("old tags {} will be updated to new tags {}", oldTags, newTags);
        if (oldTags.equals(newTags)) {
            LOGGER.debug("tags have not changed of photo {}", photo);
            return;
        }

        // Replace tags with new ones.
        photo.getData().getTags().clear();
        newTags.forEach(tag -> photo.getData().getTags().add(new Tag(null, tag)));

        try {
            photoservice.editPhoto(photo);
        } catch (ServiceException ex) {
            LOGGER.warn("failed updating photo", photo);
            LOGGER.error("error while updating photo", ex);
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
