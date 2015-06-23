package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.gui.FullscreenWindow;
import at.ac.tuwien.qse.sepm.gui.util.ImageCacheImpl;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageTile extends StackPane {

    private static final Logger logger = LogManager.getLogger();
    private final SmartImage imageView = new SmartImage(ImageSize.LARGE);
    private final BorderPane overlay = new BorderPane();
    private final Label taggingIndicator = new Label();
    private List<Photo> photos = new ArrayList<>();

    public ImageTile() {
        setAlignment(overlay, Pos.CENTER);
        getStyleClass().add("imageTile");
        overlay.getStyleClass().add("overlay");
        taggingIndicator.getStyleClass().add("tagging");
        overlay.setCenter(taggingIndicator);
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        if (photos.size() > 0) {
            getChildren().add(imageView);
            Random rand = new Random();
            int randomPos = 0;
            if (photos.size() > 1) {
                randomPos = rand.nextInt(photos.size() - 1);
            }

            imageView.setImage(photos.get(randomPos).getFile());
            addEvents(photos);
        }
    }

    public void setTag(Tag tag) {
        getChildren().add(overlay);
        FontAwesomeIconView taggingIndicatorIcon = new FontAwesomeIconView();
        taggingIndicatorIcon.setGlyphName("TAGS");
        taggingIndicator.setGraphic(taggingIndicatorIcon);
        taggingIndicator.setText(tag.getName());
        overlay.setVisible(true);
    }

    public void setGood() {
        getChildren().add(overlay);
        FontAwesomeIconView taggingIndicatorIcon = new FontAwesomeIconView();
        taggingIndicatorIcon.setGlyphName("HEART");
        taggingIndicator.setGraphic(taggingIndicatorIcon);
        taggingIndicator.setText("Favorites");
        overlay.setVisible(true);
    }

    private void addEvents(List<Photo> photos) {

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setCursor(Cursor.HAND);
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setCursor(Cursor.DEFAULT);
            }
        });
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FullscreenWindow fw = new FullscreenWindow(new ImageCacheImpl());
                fw.present(photos, photos.get(0));
            }
        });
    }

    public void clearImageTile() {
        setOnMouseEntered(null);
        setOnMouseClicked(null);
        getChildren().clear();
    }

}
