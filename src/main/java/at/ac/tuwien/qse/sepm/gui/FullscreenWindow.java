package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.gui.control.RatingPicker;
import at.ac.tuwien.qse.sepm.gui.dialogs.ErrorDialog;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.impl.PhotoServiceImpl;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


public class FullscreenWindow extends AnchorPane {

    private static final Logger logger = LogManager.getLogger();

    private Stage stage;
    private Scene scene;

    @FXML
    private AnchorPane root;
    @FXML
    private ImageView imageView;
    @FXML
    private Button bt_previous, bt_next;
    @Autowired private RatingPicker ratingPicker;
    @Autowired private PhotoService photoService;
    @Autowired private PhotoDAO photoDAO;
    @FXML
    private HBox raitingContainer;
    private List<Photo> photos;
    private Image image;
    private Photo aktivePhoto = new Photo();
    private int activeIndex = 0;

    private ImageCache imageCache;

    public  FullscreenWindow(ImageCache imageCache){
        FXMLLoadHelper.load(this, this, FullscreenWindow.class, "view/FullScreenDialog.fxml");

        this.imageCache = imageCache;
    }

    @FXML
    private void initialize() {

        this.stage = new Stage();
        this.scene = new Scene(this);
        this.bt_next.setOnAction(this::bt_nextPressed);
        this.bt_previous.setOnAction(this::bt_previousPressed);
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
                if(keyEvent.getCode() == KeyCode.DIGIT1){
                    handleRatingChange(Rating.BAD);
                }
            }
        });

        ratingPicker.setRatingChangeHandler(this::handleRatingChange);
        raitingContainer.getChildren().add(ratingPicker);
    }

    private void handleRatingChange(Rating newRating) {
        logger.debug("rating picker changed to {}", newRating);


        aktivePhoto = photos.get(activeIndex);
            if (aktivePhoto.getRating() == newRating) {
                logger.debug("photo already has rating of {}", newRating);

            }else {

                Rating oldRating = aktivePhoto.getRating();
                logger.debug("setting photo rating from {} to {}", oldRating, newRating);
                aktivePhoto.setRating(newRating);

                try {
                    System.out.println(aktivePhoto.toString());
                    photoService.editPhoto(aktivePhoto);

                } catch (ServiceException ex) {
                    logger.error("Failed saving photo rating.", ex);
                    logger.debug("Resetting rating from {} to {}.", newRating, oldRating);

                    // Undo changes.
                    aktivePhoto.setRating(oldRating);
                    // FIXME: Reset the RatingPicker.
                    // This is not as simple as expected. Calling ratingPicker.setRating(oldValue) here
                    // will complete and finish. But once the below dialog is closed ANOTHER selection-
                    // change will occur in RatingPicker that is the same as the once that caused the error.
                    // That causes an infinite loop of error dialogs.

                    ErrorDialog.show(root, "Bewertung fehlgeschlagen",
                            "Die Bewertung für das Foto konnte nicht gespeichert werden.");
                }
            }

       // onUpdate();
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

    @FXML
    private void bt_nextPressed(ActionEvent event) {
        logger.info("Button next pressed!");

        activeIndex++;
        activeIndex = activeIndex % photos.size();

        loadImage();
    }

    @FXML
    private void bt_previousPressed(ActionEvent event) {
        logger.info("Button previous pressed!");

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

        image = imageCache.get(photos.get(activeIndex), ImageSize.ORIGINAL);
        imageView.setImage(image);

        // handling of images in original size can consume a lot of memory so collect it here
        System.gc();
    }

}
