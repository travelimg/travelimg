package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Rating;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RatingPicker extends HBox {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML private ToggleGroup toggleGroup;
    @FXML private Toggle badButton;
    @FXML private Toggle neutralButton;
    @FXML private Toggle goodButton;

    public RatingPicker() {
        LOGGER.debug("RatingPicker()");
        FXMLLoadHelper.load(this, this, RatingPicker.class, "view/RatingPicker.fxml");
        toggleGroup.selectedToggleProperty().addListener(this::handleSelect);
    }

    public final Rating getRating() {
        return ratingProperty().get();
    }
    public final ObjectProperty<Rating> ratingProperty() {
        return rating;
    }
    public final void setRating(Rating rating) {
        LOGGER.debug("setRating({})", rating);

        if (getRating() == rating) {
            LOGGER.debug("Rating {} already set.", rating);
            return;
        }

        ratingProperty().set(rating);
        toggleGroup.selectToggle(null);
        switch (rating) {
            case BAD:
                toggleGroup.selectToggle(badButton);
                break;
            case NEUTRAL:
                toggleGroup.selectToggle(neutralButton);
                break;
            case GOOD:
                toggleGroup.selectToggle(goodButton);
                break;
        }
    }
    private final ObjectProperty<Rating> rating =
            new SimpleObjectProperty<>(RatingPicker.this, "rating", Rating.NONE);

    private void handleSelect(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        LOGGER.debug("handleSelect(~)", oldValue, newValue);

        if (newValue == badButton) {
            setRating(Rating.BAD);
        } else if (newValue == neutralButton) {
            setRating(Rating.NEUTRAL);
        } else if (newValue == goodButton) {
            setRating(Rating.GOOD);
        }
    }
}
