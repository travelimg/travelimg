package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.gui.control.SmartImage;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;

public class PhotoSlideTile extends SlideTileBase<PhotoSlide> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final SmartImage imageView = new SmartImage(ImageSize.MEDIUM);

    public PhotoSlideTile(PhotoSlide slide) {
        super(slide);
        getStyleClass().add("photo");
        getChildren().add(0, imageView);

        setMaxHeight(ImageSize.inPixels(ImageSize.MEDIUM));
        setMaxWidth(ImageSize.inPixels(ImageSize.MEDIUM));
        imageView.setImage(slide.getPhoto().getFile());
    }
}
