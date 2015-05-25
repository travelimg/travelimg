package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.entities.Tag;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PhotoTile extends StackPane {

    private static final Logger LOGGER = LogManager.getLogger();

    private ProgressIndicator progress;
    private Label ratingIndicator;
    private FontAwesomeIconView ratingIndicatorIcon;
    private Tooltip ratingIndicatorTooltip;
    private Label taggingIndicator;
    private Tooltip taggingIndicatorTooltip;
    private ImageView imageView;
    private Label dateLabel;

    private Image image = null;
    private boolean selected = false;

    public PhotoTile() {
        getStyleClass().add("photo-tile");

        progress = new ProgressIndicator();
        progress.setMaxWidth(50);
        progress.setMaxHeight(50);
        getChildren().add(progress);
        setMargin(progress, new Insets(16));
        setPadding(new Insets(4));

        imageView = new ImageView();
        imageView.setFitWidth(142);
        imageView.setFitHeight(142);
        imageView.setPreserveRatio(true);
        getChildren().add(imageView);

        BorderPane overlay = new BorderPane();
        overlay.getStyleClass().add("overlay");
        setAlignment(overlay, Pos.BOTTOM_CENTER);
        getChildren().add(overlay);

        ratingIndicator = new Label();
        ratingIndicator.getStyleClass().add("rating");
        ratingIndicatorIcon = new FontAwesomeIconView();
        ratingIndicator.setGraphic(ratingIndicatorIcon);
        ratingIndicatorTooltip = new Tooltip();
        ratingIndicator.setTooltip(ratingIndicatorTooltip);
        overlay.setRight(ratingIndicator);

        taggingIndicator = new Label();
        taggingIndicator.getStyleClass().add("tagging");
        FontAwesomeIconView taggingIndicatorIcon = new FontAwesomeIconView();
        taggingIndicatorIcon.setGlyphName("TAGS");
        taggingIndicator.setGraphic(taggingIndicatorIcon);
        taggingIndicatorTooltip = new Tooltip();
        taggingIndicator.setTooltip(taggingIndicatorTooltip);
        overlay.setLeft(taggingIndicator);

        dateLabel = new Label();
        dateLabel.getStyleClass().add("date");
        overlay.setCenter(dateLabel);

        getStyleClass().add("loading");
    }

    public void setPhoto(Photo photo) {
        showImage(photo.getPath());
        showRating(photo.getRating());
        showTags(photo.getTags());
        showDate(photo.getDatetime());
    }

    public void select() {
        if (selected) return;
        getStyleClass().add("selected");
        this.selected = true;
    }

    public void deselect() {
        if (!selected) return;
        getStyleClass().remove("selected");
        this.selected = false;
    }

    public boolean isSelected() {
        return selected;
    }

    public void cancel() {
        if (image == null) return;
        image.cancel();
    }

    private void showImage(String path) {
        File file = new File(path);
        String url = null;
        try {
            url = file.toURI().toURL().toString();
        } catch (MalformedURLException ex) {
            LOGGER.error("photo URL is malformed", ex);
            // TODO: handle exception
        }

        image = new Image(url, imageView.getFitWidth(), imageView.getFitHeight(), false, true, true);
        imageView.setImage(image);

        image.progressProperty().addListener((observable, oldValue, newValue) -> {
            // Image is fully loaded.
            if (newValue.doubleValue() == 1.0) {
                getChildren().remove(progress);
                getStyleClass().remove("loading");
            }
        });
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
