package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.scene.image.Image;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;



public class FullscreenWindow extends Pane {

    private static final Logger logger = LogManager.getLogger();

    private Stage stage;
    private Scene scene;
    private Photo photo;
    private Image image;

    int slideshowcount=0;

    @FXML
    private ImageView current;

    @FXML
    private ImageView imageView;

    @FXML
    private ImageView next;

    @FXML
    private Button bt_next;

    @FXML
    private Button bt_previous;

    private ArrayList<Photo> ListofPhotos = new ArrayList<>();

    MainController mainController = new MainController();

    public FullscreenWindow() {
        FXMLLoadHelper.load(this, this, FullscreenWindow.class, "view/FullScreenDialog.fxml");


    }

    @FXML
    private void initialize() {
        this.stage = new Stage();
        this.scene = new Scene(this);

        stage.setScene(scene);
        //stage.setFullScreen(true);

    }

    public void present(Photo photo, ArrayList<Photo> photoListe)
    {
        ListofPhotos = photoListe;

            try {
                image = new Image(new FileInputStream(new File(photoListe.get(slideshowcount).getPath())), 0, 0, true, true);
            } catch (FileNotFoundException ex) {
                logger.error("Could not find photo", ex);
                return;
            }

            imageView.setImage(image);
            slideshowcount++;

            if(slideshowcount >= photoListe.size())
                slideshowcount = 0;

        this.photo = photo;
        stage.show();


    }

    public void bt_nextPressed(ActionEvent event)
    {
        logger.info("Button next pressed!");

        logger.info(ListofPhotos.size());


        if(slideshowcount<ListofPhotos.size()-1) {
            try {
                slideshowcount++;
                image = new Image(new FileInputStream(new File(ListofPhotos.get(slideshowcount).getPath())), 0, 0, true, true);

            } catch (FileNotFoundException ex) {
                logger.error("Could not find photo", ex);
                return;
            }

            imageView.setImage(image);
            logger.info(slideshowcount);
        }
        else
            logger.info("Album Ende erreicht!");


    }

    public void bt_previousPressed(ActionEvent event) {
        logger.info("Button previous pressed!");


        if ((slideshowcount == ListofPhotos.size() || slideshowcount <= ListofPhotos.size()) && slideshowcount >0)
        {
            try {
                slideshowcount--;
                image = new Image(new FileInputStream(new File(ListofPhotos.get(slideshowcount).getPath())), 0, 0, true, true);

            } catch (FileNotFoundException ex) {
                logger.error("Could not find photo", ex);
                return;
            }


        imageView.setImage(image);
            logger.info(slideshowcount);

        }
        else
           logger.info("Album Ende erreicht!");
    }

}
