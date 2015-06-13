package at.ac.tuwien.qse.sepm.gui.grid;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ImageGrid extends TilePane {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ImageCache imageCache;
    private final Supplier<ImageGridTile> tileFactory;

    protected List<Photo> photos = new ArrayList<>();
    protected final List<ImageGridTile> tiles = new LinkedList<>();
    private Consumer<Set<Photo>> selectionChangeAction = null;

    public ImageGrid(ImageCache imageCache, Supplier<ImageGridTile> tileFactory) {
        this.imageCache = imageCache;
        this.tileFactory = tileFactory;

        getStyleClass().add("image-grid");
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;

        photos.forEach(this::addPhoto);

    }

    public void setSelectionChangeAction(Consumer<Set<Photo>> selectionChangeAction) {
        this.selectionChangeAction = selectionChangeAction;
    }

    /**
     * Update tile for given photo.
     *
     * @param photo The photo which should be updated in the grid.
     */
    public void updatePhoto(Photo photo) {
        ImageGridTile tile = findTile(photo);
        if (tile == null) return;

        // update photo in list
        int index = 0;
        for (Photo p : photos) {
            if (p.getId().equals(photo.getId()))
                break;
            index++;
        }

        photos.set(index, photo);

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

    /**
     * Remove selection for all photos in the grid.
     */
    public void deselectAll() {
        LOGGER.debug("deselecting all items");
        tiles.forEach(ImageGridTile::deselect);
        onSelectionChange();
    }

    private void addPhoto(Photo photo) {
        Image image = imageCache.get(photo, ImageSize.MEDIUM);

        ImageGridTile tile = tileFactory.get();
        tile.setPhoto(photo, image);

        tile.setOnMouseClicked(event -> handleTileClicked(tile, event));

        // add tile to page
        tiles.add(tile);
        getChildren().add(tile);
    }

    private void handleTileClicked(ImageGridTile tile, MouseEvent event) {
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

    private void select(ImageGridTile tile) {
        if (tile == null) return;
        tile.select();
        onSelectionChange();
    }

    private void deselect(ImageGridTile tile) {
        if (tile == null) return;
        tile.deselect();
        onSelectionChange();
    }

    protected void onSelectionChange() {
        if (selectionChangeAction == null) return;
        selectionChangeAction.accept(getSelectedItems());
    }

    private Set<Photo> getSelectedItems() {
        return tiles.stream()
                .filter(ImageGridTile::isSelected)
                .map(ImageGridTile::getPhoto)
                .collect(Collectors.toSet());
    }

    private ImageGridTile findTile(Photo photo) {
        for (ImageGridTile tile : tiles) {
            if (photo.getId().equals(tile.getPhoto().getId())) return tile;
        }
        return null;
    }
}
