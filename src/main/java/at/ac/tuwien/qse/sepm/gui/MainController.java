package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.dialogs.InfoDialog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Controller for the main view.
 */
public class MainController {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private Organizer organizer;
    @Autowired private Inspector inspector;

    @FXML private ScrollPane scrollPane;

    @FXML private TilePane tilePane;

    private  Photo photoForFullScreen;

    private ImageTile selectedTile = null;

    public MainController() {

    }

    @FXML
    private void initialize() {

    }

    /**
     * Add a photo to the image grid
     *
     * @param photo The photo to be added.
     */
    public void addPhoto(Photo photo) {
        ImageTile imageTile = new ImageTile(photo);

        imageTile.getSelectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    if (selectedTile != null) {
                        selectedTile.unselect();
                    }
                    selectedTile = imageTile;
                }
            }
        });

        tilePane.getChildren().add(imageTile);
    }

    /**
     * Clear the image grid and don't show any photos.
     */
    public void clearPhotos() {
        tilePane.getChildren().clear();
    }

    public void fullsizePhotos()
    {
        ObservableList<Node> c = tilePane.getChildren();


       logger.info("Info fullsize Photos");


        FullscreenWindow fullscreen = new FullscreenWindow();
        fullscreen.present(photoForFullScreen,c);


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
            photoForFullScreen = this.photo;

            try {
                image = new Image(new FileInputStream(new File(photo.getPath())), 150, 0, true, true);
            } catch (FileNotFoundException ex) {
                logger.error("Could not find photo", ex);
                return;
            }

            imageView = new ImageView(image);
            imageView.setFitWidth(150);
            imageView.setOnMouseClicked(this::handleSelected);

            getStyleClass().add("image-tile-non-selected");


            this.getChildren().add(imageView);
        }

        /**
         * Select this photo. Triggers an update of the inspector widget.
         */
        public void select() {
            getStyleClass().remove("image-tile-non-selected");
            getStyleClass().add("image-tile-selected");

            inspector.setActivePhoto(photo);

            this.selected.set(true);
        }

        /**
         * Unselect a photo.
         */
        public void unselect() {
            getStyleClass().add("image-tile-non-selected");
            getStyleClass().remove("image-tile-selected");

            this.selected.set(false);
        }

        /**
         * Property which represents if this tile is currently selected or not.
         * @return The selected property.
         */
        public BooleanProperty getSelectedProperty() {
            return selected;
        }

        private void handleSelected(MouseEvent event) {
            select();
        }
    }
}
