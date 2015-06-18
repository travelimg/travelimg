package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by mb on 14.06.15.
 */
public class PresentationWindow extends AnchorPane{

    private static final Logger logger = LogManager.getLogger();

    private Stage stage;
    private Scene scene;

    @FXML
    private AnchorPane root;
    @FXML
    private ImageView imageView;

    private int activeIndex = 0;

    private Slideshow slideshow;
    private ImageCache imageCache;

    public PresentationWindow(Slideshow slideshow, ImageCache imageCache) {
        FXMLLoadHelper.load(this, this, PresentationWindow.class, "view/PresentationWindow.fxml");

        this.slideshow = slideshow;
        this.imageCache = imageCache;
    }

    @FXML
    private void initialize() {
        this.stage = new Stage();
        this.scene = new Scene(this);

        stage.setScene(scene);
        imageView.fitWidthProperty().bind(root.widthProperty());
        imageView.fitHeightProperty().bind(root.heightProperty());
        root.setOnKeyPressed((keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.RIGHT) {
                showNextSlide(null);
            }
            if (keyEvent.getCode() == KeyCode.LEFT) {
                showPrevSlide(null);
            }
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });
    }

    public void present() {
        loadImage();

        stage.setFullScreen(true);
        stage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), this::showNextSlide));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void showPrevSlide(ActionEvent event) {
        activeIndex = Math.max(0, activeIndex - 1);
        loadImage();
    }

    private void showNextSlide(ActionEvent event) {
        activeIndex = Math.min(slideshow.getSlides().size() - 1, activeIndex + 1);
        loadImage();
    }

    private void loadImage() {
        List<Slide> slides = slideshow.getSlides();
        if (slides.size() == 0 || slides.size() <= activeIndex) {
            // out of bounds
            return;
        }

        Image image = imageCache.get(slides.get(activeIndex).getPhoto(), ImageSize.ORIGINAL);
        imageView.setImage(image);

        // handling of images in original size can consume a lot of memory so collect it here
        System.gc();
    }
}
