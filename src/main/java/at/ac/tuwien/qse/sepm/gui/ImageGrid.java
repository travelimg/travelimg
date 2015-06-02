package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import javafx.scene.image.Image;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ImageGrid extends TilePane {

    private static final Logger LOGGER = LogManager.getLogger();

    private final List<ImageGridTile> tiles = new LinkedList<>();
    private Consumer<Set<Photo>> selectionChangeAction;

    private final Supplier<ImageGridTile> tileSupplier;
    private ImageCache imageCache;

    public ImageGrid(Supplier<ImageGridTile> tileSupplier) {
        if (tileSupplier == null) throw new IllegalArgumentException();
        this.tileSupplier = tileSupplier;

        getStyleClass().add("image-grid");
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);
    }

    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    public List<Photo> getItems() {
        return tiles.stream()
                .map(ImageGridTile::getPhoto)
                .collect(Collectors.toList());
    }

    public void setItems(List<Photo> newItems) {
        if (newItems == null) throw new IllegalArgumentException();
        clear();
        newItems.forEach(this::addItem);
        onSelectionChange();
    }

    public void addItem(Photo item) {
        if (item == null) throw new IllegalArgumentException();
        ImageGridTile tile = tileSupplier.get();

        Image image = imageCache.get(item, ImageSize.MEDIUM);
        tile.setPhoto(item, image);

        tiles.add(tile);
        tile.setOnMouseClicked(event -> {
            if (event.isControlDown()) {
                if (tile.isSelected()) {
                    deselect(item);
                } else {
                    select(item);
                }
            } else {
                deselectAll();
                select(item);
            }
        });
        getChildren().add(tile);
    }

    public void updateItem(Photo item) {
        if (item == null) throw new IllegalArgumentException();
        ImageGridTile tile = findTile(item);
        if (tile == null) return;

        Image image = imageCache.get(item, ImageSize.MEDIUM);
        tile.setPhoto(item, image);
    }

    public void removeItem(Photo item) {
        if (item == null) throw new IllegalArgumentException();
        ImageGridTile tile = findTile(item);
        if (tile == null) return;
        tile.cancel();
        tiles.remove(item);
        getChildren().remove(tile);
        onSelectionChange();
    }

    public void clear() {
        tiles.forEach(tile -> tile.cancel());
        tiles.clear();
        getChildren().clear();
        onSelectionChange();
    }

    public Set<Photo> getSelectedItems() {
        return tiles.stream()
                .filter(tile -> tile.isSelected())
                .map(tile -> tile.getPhoto())
                .collect(Collectors.toSet());
    }

    public void select(Photo item) {
        if (item == null) throw new IllegalArgumentException();
        ImageGridTile tile = findTile(item);
        if (tile == null) return;
        tile.select();
        onSelectionChange();
    }

    public void selectAll() {
        LOGGER.debug("selecting all items");
        tiles.forEach(ImageGridTile::select);
        onSelectionChange();
    }

    public void deselect(Photo item) {
        if (item == null) throw new IllegalArgumentException();
        ImageGridTile tile = findTile(item);
        if (tile == null) return;
        tile.deselect();
        onSelectionChange();
    }

    public void deselectAll() {
        LOGGER.debug("deselecting all items");
        tiles.forEach(ImageGridTile::deselect);
        onSelectionChange();
    }

    public void setSelectionChangeAction(Consumer<Set<Photo>> selectionChangeAction) {
        this.selectionChangeAction = selectionChangeAction;
    }

    private void onSelectionChange() {
        if (selectionChangeAction == null) return;
        selectionChangeAction.accept(getSelectedItems());
    }

    private ImageGridTile findTile(Photo item) {
        for (ImageGridTile tile : tiles) {
            if (item == tile.getPhoto()) return tile;
        }
        return null;
    }
}
