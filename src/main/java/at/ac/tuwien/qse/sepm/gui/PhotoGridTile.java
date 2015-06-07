package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PhotoGridTile extends ImageGridTile<Photo> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final BorderPane overlay = new BorderPane();
    private final Label ratingIndicator = new Label();
    private final FontAwesomeIconView ratingIndicatorIcon = new FontAwesomeIconView();
    private final Tooltip ratingIndicatorTooltip = new Tooltip();
    private final Label taggingIndicator = new Label();
    private final Tooltip taggingIndicatorTooltip = new Tooltip();
    private final Label dateLabel = new Label();

    private Image image = null;
    private boolean selected = false;

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

    @Override public void setItem(Photo photo) {
        Photo oldPhoto = getItem();
        super.setItem(photo);
        if (photo == null) return;

        // Only reload image if necessary.
        if (oldPhoto == null || !photo.getPath().equals(oldPhoto.getPath())) {
            showImage(photo.getPath());
        }
        showRating(photo.getRating());
        showTags(photo.getTags());
        showDate(photo.getDatetime());
    }

    private void showImage(String path) {
        File file = new File(path);
        String url = null;
        try {
            url = file.toURI().toURL().toString();
        } catch (MalformedURLException ex) {
            LOGGER.error("Photo URL is malformed. Skipping loading of image", ex);
            return;
        }
        loadImage(url);
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

    private void showTags(List<Tag> tags) {
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
