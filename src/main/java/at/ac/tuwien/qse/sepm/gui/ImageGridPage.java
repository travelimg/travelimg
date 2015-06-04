package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ImageGridPage extends TilePane {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ImageCache imageCache;

    private final List<Photo> photos;
    private final List<PhotoGridTile> tiles = new LinkedList<>();

    private Consumer<Set<Photo>> selectionChangeAction = null;

    public ImageGridPage(List<Photo> photos, ImageCache imageCache) {
        this.photos = photos;
        this.imageCache = imageCache;

        photos.forEach(this::addPhoto);

        getStyleClass().add("image-grid");
    }

    public void setSelectionChangeAction(Consumer<Set<Photo>> selectionChangeAction) {
        this.selectionChangeAction = selectionChangeAction;
    }

    /**
     * Return the first photo in the grid.
     *
     * @return the first photo in the grid or null if the page is empty
     */
    public Photo getActivePhoto() {
        if (photos.isEmpty())
            return null;

        return photos.get(0);
    }

    /**
     * Update tile for given photo.
     *
     * @param photo The photo which should be updated in the grid.
     */
    public void updatePhoto(Photo photo) {
        PhotoGridTile tile = findTile(photo);
        if (tile == null) return;

        Image image = imageCache.get(photo, ImageSize.MEDIUM);
        tile.setPhoto(photo, image);
    }

    /**
     * Select all photos in the grid.
     */
    public void selectAll() {
        LOGGER.debug("selecting all items");
        tiles.forEach(ImageGridTile::select);
        onSelectionChange();
    }

    private void addPhoto(Photo photo) {
        Image image = imageCache.get(photo, ImageSize.MEDIUM);

        PhotoGridTile tile = new PhotoGridTile();
        tile.setPhoto(photo, image);

        tile.setOnMouseClicked(event -> handleTileClicked(tile, event));

        // add tile to page
        tiles.add(tile);
        getChildren().add(tile);
    }

    private void handleTileClicked(PhotoGridTile tile, MouseEvent event) {
        if (event.isControlDown()) {
            if (tile.isSelected()) {
                deselect(tile);
            } else {
                select(tile);
            }
        } else {
            deselectAll();
            select(tile);
        }
    }

    private void select(PhotoGridTile tile) {
        if (tile == null) return;
        tile.select();
        onSelectionChange();
    }



    private void deselect(PhotoGridTile tile) {
        if (tile == null) return;
        tile.deselect();
        onSelectionChange();
    }

    private void deselectAll() {
        LOGGER.debug("deselecting all items");
        tiles.forEach(ImageGridTile::deselect);
        onSelectionChange();
    }

    private void onSelectionChange() {
        if (selectionChangeAction == null) return;
        selectionChangeAction.accept(getSelectedItems());
    }

    private Set<Photo> getSelectedItems() {
        return tiles.stream()
                .filter(ImageGridTile::isSelected)
                .map(ImageGridTile::getPhoto)
                .collect(Collectors.toSet());
    }

    private PhotoGridTile findTile(Photo photo) {
        for (PhotoGridTile tile : tiles) {
            if (photo.getId() == tile.getPhoto().getId()) return tile;
        }
        return null;
    }
}
