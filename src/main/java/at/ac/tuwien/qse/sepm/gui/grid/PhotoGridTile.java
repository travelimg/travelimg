package at.ac.tuwien.qse.sepm.gui.grid;

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

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.entities.Tag;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PhotoGridTile extends ImageGridTile {

    private static final Logger LOGGER = LogManager.getLogger();

    private final BorderPane overlay = new BorderPane();
    private final Label ratingIndicator = new Label();
    private final FontAwesomeIconView ratingIndicatorIcon = new FontAwesomeIconView();
    private final Tooltip ratingIndicatorTooltip = new Tooltip();
    private final Label taggingIndicator = new Label();
    private final Tooltip taggingIndicatorTooltip = new Tooltip();
    private final Label dateLabel = new Label();

    public PhotoGridTile() {
        getStyleClass().add("photo-tile");

        overlay.getStyleClass().add("overlay");
        setAlignment(overlay, Pos.BOTTOM_CENTER);

        ratingIndicator.getStyleClass().add("rating");
        ratingIndicator.setGraphic(ratingIndicatorIcon);
        ratingIndicator.setTooltip(ratingIndicatorTooltip);

        taggingIndicator.getStyleClass().add("tagging");
        FontAwesomeIconView taggingIndicatorIcon = new FontAwesomeIconView();
        taggingIndicatorIcon.setGlyphName("TAGS");
        taggingIndicator.setGraphic(taggingIndicatorIcon);
        taggingIndicator.setTooltip(taggingIndicatorTooltip);

        dateLabel.getStyleClass().add("date");

        getChildren().add(overlay);
        overlay.setLeft(taggingIndicator);
        overlay.setCenter(dateLabel);
        overlay.setRight(ratingIndicator);
    }

    @Override
    public void setPhoto(Photo photo) {
        super.setPhoto(photo);
        if (photo == null) return;

        showRating(photo.getData().getRating());
        showTags(photo.getData().getTags());
        showDate(photo.getData().getDatetime());
    }

    private void showRating(Rating rating) {
        if (rating == Rating.NONE) {
            ratingIndicator.setVisible(false);
            return;
        }
        ratingIndicator.setVisible(true);
        String text = "";
        String glyph = "";
        switch (rating) {
            case GOOD:
                glyph = "HEART";
                text = "Gutes Foto";
                break;
            case NEUTRAL:
                glyph = "CHECK";
                text = "Neutrales Foto";
                break;
            case BAD:
                glyph = "THUMBS_DOWN";
                text = "Schlechtes Foto";
                break;
        }
        ratingIndicatorIcon.setGlyphName(glyph);
        ratingIndicatorTooltip.setText(text);
    }

    private void showTags(Collection<Tag> tags) {
        if (tags.isEmpty()) {
            taggingIndicator.setVisible(false);
            return;
        }
        taggingIndicator.setVisible(true);
        List<String> stringTags = tags.stream()
                .map(Tag::getName).collect(Collectors.toList());
        String tagString = String.join(", ", stringTags);
        taggingIndicatorTooltip.setText(tagString);
    }

    private void showDate(LocalDateTime date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String text = date.format(format);
        dateLabel.setText(text);
    }
}
