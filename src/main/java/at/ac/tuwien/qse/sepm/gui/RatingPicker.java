package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Rating;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class RatingPicker {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private Node root;
    @FXML
    private Button badButton;
    @FXML
    private Button neutralButton;
    @FXML
    private Button goodButton;

    private Rating rating = null;
    private boolean indetermined = false;
    private Consumer<Rating> ratingChangeHandler;

    @FXML
    private void initialize() {
        badButton.setOnMouseClicked((event) -> handleToggle(badButton, Rating.BAD));
        neutralButton.setOnMouseClicked((event) -> handleToggle(neutralButton, Rating.NEUTRAL));
        goodButton.setOnMouseClicked((event) -> handleToggle(goodButton, Rating.GOOD));
    }

    public void setRatingChangeHandler(Consumer<Rating> ratingChangeHandler) {
        this.ratingChangeHandler = ratingChangeHandler;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        if (this.rating == rating) return;

        badButton.getStyleClass().remove("selected");
        neutralButton.getStyleClass().remove("selected");
        goodButton.getStyleClass().remove("selected");
        root.getStyleClass().remove("indetermined");

        this.rating = rating;
        this.indetermined = rating == null;

        if (rating == null) {
            root.getStyleClass().add("indetermined");
            return;
        }

        switch (rating) {
            case BAD:
                badButton.getStyleClass().add("selected");
                break;
            case NEUTRAL:
                neutralButton.getStyleClass().add("selected");
                break;
            case GOOD:
                goodButton.getStyleClass().add("selected");
                break;
            case NONE:

        }

        onRatingChange();
    }

    private void onRatingChange() {
        if (ratingChangeHandler != null) {
            ratingChangeHandler.accept(getRating());
        }
    }

    private void handleToggle(Button button, Rating rating) {
        if (button.getStyleClass().contains("selected")) {
            setRating(Rating.NONE);
        } else {
            setRating(rating);
        }
    }
}
