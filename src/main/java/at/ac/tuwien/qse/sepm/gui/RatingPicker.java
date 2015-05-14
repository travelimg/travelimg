package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Rating;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class RatingPicker extends HBox {

    @FXML private ToggleGroup toggleGroup;
    @FXML private Toggle dislikeButton;
    @FXML private Toggle rateButton;
    @FXML private Toggle likeButton;

    public RatingPicker() {
        FXMLLoadHelper.load(this, this, RatingPicker.class, "view/RatingPicker.fxml");
        toggleGroup
                .selectedToggleProperty()
                .addListener(this::handleSelect);
    }

    public final Rating getRating() {
        return ratingProperty().get();
    }
    public final ObjectProperty<Rating> ratingProperty() {
        return rating;
    }
    public final void setRating(Rating rating) {
        ratingProperty().set(rating);
        switch (rating) {
            case HIDDEN:
                toggleGroup.selectToggle(dislikeButton);
                break;
            case NEUTRAL:
                toggleGroup.selectToggle(rateButton);
                break;
            case FAVORITE:
                toggleGroup.selectToggle(likeButton);
                break;
            default:
                toggleGroup.selectToggle(null);
                break;
        }
    }
    private final ObjectProperty<Rating> rating =
            new SimpleObjectProperty<>(RatingPicker.this, "rating", Rating.NONE);

    private void handleSelect(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        if (newValue == dislikeButton) {
            setRating(Rating.HIDDEN);
        } else if (newValue == rateButton) {
            setRating(Rating.NEUTRAL);
        } else if (newValue == likeButton) {
            setRating(Rating.FAVORITE);
        } else {
            setRating(Rating.NONE);
        }
    }
}
