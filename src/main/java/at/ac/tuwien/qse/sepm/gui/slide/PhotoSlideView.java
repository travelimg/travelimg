package at.ac.tuwien.qse.sepm.gui.slide;

import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class PhotoSlideView extends SlideView {

    private static final Logger LOGGER = LogManager.getLogger();

    private final PhotoSlide slide;
    private final ImageView imageView = new ImageView();

    public PhotoSlideView(PhotoSlide slide, ImageCache imageCache) {
        this.slide = slide;

        Image image = loadImage();

        if (image != null) {
            imageView.setImage(loadImage());
            getChildren().add(imageView);
        }
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
