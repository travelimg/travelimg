package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Rating;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.function.Consumer;

public class RatingPicker {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML private Node root;
    @FXML private Toggle badButton;
    @FXML private Toggle neutralButton;
    @FXML private Toggle goodButton;

    private Rating rating = Rating.NONE;
    private boolean indetermined = false;
    private Consumer<Rating> ratingChangeHandler;

    @FXML private void initialize() {
        badButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                setRating(Rating.BAD));
        neutralButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                setRating(Rating.NEUTRAL));
        goodButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                setRating(Rating.GOOD));
    }

    public void setRatingChangeHandler(Consumer<Rating> ratingChangeHandler) {
        this.ratingChangeHandler = ratingChangeHandler;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        if (this.rating == rating) return;

        badButton.setSelected(false);
        neutralButton.setSelected(false);
        goodButton.setSelected(false);
        root.getStyleClass().remove("indetermined");

        this.rating = rating;
        this.indetermined = rating == null;

        if (rating == null) {
            root.getStyleClass().add("indetermined");
            return;
        }

        switch (rating) {
            case BAD:
                badButton.setSelected(true);
                break;
            case NEUTRAL:
                neutralButton.setSelected(true);
                break;
            case GOOD:
                goodButton.setSelected(true);
                break;
        }

        onRatingChange();
    }

    public boolean isIndetermined() {
        return indetermined;
    }

    private void onRatingChange() {
        if (ratingChangeHandler != null) {
            ratingChangeHandler.accept(getRating());
        }
    }
}
