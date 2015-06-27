package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.gui.FullscreenWindow;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

public class ImageTile extends StackPane {

    private final SmartImage imageView = new SmartImage(ImageSize.LARGE);

    private final Label name = new Label();
    private final FontAwesomeIconView overLayIcon = new FontAwesomeIconView();
    private final FontAwesomeIconView placeHolder = new FontAwesomeIconView(FontAwesomeIcon.CAMERA);
    @Autowired
    private PhotoService photoService;
    private ObservableList<Photo> photos = FXCollections.observableArrayList();
    private ListProperty<Photo> photosProperty = new SimpleListProperty<>(photos);

    public ImageTile() {
        getStyleClass().add("imageTile");

        Group overlay = new Group();
        HBox overlayBox = new HBox();
        overlayBox.getStyleClass().add("hoverlay");
        overlayBox.getChildren().addAll(overLayIcon, name);
        overlay.getChildren().add(overlayBox);

        StackPane.setAlignment(overlay, Pos.CENTER);

        placeHolder.setGlyphSize(ImageSize.LARGE.pixels() * 0.6);

        setMinHeight(0);
        setMinWidth(0);
        imageView.setPreserveRatio(false);

        getChildren().add(placeHolder);
        getChildren().add(imageView);
        getChildren().add(overlay);

        setAlignment(overlay, Pos.CENTER);

        setOnMouseEntered((event) -> {
            if (photos.size() > 0) {
                setCursor(Cursor.HAND);
            }
        });
        
        setOnMouseExited((event -> setCursor(Cursor.DEFAULT)));

        imageView.visibleProperty().bind(photosProperty.emptyProperty().not());
        overlay.visibleProperty().bind(photosProperty.emptyProperty().not().and(name.textProperty().isNotEmpty()));
        placeHolder.visibleProperty().bind(photosProperty.emptyProperty());

        heightProperty().addListener(this::handleSizeChange);
        widthProperty().addListener(this::handleSizeChange);
    }

    public void setPhotos(List<Photo> ps) {
        handleSizeChange(null);
        this.photos.clear();
        this.photos.addAll(ps);

        if (photos.size() > 0) {
            Random rand = new Random();
            int randomPos = 0;
            if (photos.size() > 1) {
                randomPos = rand.nextInt(photos.size() - 1);
            }

            imageView.setImage(photos.get(randomPos).getFile());

            setOnMouseClicked((event) -> {
                if (photos.size() > 0) {
                    FullscreenWindow fw = new FullscreenWindow(photoService);
                    fw.present(this.photos, this.photos.get(0));
                }
            });
        }
    }

    public void setTag(Tag tag) {
        name.setText(tag.getName());
        overLayIcon.setGlyphName("TAGS");
        placeHolder.setGlyphName("TAGS");
        overLayIcon.setGlyphSize(30);
    }

    public void setGood() {
        name.setText("Favorites");
        overLayIcon.setGlyphName("HEART");
        placeHolder.setGlyphName("HEART");
        overLayIcon.setGlyphSize(30);
    }

    public void clearImageTile() {
        photos.clear();
        name.setText("");
        setOnMouseClicked(null);
    }

    private void handleSizeChange(Object observable) {
        // fit image to available size
        imageView.fitToSize(getWidth(), getHeight());
        placeHolder.setGlyphSize(Math.min(getWidth(), getHeight()) * 0.6);
    }
}
