package at.ac.tuwien.qse.sepm.gui.slide;

import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;

public class PhotoSlideView extends SlideView {

    private static final Logger LOGGER = LogManager.getLogger();

    private final PhotoSlide slide;
    private final ImageView imageView = new ImageView();

    public PhotoSlideView(PhotoSlide slide, int width, int height) {
        this.slide = slide;

        Image image = loadImage();

        if (image != null) {
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(height);
            imageView.setFitWidth(width);
            imageView.setImage(loadImage());
            getChildren().add(imageView);
        }

        Node overlay = createCaptionBox(slide.getCaption());
        StackPane.setAlignment(overlay, Pos.BOTTOM_CENTER);
        getChildren().add(overlay);
    }

    private Image loadImage() {
        String url;
        try {
            url = slide.getPhoto().getFile().toUri().toURL().toString();
        } catch (MalformedURLException ex) {
            LOGGER.error("Failed to convert photo path to URL", ex);
            return null;
        }

        return new Image(url, true);
    }
}
