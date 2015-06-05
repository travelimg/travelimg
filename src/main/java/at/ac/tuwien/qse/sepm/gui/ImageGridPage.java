package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import javafx.scene.control.ScrollPane;
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

public class ImageGridPage extends ScrollPane {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ImageCache imageCache;

    private final List<Photo> photos;
    private final List<PhotoGridTile> tiles = new LinkedList<>();

    private Consumer<Set<Photo>> selectionChangeAction = null;

    private TilePane tilePane = new TilePane();

    public ImageGridPage(List<Photo> photos, ImageCache imageCache) {
        this.photos = photos;
        this.imageCache = imageCache;

        photos.forEach(this::addPhoto);

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setFitToWidth(true);
        setFitToHeight(true);

        setContent(tilePane);
        tilePane.getStyleClass().add("image-grid");
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

    /**
     * Select the n'th photo (index) in the grid.
     * @param index Specify which photo to select.
     */
    public void selectAt(int index) {
        PhotoGridTile tile = tiles.get(Math.max(Math.min(tiles.size() - 1, index), 0));
        tile.select();
        onSelectionChange();
    }

    /**
     * Return the index of the first selected tile.
     * @return the index of the first selected tile or -1 if none is selected.
     */
    public int getFirstSelectedIndex() {
        PhotoGridTile selected = tiles.stream()
                .filter(ImageGridTile::isSelected)
                .findFirst()
                .orElse(null);

        if (selected == null) {
            return -1;
        } else {
            return tiles.indexOf(selected);
        }
    }

    private void addPhoto(Photo photo) {
        Image image = imageCache.get(photo, ImageSize.MEDIUM);

        PhotoGridTile tile = new PhotoGridTile();
        tile.setPhoto(photo, image);

        tile.setOnMouseClicked(event -> handleTileClicked(tile, event));

        // add tile to page
        tiles.add(tile);
        tilePane.getChildren().add(tile);
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
            if (photo.getId().equals(tile.getPhoto().getId())) return tile;
        }
        return null;
    }
}
