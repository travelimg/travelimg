package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

public class MainController extends BorderPane {

    private static final Logger logger = LogManager.getLogger();

    private final Organizer organizer = new Organizer();
    private final Inspector inspector = new Inspector();

    @FXML private GridView<Photo> imageGrid;

    public MainController() {
        FXMLLoadHelper.load(this, this, MainController.class, "view/Main.fxml");

        setLeft(organizer);
        setRight(inspector);

        imageGrid.setItems(organizer.getActivePhotos());
        imageGrid.setCellFactory(list -> new PhotoGridCell());
    }
}

class PhotoGridCell extends GridCell<Photo> {

    private final ImageView imageView;

    public PhotoGridCell() {
        this.getStyleClass().add("photo-grid-cell");
        this.imageView = new ImageView();
        this.imageView.fitHeightProperty().bind(this.heightProperty());
        this.imageView.fitWidthProperty().bind(this.widthProperty());
    }

    protected void updateItem(Photo item, boolean empty) {
        super.updateItem(item, empty);
         if (empty) {
            this.setGraphic(null);
            return;
        }

        // TODO: CACHING. Images are loaded everytime the grid wraps.

        // TODO: JavaFX loads images via URI. We need that URI!
        Image image = new Image(item.getPath());
        imageView.setImage(image);
        setGraphic(this.imageView);
    }
}
