package at.ac.tuwien.qse.sepm.gui.controller.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.gui.control.*;
import at.ac.tuwien.qse.sepm.gui.controller.SlideshowView;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.util.LatLong;
import at.ac.tuwien.qse.sepm.service.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PhotoInspectorImpl extends InspectorImpl<Photo> {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private InspectorPane root;

    @FXML
    private TagPicker tagPicker;

    @FXML
    private SuggestionField tagField;

    @FXML
    private SuggestionField photographerField;

    @FXML
    private RatingPicker ratingPicker;

    @FXML
    private VBox exifList;

    @FXML
    private ComboBox<Slideshow> slideshowsCombobox;

    @FXML
    private Button addToSlideshowButton;

    @FXML
    private GoogleMapScene mapScene;

    @Autowired
    private SlideshowView slideshowView;

    @Autowired
    private PhotoService photoservice;

    @Autowired
    private ExifService exifService;

    @Override public void setEntities(Collection<Photo> photos) {
        super.setEntities(photos);

        getEntities().forEach(photo -> mapScene.addMarker(new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude())));

        update(getEntities());
    }

    @FXML
    private void initialize() {
        addToSlideshowButton.setOnAction(this::handleAddToSlideshow);
        slideshowsCombobox.setConverter(new SlideshowStringConverter());
        slideshowsCombobox.setItems(slideshowView.getSlideshows());

        ratingPicker.setRatingChangeHandler(r -> {
            getEntities().forEach(this::saveRating);
            onUpdate();
        });

        tagPicker.setOnUpdate(() -> {
            getEntities().forEach(this::saveTags);
            onUpdate();
        });

        tagField.setOnAction(() -> {
            String value = tagField.getText();
            if (value == null || value.isEmpty()) return;
            for (Photo photo : getEntities()) {
                tagField.setText(null);
                photo.getData().getTags().add(new Tag(null, value));
                savePhoto(photo);
            }
            onUpdate();
        });

        photographerField.setOnAction(() -> {
            String value = photographerField.getText();
            if (value == null || value.isEmpty()) return;
            for (Photo photo : getEntities()) {
                photographerField.setText(null);
                photo.getData().setPhotographer(new Photographer(null, value));
                savePhoto(photo);
            }
            onUpdate();
        });
    }

    private void handleAddToSlideshow(Event event) {
        Slideshow slideshow = slideshowsCombobox.getSelectionModel().getSelectedItem();

        if (slideshow == null) {
            return;
        }

        slideshowView.addPhotosToSlideshow(new ArrayList<>(getEntities()), slideshow);
    }

    private void saveRating(Photo photo) {
        Rating rating = ratingPicker.getRating();
        if (photo.getData().getRating() == rating) {
            LOGGER.debug("photo already has rating of {}", rating);
            return;
        }

        Rating oldRating = photo.getData().getRating();
        LOGGER.debug("setting photo rating from {} to {}", oldRating, rating);
        photo.getData().setRating(rating);
        savePhoto(photo);
    }
    private void saveTags(Photo photo) {
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
        savePhoto(photo);
    }
    private void savePhoto(Photo photo) {
        try {
            photoservice.editPhoto(photo);
        } catch (ServiceException ex) {
            LOGGER.warn("failed updating photo", photo);
            LOGGER.error("", ex);
        }
    }

    private void update(Collection<Photo> photos) {
        if (photos.size() == 0) return;
        updateRatingPicker(photos);
        updateMap(photos);
        updateTagPicker(photos);
        updateExifTable(photos);
    }
    private void updateRatingPicker(Collection<Photo> photos) {
        ratingPicker.setRating(null);
        List<Rating> ratings = photos.stream()
                .map(photo -> photo.getData().getRating())
                .distinct()
                .collect(Collectors.toList());
        if (ratings.size() == 1) {
            ratingPicker.setRating(ratings.get(0));
        }
    }
    private void updateMap(Collection<Photo> photos) {
        mapScene.clear();
        photos.forEach((photo) -> mapScene.addMarker(
                new LatLong(photo.getData().getLatitude(), photo.getData().getLongitude())));
        mapScene.fitToMarkers();
    }
    private void updateTagPicker(Collection<Photo> photos) {
        tagPicker.getEntities().clear();
        photos.forEach((photo) -> {
            Set<String> tags = photo.getData().getTags().stream().map(Tag::getName)
                    .collect(Collectors.toSet());
            tagPicker.getEntities().add(tags);
        });
    }
    private void updateExifTable(Collection<Photo> photos) {

        // Fetch EXIF data for all photos.
        Collection<Exif> exifs = new ArrayList<>(photos.size());
        for (Photo photo : photos) {
            try {
                exifs.add(exifService.getExif(photo));
            } catch (ServiceException ex) {
                LOGGER.warn("failed loading EXIF data for photo {}", photo);
                LOGGER.error("", ex);
            }
        }

        exifList.getChildren().clear();
        addExifCell("Fotograf", photos, p -> {
            Photographer photographer = p.getData().getPhotographer();
            if (photographer == null)
                return null;
            return photographer.getName();
        });
        addExifCell("Datum", photos, p -> {
            LocalDateTime date = p.getData().getDatetime();
            if (date == null)
                return null;
            return date.toString().replace("T", " ");
        });
        addExifCell("Kamerahersteller", exifs, Exif::getMake);
        addExifCell("Kameramodell", exifs, Exif::getModel);
        addExifCell("Belichtungszeit", exifs, e -> e.getExposure() + " Sekunden");
        addExifCell("Blende", exifs, Exif::getAperture);
        addExifCell("Brennweite", exifs, Exif::getFocalLength);
        addExifCell("ISO", exifs, Exif::getIso);
        addExifCell("Blitz", exifs, e -> e.isFlash() ? "ausgelöst" : "ohne");
        addExifCell("Höhe", exifs, Exif::getAltitude);
    }
    private <T> void addExifCell(String key, Collection<T> entities, Function<T, Object> mapper) {
        Set<String> values = new HashSet<>();
        entities.forEach(t -> {
            String value = null;
            Object obj = mapper.apply(t);
            if (obj != null) {
                value = obj.toString();
            }
            values.add(value);
        });
        boolean indetermined = values.size() != 1;

        KeyValueCell cell = new KeyValueCell();
        cell.setKey(key);
        cell.setIndetermined(indetermined);
        if (!indetermined) {
            cell.setValue(values.iterator().next());
        }
        exifList.getChildren().add(cell);
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
