package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Rating;
import com.sun.javaws.exceptions.InvalidArgumentException;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PhotoGrid extends TilePane {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<Photo, PhotoTile> tiles = new HashMap<>();
    private Consumer<Set<Photo>> selectionChangeAction;

    public PhotoGrid() {
        getStyleClass().add("photo-grid");

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
    }

    public void removePhoto(Photo photo) {
        if (photo == null) throw new IllegalArgumentException();
        if (!tiles.containsKey(photo)) return;
        // FIXME: cancel loading operation of tile
        getChildren().remove(tiles.get(photo));
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
            // FIXME: tile is null
            // When changing selection after doing something in the inspector the tile here becomes null.
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
    private void onSelectionChange() {
        if (selectionChangeAction == null) return;
        selectionChangeAction.accept(getSelectedPhotos());
    }

    /**
     * Widget for a single image. Can either be in a selected or an unselected state.
     */
    private static class PhotoTile extends HBox {

        private static final Logger LOGGER = LogManager.getLogger();

        private final int width = 150;
        private final int height = 150;
        private final int padding = 4;

        private final Photo photo;
        private boolean selected;
        private final ProgressIndicator progress = new ProgressIndicator();

        public PhotoTile(Photo photo) {
            if (photo == null) throw new IllegalArgumentException();

            this.photo = photo;

            getStyleClass().add("photo-tile");
            getStyleClass().add("loading");

            getChildren().add(progress);
            setAlignment(Pos.CENTER);
            setPrefWidth(width + padding * 2);
            setPrefHeight(height + padding * 2);
            setPadding(new Insets(padding));

            load();
        }

        private void load() {
            File file = new File(photo.getPath());
            String url = null;
            try {
                url = file.toURI().toURL().toString();
            } catch (MalformedURLException ex) {
                LOGGER.error("photo URL is malformed", ex);
                // TODO: handle exception
            }

            Image image = new Image(url, width, height, false, true, true);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);

            image.progressProperty().addListener((observable, oldValue, newValue) -> {
                // Image is fully loaded.
                if (newValue.doubleValue() == 1.0) {
                    getChildren().clear();
                    getChildren().add(imageView);
                    getStyleClass().remove("loading");
                }
            });
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
