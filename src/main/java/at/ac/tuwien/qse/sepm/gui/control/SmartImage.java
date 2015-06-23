package at.ac.tuwien.qse.sepm.gui.control;

import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.nio.file.Path;

public class SmartImage extends StackPane {

    private static final ImageCache cache = new ImageCache();

    private final ProgressIndicator progress = new ProgressIndicator();
    private final ImageView imageView = new ImageView();

    private Image image = null;
    private ImageSize size;

    public SmartImage(ImageSize size) {
        this.size = size;

        getStyleClass().add("smart-image");

        setMargin(progress, new Insets(16));
        setPadding(new Insets(4));

        heightProperty().addListener(this::handleSizeChange);
        widthProperty().addListener(this::handleSizeChange);

        getChildren().add(progress);
        getChildren().add(imageView);
    }

    public void setImage(Path path) {
        indicateLoading();

        if (path == null) {
            return;
        }

        image = cache.get(path, size);
        imageView.setImage(image);

        // NOTE: Image may be loaded already.
        if (image.getProgress() == 1.0) {
            indicateLoaded();
            return;
        }

        image.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0) {
                indicateLoaded();
            }
        });
    }

    public void setPreserveRatio(boolean preserve) {
        imageView.setPreserveRatio(preserve);
    }

    public void fitToSize(double width, double height) {
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }

    public void cancel() {
        if (image == null) return;
        image.cancel();
    }

    private void indicateLoading() {
        getStyleClass().add("loading");
        progress.setVisible(true);
        imageView.setVisible(false);
    }

    private void indicateLoaded() {
        getStyleClass().removeAll("loading");
        progress.setVisible(false);
        imageView.setVisible(true);
    }

    private void handleSizeChange(Object observable) {
        imageView.setFitWidth(getWidth());
        imageView.setFitHeight(getHeight());
    }
}
