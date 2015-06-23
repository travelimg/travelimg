package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.gui.FullscreenWindow;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private final FontAwesomeIconView placeHolder = new FontAwesomeIconView(FontAwesomeIcon.CAMERA);

    private ObservableList<Photo> photos = FXCollections.observableArrayList();
    private ListProperty<Photo> photosProperty = new SimpleListProperty<>(photos);

    public ImageTile() {
        setAlignment(overlay, Pos.CENTER);
        getStyleClass().add("imageTile");
        overlay.getStyleClass().add("overlay");
        taggingIndicator.getStyleClass().add("tagging");
        overlay.setCenter(taggingIndicator);

        placeHolder.setGlyphSize(ImageSize.LARGE.pixels() * 0.6);

        getChildren().add(placeHolder);
        getChildren().add(imageView);
        getChildren().add(overlay);

        setOnMouseEntered((event) -> setCursor(Cursor.HAND));
        setOnMouseExited((event -> setCursor(Cursor.DEFAULT)));

        imageView.visibleProperty().bind(photosProperty.emptyProperty().not());
        overlay.visibleProperty().bind(photosProperty.emptyProperty().not());
        placeHolder.visibleProperty().bind(photosProperty.emptyProperty());
    }

    public void setPhotos(List<Photo> photos) {
        this.photos.clear();
        this.photos.addAll(photos);

        if (photos.size() > 0) {
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
        FontAwesomeIconView taggingIndicatorIcon = new FontAwesomeIconView();
        taggingIndicatorIcon.setGlyphName("TAGS");
        taggingIndicator.setGraphic(taggingIndicatorIcon);
        taggingIndicator.setText(tag.getName());
    }

    public void setGood() {
        FontAwesomeIconView taggingIndicatorIcon = new FontAwesomeIconView();
        taggingIndicatorIcon.setGlyphName("HEART");
        taggingIndicator.setGraphic(taggingIndicatorIcon);
        taggingIndicator.setText("Favorites");
    }

    public void clearImageTile() {
        photos.clear();
    }
}
