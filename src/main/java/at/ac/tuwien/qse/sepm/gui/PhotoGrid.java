package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import com.sun.javaws.exceptions.InvalidArgumentException;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PhotoGrid extends TilePane {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    private final TilePane tilePane = new TilePane();
    private final Map<Photo, PhotoTile> tiles = new HashMap<>();
    private Consumer<Set<Photo>> selectionChangeAction;

    public PhotoGrid() {
        getStyleClass().add("photo-grid");

        setPrefTileWidth(150);
        setPrefTileHeight(150);
        setHgap(15);
        setVgap(15);
        setPadding(new Insets(15));
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);
    }

    public List<Photo> getPhotos() {
        return new ArrayList<>(tiles.keySet());
    }

    public void setPhotos(List<Photo> newPhotos) {
        if (newPhotos == null) throw new IllegalArgumentException();
        clear();
        newPhotos.forEach(this::addPhoto);
    }

    public void addPhoto(Photo photo) {
        if (photo == null) throw new IllegalArgumentException();
        PhotoTile tile = new PhotoTile(photo);
        tiles.put(photo, tile);
        tile.setOnMouseClicked(event -> {
            deselectAll();
            select(photo);
        });
        getChildren().add(tile);

        // Load the image in a separate thread, so the GUI is not blocked.
        executor.execute(tile::load);
    }

    public void removePhoto(Photo photo) {
        if (photo == null) throw new IllegalArgumentException();
        if (!tiles.containsKey(photo)) return;
        // FIXME: cancel loading operation of tile
        tilePane.getChildren().remove(tiles.get(photo));
        tiles.remove(photo);
    }

    public void clear() {
        // FIXME: cancel loading operations of all tiles
        // Otherwise tile loading threads can stack up.
        getChildren().clear();
        tiles.clear();
    }

    public Set<Photo> getSelectedPhotos() {
        Set<Photo> photos = new HashSet<>();
        for (Photo photo : tiles.keySet()) {
            PhotoTile tile = tiles.get(photo);
            if (tile.isSelected()) {
                photos.add(photo);
            }
        }
        return photos;
    }

    public void setSelectedPhotos(List<Photo> newSelection) {
        if (newSelection == null) throw new IllegalArgumentException();
        deselectAll();
        newSelection.forEach(this::select);
    }

    public void select(Photo photo) {
        if (photo == null) throw new IllegalArgumentException();
        if (!tiles.containsKey(photo)) return;

        PhotoTile tile = tiles.get(photo);
        if (!tile.isSelected()) {
            tile.select();
            onSelectionChange();
        }
    }

    public void deselect(Photo photo) {
        if (photo == null) throw new IllegalArgumentException();
        if (!tiles.containsKey(photo)) return;

        PhotoTile tile = tiles.get(photo);
        if (tile.isSelected()) {
            tile.deselect();
            onSelectionChange();
        }
    }

    public void deselectAll() {
        tiles.values().forEach(tile -> tile.deselect());
    }

    public void setSelectionChangeAction(Consumer<Set<Photo>> selectionChangeAction) {
        this.selectionChangeAction = selectionChangeAction;
    }

    public void close() {
        executor.shutdown();
    }

    private void onSelectionChange() {
        if (selectionChangeAction == null) return;
        selectionChangeAction.accept(getSelectedPhotos());
    }

    /**
     * Widget for a single image. Can either be in a selected or an unselected state.
     */
    private static class PhotoTile extends HBox {

        private static final Logger LOGGER = LogManager.getLogger();

        private final Photo photo;
        private Image image;
        private ImageView imageView;
        private boolean selected;

        public PhotoTile(Photo photo) {
            if (photo == null) throw new IllegalArgumentException();

            this.photo = photo;

            getStyleClass().add("photo-tile");
            getStyleClass().add("loading");

            // Show a loading indicator.
            ProgressIndicator progress = new ProgressIndicator();
            getChildren().add(progress);
        }

        public void load() {
            try {
                image = new Image(new FileInputStream(new File(photo.getPath())), 150, 0, true, true);
                imageView = new ImageView(image);

                // Once the image is loaded, add it during the next GUI update.
                Platform.runLater(() -> {
                    getChildren().add(imageView);
                    getStyleClass().remove("loading");
                });
            } catch (FileNotFoundException ex) {
                LOGGER.error("could not find photo", ex);
                return;
            }
        }

        public void select() {
            getStyleClass().add("selected");
            this.selected = true;
        }

        public void deselect() {
            getStyleClass().remove("selected");
            this.selected = false;
        }

        public boolean isSelected() {
            return selected;
        }
    }
}
