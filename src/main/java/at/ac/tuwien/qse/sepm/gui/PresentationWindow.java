package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.slide.SlideView;
import at.ac.tuwien.qse.sepm.gui.util.ColorUtils;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class PresentationWindow extends StackPane {

    private static final Logger logger = LogManager.getLogger();

    private Stage stage;
    private Scene scene;

    private int activeIndex = 0;
    private Timeline timeline = null;

    private Slideshow slideshow;

    public PresentationWindow(Slideshow slideshow) {
        FXMLLoadHelper.load(this, this, PresentationWindow.class, "view/PresentationWindow.fxml");

        getStyleClass().add("fullscreen");

        this.slideshow = slideshow;
    }

    @FXML
    private void initialize() {
        this.stage = new Stage();
        this.scene = new Scene(this);

        stage.setScene(scene);

        // TODO: replace by css
        Background background = new Background(new BackgroundFill(Paint.valueOf("black"), null, null));
        setBackground(background);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(final KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.RIGHT) {
                    showNextSlide(null);
                }
                if (keyEvent.getCode() == KeyCode.LEFT) {
                    logger.debug("Button pressed");
                    showPrevSlide(null);


                }
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    close();
                }
            }
        });
    }

    public void present() {
        loadSlide();

        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();

        timeline = new Timeline(new KeyFrame(Duration.seconds(slideshow.getDurationBetweenPhotos()), this::showNextSlide));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void close() {
        if (timeline != null) {
            timeline.stop();
        }

        stage.close();
    }

    private void showPrevSlide(ActionEvent event) {
        activeIndex = Math.max(0, activeIndex - 1);
        loadSlide();
    }

    private void showNextSlide(ActionEvent event) {
        if (activeIndex == slideshow.getSlides().size() - 1)
            return;
        activeIndex = Math.min(slideshow.getSlides().size() - 1, activeIndex + 1);
        loadSlide();

    }

    private void loadSlide() {
        List<Slide> slides = slideshow.getSlides();
        if (slides.size() == 0 || slides.size() <= activeIndex) {
            // out of bounds
            return;
        }

        int width = (int)Screen.getPrimary().getVisualBounds().getWidth();
        int height = (int)Screen.getPrimary().getVisualBounds().getHeight();

        SlideView slideView = SlideView.of(slides.get(activeIndex), width, height);

        getChildren().clear();
        getChildren().add(slideView);
        logger.debug(slides.get(activeIndex));
    }
}
