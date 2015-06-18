package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
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
    @FXML
    private Button bt_previous, bt_next;

    private Slideshow slideshow;
    private Image image;

    private int activeIndex = 0;

    private ImageCache imageCache;

    private Integer slideshowCount=0;

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
        root.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(final KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.RIGHT) {
                    bt_nextPressed(null);
                }
                if (keyEvent.getCode() == KeyCode.LEFT) {
                    bt_previousPressed(null);
                }
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
            }
        });

    }

    public void present() {
        activeIndex = 0;

        loadImage();
        //animateImage(photos,initial,duration);

        stage.setFullScreen(true);
        stage.show();
    }

    @FXML
    private void bt_nextPressed(ActionEvent event) {
        logger.info("Button next pressed!");

        activeIndex++;
        //activeIndex = activeIndex % photos.size();

        loadImage();
    }

    @FXML
    private void bt_previousPressed(ActionEvent event) {
        logger.info("Button previous pressed!");

        activeIndex--;
        /*if (activeIndex < 0)
            activeIndex += photos.size();
        activeIndex = activeIndex % photos.size();*/

        loadImage();
    }

    private void loadImage() {
        List<Slide> slides = slideshow.getSlides();
        if (slides.size() == 0 || slides.size() <= activeIndex) {
            // out of bounds
            return;
        }

        image = imageCache.get(slides.get(activeIndex).getPhoto(), ImageSize.ORIGINAL);
        imageView.setImage(image);

        // handling of images in original size can consume a lot of memory so collect it here
        System.gc();
    }
    private void animateImage(List<Photo> photos,Photo initial,Double duration) {
        long time = (new Double(duration).longValue());
        Task task = new Task<Void>() {
            @Override public Void call() throws Exception {
                for (int i = 0; i < photos.size(); i++) {
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            image = imageCache.get(photos.get(slideshowCount), ImageSize.ORIGINAL);
                            imageView.setImage(image);
                            slideshowCount++;
                            if (slideshowCount >= photos.size()) {
                                slideshowCount = 0;
                            }
                        }
                    });

                    Thread.sleep(time);

                }
                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

        //});
    }



}
