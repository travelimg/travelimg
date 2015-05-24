package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.entities.Tag;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    @FXML private ProgressIndicator progress;
    @FXML private Label rating;
    @FXML private FontAwesomeIconView ratingIcon;
    @FXML private Tooltip ratingTooltip;
    @FXML private Label tag;
    @FXML private Tooltip tagTooltip;
    @FXML private ImageView imageView;
    @FXML private Label dateLabel;

    private Image image = null;
    private boolean selected = false;

    public PhotoTile() {
        FXMLLoadHelper.load(this, this, getClass(), "view/PhotoTile.fxml");
        getStyleClass().add("loading");
    }

    public void setPhoto(Photo photo) {
        FXMLLoadHelper.load(this, this, getClass(), "view/PhotoTile.fxml");
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
            this.rating.setVisible(false);
            return;
        }
        this.rating.setVisible(true);
        switch (rating) {
            case GOOD:
                ratingIcon.setGlyphName("HEART");
                ratingTooltip.setText("Gutes Foto");
                break;
            case NEUTRAL:
                ratingIcon.setGlyphName("CHECK");
                ratingTooltip.setText("Neutrales Foto");
                break;
            case BAD:
                ratingIcon.setGlyphName("THUMBS_DOWN");
                ratingTooltip.setText("Schlechtes Foto");
                break;
        }
    }

    private void showTags(List<Tag> tags) {
        if (tags.isEmpty()) {
            tag.setVisible(false);
            return;
        }
        tag.setVisible(true);
        List<String> stringTags = tags.stream()
                .map(Tag::getName).collect(Collectors.toList());
        String tagString = String.join(", ", stringTags);
        tagTooltip.setText(tagString);
    }

    private void showDate(LocalDateTime date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String text = date.format(format);
        dateLabel.setText(text);
    }
}
