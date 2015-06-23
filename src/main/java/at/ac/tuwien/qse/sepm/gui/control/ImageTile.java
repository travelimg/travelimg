package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.gui.FullscreenWindow;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Random;

public class ImageTile extends StackPane {

    private final SmartImage imageView = new SmartImage(ImageSize.LARGE);
    private final BorderPane overlay = new BorderPane();
    private final Label taggingIndicator = new Label();

    public ImageTile() {
        setAlignment(overlay, Pos.CENTER);
        getStyleClass().add("imageTile");
        overlay.getStyleClass().add("overlay");
        taggingIndicator.getStyleClass().add("tagging");
        overlay.setCenter(taggingIndicator);

        setOnMouseEntered((event) -> setCursor(Cursor.HAND));
        setOnMouseExited((event -> setCursor(Cursor.DEFAULT)));
    }

    public void setPhotos(List<Photo> photos) {
        if (photos.size() > 0) {
            getChildren().add(imageView);
            Random rand = new Random();
            int randomPos = 0;
            if (photos.size() > 1) {
                randomPos = rand.nextInt(photos.size() - 1);
            }

            imageView.setImage(photos.get(randomPos).getFile());

            setOnMouseClicked((event) -> {
                FullscreenWindow fw = new FullscreenWindow();
                fw.present(photos, photos.get(0));
            });

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

    public void clearImageTile() {
        getChildren().clear();
    }
}
