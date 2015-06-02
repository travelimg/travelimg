package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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


    private List<Photo> photos;
    private Image image;

    private int activeIndex = 0;

    private ImageCache imageCache;

    public FullscreenWindow(ImageCache imageCache) {
        FXMLLoadHelper.load(this, this, FullscreenWindow.class, "view/FullScreenDialog.fxml");

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
                if(keyEvent.getCode() == KeyCode.LEFT) {
                    bt_previousPressed(null);
                }
                if(keyEvent.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
            }
        });
    }

    public void present(List<Photo> photos) {
        this.photos = photos;

        if(activeIndex >= photos.size()) {
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
        if(activeIndex < 0)
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
