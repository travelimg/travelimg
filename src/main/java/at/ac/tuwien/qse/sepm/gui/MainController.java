package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the main view.
 */
public class MainController {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private Organizer organizer;
    @Autowired private Inspector inspector;

    @FXML private ScrollPane scrollPane;

    @FXML private FlowPane flowPane;
    private ArrayList<ImageTile> selectedImages = new ArrayList<ImageTile>();

    private List<Photo> activePhotos = new ArrayList<>();


    public MainController() {

    }

    @FXML
    private void initialize() {
        scrollPane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.isControlDown() && event.getCode()== KeyCode.A){
                    for (Node n : flowPane.getChildren()) {
                        if(n instanceof ImageTile){
                            if(!((ImageTile) n).getSelectedProperty().getValue()){
                                ((ImageTile) n).select();
                                selectedImages.add((ImageTile) n);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Add a photo to the image grid
     *
     * @param photo The photo to be added.
     */
    public void addPhoto(Photo photo) {
        ImageTile imageTile = new ImageTile(photo);
        activePhotos.add(photo);

        imageTile.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isControlDown()) {
                    if (imageTile.getSelectedProperty().getValue()) {
                        imageTile.unselect();
                        selectedImages.remove(imageTile);
                    } else {
                        imageTile.select();
                        selectedImages.add(imageTile);
                    }
                } else {
                    for (ImageTile i : selectedImages) {
                        i.unselect();
                    }
                    selectedImages.clear();
                    imageTile.select();
                    selectedImages.add(imageTile);
                }
            }
        });

        flowPane.getChildren().add(imageTile);
    }

    /**
     * Return the List of active photos.
     *
     * @return the currently active photos
     */
    public List<Photo> getActivePhotos()
    {
        return activePhotos;
    }
    /**
     * Clear the image grid and don't show any photos.
     */
    public void clearPhotos() {
        activePhotos.clear();
        flowPane.getChildren().clear();
    }

    /**
     * Widget for one widget in the image grid. Can either be in a selected or an unselected state.
     */
    private class ImageTile extends HBox {

        private Photo photo;

        private Image image;
        private ImageView imageView;

        private BooleanProperty selected = new SimpleBooleanProperty(false);

        public ImageTile(Photo photo) {
            this.photo = photo;

            try {
                image = new Image(new FileInputStream(new File(photo.getPath())), 150, 0, true, true);
            } catch (FileNotFoundException ex) {
                logger.error("Could not find photo", ex);
                return;
            }

            imageView = new ImageView(image);
            imageView.setFitWidth(150);

            getStyleClass().add("image-tile-non-selected");


            this.getChildren().add(imageView);
        }

        /**
         * Select this photo. Triggers an update of the inspector widget.
         */
        public void select() {
            getStyleClass().remove("image-tile-non-selected");
            getStyleClass().add("image-tile-selected");
            inspector.addActivePhoto(photo);
            this.selected.set(true);
        }

        /**
         * Unselect a photo.
         */
        public void unselect() {
            getStyleClass().remove("image-tile-selected");
            getStyleClass().add("image-tile-non-selected");
            inspector.removeActivePhoto(photo);
            this.selected.set(false);
        }

        /**
         * Property which represents if this tile is currently selected or not.
         * @return The selected property.
         */
        public BooleanProperty getSelectedProperty() {
            return selected;
        }
    }
}
