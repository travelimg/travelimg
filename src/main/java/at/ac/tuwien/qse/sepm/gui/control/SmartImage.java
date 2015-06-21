package at.ac.tuwien.qse.sepm.gui.control;

import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class SmartImage extends StackPane {

    private final ProgressIndicator progress = new ProgressIndicator();
    private final ImageView imageView = new ImageView();

    private Image image = null;

    public SmartImage() {
        getStyleClass().add("smart-image");

        setMargin(progress, new Insets(16));
        setPadding(new Insets(4));

        heightProperty().addListener(this::handleSizeChange);
        widthProperty().addListener(this::handleSizeChange);

        getChildren().add(progress);
        getChildren().add(imageView);
    }

    public void setImage(Image image) {
        indicateLoading();

        if (image == null) {
            return;
        }

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
