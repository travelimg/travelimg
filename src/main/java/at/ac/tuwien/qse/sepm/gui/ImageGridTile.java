package at.ac.tuwien.qse.sepm.gui;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ImageGridTile<E> extends StackPane {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ProgressIndicator progress = new ProgressIndicator();
    private final ImageView imageView = new ImageView();
    private final StackPane container = new StackPane();

    private Image image = null;
    private boolean selected = false;
    private E item = null;

    public ImageGridTile() {
        getStyleClass().add("image-tile");

        progress.setMaxWidth(50);
        progress.setMaxHeight(50);
        setMargin(progress, new Insets(16));
        setPadding(new Insets(4));

        imageView.setFitWidth(142);
        imageView.setFitHeight(142);
        imageView.setPreserveRatio(true);

        super.getChildren().add(progress);
        super.getChildren().add(imageView);
        super.getChildren().add(container);
    }

    /**
     * Set the item this tile represents.
     *
     * @param item item this tile represents, or null
     */
    public void setItem(E item) {
        this.item = item;
        if (item == null) indicateLoading();
    }

    /**
     * @return item this tile represents, or null
     */
    public E getItem() {
        return item;
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
     * Loads an image to be displayed in the tile. The image is loaded in the background, so it does
     * not block the GUI.
     *
     * @param url URL of the image
     */
    protected void loadImage(String url) {
        indicateLoading();
        image = new Image(url, imageView.getFitWidth(), imageView.getFitHeight(), false, true, true);
        imageView.setImage(image);
        image.progressProperty().addListener((observable, oldValue, newValue) -> {
            // Image is fully loaded.
            if (newValue.doubleValue() == 1.0) {
                indicateLoaded();
            }
        });
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