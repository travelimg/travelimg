package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.gui.control.RatingPicker;
import at.ac.tuwien.qse.sepm.gui.control.SmartImage;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class FullscreenWindow extends StackPane {

    private static final Logger LOGGER = LogManager.getLogger();

    private Stage stage;
    private Scene scene;

    @FXML
    private StackPane root;
    @FXML
    private Button bt_previous, bt_next;
    @FXML
    private RatingPicker ratingPicker;
    @FXML
    private VBox vBoxTop;
    @FXML
    private Button hideButton;
    @FXML
    private Node menu;

    private PhotoService photoService;
    private List<Photo> photos;
    private SmartImage image = new SmartImage(ImageSize.ORIGINAL);

    private int activeIndex = 0;

    public FullscreenWindow(PhotoService photoService) {
        FXMLLoadHelper.load(this, this, FullscreenWindow.class, "view/FullScreenDialog.fxml");

        this.photoService = photoService;
    }

    @FXML
    private void initialize() {
        this.stage = new Stage();
        this.scene = new Scene(this);

        stage.setScene(scene);

        image.setPreserveRatio(true);
        getChildren().add(0, image);

        hideButton.setOnAction((e) -> menu.setOpacity(0.0));
        menu.setOnMouseEntered(e -> menu.setOpacity(1.0));

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
                if (keyEvent.getCode() == KeyCode.DIGIT1) {
                    ratingPicker.setRating(Rating.BAD);
                }
                if (keyEvent.getCode() == KeyCode.DIGIT2) {
                    ratingPicker.setRating(Rating.NEUTRAL);
                }
                if (keyEvent.getCode() == KeyCode.DIGIT3) {
                    ratingPicker.setRating(Rating.GOOD);
                }
            }
        });

        ratingPicker.setRatingChangeHandler(this::handleRatingChange);
    }



    public void present(List<Photo> photos, Photo initial) {
        this.photos = photos;

        activeIndex = photos.indexOf(initial);

        if (activeIndex < 0) {
            activeIndex = 0;
        }

        loadImage();

        stage.setFullScreen(true);
        stage.show();
    }

    private void handleRatingChange(Rating newRating) {
        LOGGER.debug("rating picker changed to {}", newRating);

        if (photos.get(activeIndex).getData().getRating() == newRating) {
            LOGGER.debug("photo already has rating of {}", newRating);

        } else {
            Rating oldRating = photos.get(activeIndex).getData().getRating();
            LOGGER.debug("setting photo rating from {} to {}", oldRating, newRating);
            photos.get(activeIndex).getData().setRating(newRating);

            try {
                photoService.editPhoto(photos.get(activeIndex));
            } catch (ServiceException ex) {
                LOGGER.error("Failed saving photo rating.", ex);
                LOGGER.debug("Resetting rating from {} to {}.", newRating, oldRating);

                // Undo changes.
                photos.get(activeIndex).getData().setRating(oldRating);
            }
        }

    }

    @FXML
    private void bt_nextPressed(ActionEvent event) {
        LOGGER.info("Button next pressed!");

        activeIndex++;
        activeIndex = activeIndex % photos.size();

        loadImage();
    }

    @FXML
    private void bt_previousPressed(ActionEvent event) {
        LOGGER.info("Button previous pressed!");

        activeIndex--;
        if (activeIndex < 0)
            activeIndex += photos.size();
        activeIndex = activeIndex % photos.size();

        loadImage();
    }

    private void loadImage() {

        if (photos.size() == 0 || photos.size() <= activeIndex) {
            // out of bounds
            return;
        }

        Photo photo = photos.get(activeIndex);
        ratingPicker.setRating(photo.getData().getRating());
        image.setImage(photo.getFile());

        // handling of images in original size can consume a lot of memory so collect it here
        System.gc();
    }

    public void setElementsVisible(boolean visible){
        vBoxTop.setVisible(visible);
        ratingPicker.setVisible(visible);
    }
}
