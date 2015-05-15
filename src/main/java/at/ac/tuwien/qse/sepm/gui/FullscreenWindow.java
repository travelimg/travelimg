package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FullscreenWindow extends Pane {

    private static final Logger logger = LogManager.getLogger();

    private Stage stage;
    private Scene scene;
    private Photo photo;
    private Image image;

    @FXML
    private ImageView imageView;

    public FullscreenWindow() {
        FXMLLoadHelper.load(this, this, FullscreenWindow.class, "view/FullScreenDialog.fxml");



    }

    @FXML
    private void initialize() {
        this.stage = new Stage();
        this.scene = new Scene(this);

        stage.setScene(scene);
        stage.setFullScreen(true);

    }

    public void present(Photo photo)
    {
        this.photo = photo;
        logger.info(photo.getPath());
        //image = new Image(new File(photo.getPath()));

        try {
            image = new Image(new FileInputStream(new File(photo.getPath())), 0, 0, true, true);
        } catch (FileNotFoundException ex) {
            logger.error("Could not find photo", ex);
            return;
        }


        imageView.setImage(image);
        //photo.setPath("blabla");
        //logger.info(photo.getPath());


        stage.showAndWait();
    }

}
