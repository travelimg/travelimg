package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ImageGridTile extends StackPane {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int IMG_SIZE = 200;

    private final ProgressIndicator progress = new ProgressIndicator();
    private final ImageView imageView = new ImageView();
    private final StackPane container = new StackPane();

    private Image image = null;
    private boolean selected = false;
    private Photo photo = null;

    public ImageGridTile() {
        getStyleClass().add("image-tile");

        progress.setMaxWidth(50);
        progress.setMaxHeight(50);
        setMargin(progress, new Insets(16));
        setPadding(new Insets(4));

        imageView.setFitWidth(IMG_SIZE);
        imageView.setFitHeight(IMG_SIZE);
        imageView.setPreserveRatio(true);

        super.getChildren().add(progress);
        super.getChildren().add(imageView);
        super.getChildren().add(container);
    }

    /**
     * Set the photo this tile represents.
     *
     * @param photo photo this tile represents, or null
     */
    public void setPhoto(Photo photo, Image image) {
        Photo oldPhoto = getPhoto();

        this.photo = photo;
        this.image = image;
        if (photo == null) {
            indicateLoading();
            return;
        }

        // Only reload image if necessary.
        if (oldPhoto == null || !photo.getPath().equals(oldPhoto.getPath())) {
            loadImage(image);
        }
    }

    /**
     * @return photo this tile represents, or null
     */
    public Photo getPhoto() {
        return photo;
    }

    /**
     * @return modifiable list of children.
     */
    @Override public ObservableList<Node> getChildren() {
        return container.getChildren();
    }

    /**
     * Marks this tile as selected. Has no effect if this tile is already selected.
     * Adds the 'selected' style class.
     */
    public void select() {
        if (selected) return;
        getStyleClass().add("selected");
        selected = true;
    }

    /**
     * Marks this tile as unselected. Has no effect if this tile is already unselected.
     * Removes the 'selected' style class.
     */
    public void deselect() {
        if (!selected) return;
        getStyleClass().remove("selected");
        selected = false;
    }

    /**
     * Get a value indicating that the tile is selected.
     *
     * @return true if it is selected, else false
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Cancels a current image loading operation.
     */
    public void cancel() {
        if (image == null) return;
        image.cancel();
    }

    /**
     * Display an image in the tile
     *
     * @param image The image to be loaded
     */
    protected void loadImage(Image image) {
        indicateLoading();

        imageView.setImage(image);
        image.progressProperty().addListener((observable, oldValue, newValue) -> {
                // Image is fully loaded.
                if (newValue.doubleValue() == 1.0) {
                    indicateLoaded();
                }
        });

        if(image.getProgress() == 1.0)
            indicateLoaded();
    }

    private void indicateLoading() {
        getStyleClass().add("loading");
        progress.setVisible(true);
        imageView.setVisible(false);
        container.setVisible(false);
    }

    private void indicateLoaded() {
        getStyleClass().remove("loading");
        progress.setVisible(false);
        imageView.setVisible(true);
        container.setVisible(true);
    }
}
