package at.ac.tuwien.qse.sepm.gui.control;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.gui.FXMLLoadHelper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class RatingPicker extends HBox {

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

    public RatingPicker() {
        FXMLLoadHelper.load(this, this, RatingPicker.class, "view/RatingPicker.fxml");
    }

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
