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
    @FXML private Toggle hateButton;
    @FXML private Toggle rateButton;
    @FXML private Toggle loveButton;

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
        switch (rating) {
            case BAD:
                toggleGroup.selectToggle(hateButton);
                break;
            case NEUTRAL:
                toggleGroup.selectToggle(rateButton);
                break;
            case GOOD:
                toggleGroup.selectToggle(loveButton);
                break;
        }
    }
    private final ObjectProperty<Rating> rating =
            new SimpleObjectProperty<>(RatingPicker.this, "rating", Rating.NONE);

    private void handleSelect(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        LOGGER.debug("handleSelect(~)", oldValue, newValue);

        if (newValue == hateButton) {
            setRating(Rating.BAD);
        } else if (newValue == rateButton) {
            setRating(Rating.NEUTRAL);
        } else if (newValue == loveButton) {
            setRating(Rating.GOOD);
        }
    }
}
