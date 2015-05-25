package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PhotoGrid extends TilePane {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<Photo, PhotoTile> tiles = new IdentityHashMap<>();
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
        onSelectionChange();
    }

    public void addPhoto(Photo photo) {
        if (photo == null) throw new IllegalArgumentException();
        PhotoTile tile = new PhotoTile();
        tile.setPhoto(photo);
        tiles.put(photo, tile);
        tile.setOnMouseClicked(event -> {
            if (event.isControlDown()) {
                if (tile.isSelected()) {
                    deselect(photo);
                } else {
                    select(photo);
                }
            } else {
                deselectAll();
                select(photo);
            }
        });
        getChildren().add(tile);
    }

    public void updatePhoto(Photo photo) {
        if (photo == null) throw new IllegalArgumentException();
        if (!tiles.containsKey(photo)) return;
        PhotoTile tile = tiles.get(photo);
        tile.setPhoto(photo);
    }

    public void removePhoto(Photo photo) {
        if (photo == null) throw new IllegalArgumentException();
        if (!tiles.containsKey(photo)) return;
        PhotoTile tile = tiles.get(photo);
        tile.cancel();
        tiles.remove(photo);
        getChildren().remove(tile);
        onSelectionChange();
    }

    public void clear() {
        tiles.values().forEach(tile -> tile.cancel());
        tiles.clear();
        getChildren().clear();
        onSelectionChange();
    }

    public Set<Photo> getSelectedPhotos() {
        return tiles.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(entry -> entry.getKey())
                .collect(Collectors.toSet());
    }

    public void select(Photo photo) {
        if (photo == null) throw new IllegalArgumentException();
        if (!tiles.containsKey(photo)) return;
        PhotoTile tile = tiles.get(photo);
        tile.select();
        onSelectionChange();
    }

    public void selectAll() {
        LOGGER.debug("selecting all photos");
        tiles.values().forEach(PhotoTile::select);
        onSelectionChange();
    }

    public void deselect(Photo photo) {
        if (photo == null) throw new IllegalArgumentException();
        if (!tiles.containsKey(photo)) return;
        PhotoTile tile = tiles.get(photo);
        tile.deselect();
        onSelectionChange();
    }

    public void deselectAll() {
        LOGGER.debug("deselecting all photos");
        tiles.values().forEach(PhotoTile::deselect);
        onSelectionChange();
    }

    public void setSelectionChangeAction(Consumer<Set<Photo>> selectionChangeAction) {
        this.selectionChangeAction = selectionChangeAction;
    }
    private void onSelectionChange() {
        if (selectionChangeAction == null) return;
        selectionChangeAction.accept(getSelectedPhotos());
    }
}
