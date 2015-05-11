package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Controller for the main view.
 */
public class MainController {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private Organizer organizer;
    @Autowired private Inspector inspector;

    @FXML private ScrollPane scrollPane;

    private TilePane tilePane;

    public MainController() {

    }

    @FXML
    private void initialize() {
        tilePane = new TilePane();
        tilePane.setPadding(new Insets(15, 15, 15, 15));
        tilePane.setHgap(15);
        tilePane.setVgap(15);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Horizontal
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tilePane);
    }

    public void addPhotos(Photo photo){
        try {
            Image image = new Image(new FileInputStream(new File(photo.getPath())), 150, 0, true, true);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(150);
            tilePane.getChildren().add(imageView);
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    System.out.println(photo + " clicked");
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void clearPhotos(){
        tilePane.getChildren().clear();
    }
}
