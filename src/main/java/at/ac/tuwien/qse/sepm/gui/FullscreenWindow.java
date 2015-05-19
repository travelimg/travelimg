package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;


public class FullscreenWindow extends BorderPane {

    private static final Logger logger = LogManager.getLogger();

    private Stage stage;
    private Scene scene;

    @FXML
    private BorderPane root;
    @FXML
    private ImageView imageView;

    private List<Photo> photos;
    private Image image;

    private int activeIndex = 0;


    public FullscreenWindow() {
        FXMLLoadHelper.load(this, this, FullscreenWindow.class, "view/FullScreenDialog.fxml");
    }

    @FXML
    private void initialize() {
        this.stage = new Stage();
        this.scene = new Scene(this);

        stage.setScene(scene);

        imageView.fitWidthProperty().bind(Bindings.subtract(root.widthProperty(), 100));
        imageView.fitHeightProperty().bind(root.heightProperty());
        root.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(final KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.RIGHT) {
                    bt_nextPressed(null);
                }
                if(keyEvent.getCode() == KeyCode.LEFT){
                    bt_previousPressed(null);
                }
                if(keyEvent.getCode() == KeyCode.ESCAPE){
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

        try {
            image = new Image(
                    new FileInputStream(
                            new File(
                                    photos.get(activeIndex).getPath())),
                    0,
                    0,
                    true,
                    true
            );

            imageView.setImage(image);
        } catch (FileNotFoundException ex) {
            logger.error("Could not find photo", ex);
        }
    }

}
