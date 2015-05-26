package at.ac.tuwien.qse.sepm.gui;

import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ImageGrid<E> extends TilePane {

    private static final Logger LOGGER = LogManager.getLogger();

    private final List<ImageGridTile<E>> tiles = new LinkedList<>();
    private Consumer<Set<E>> selectionChangeAction;

    private final Supplier<ImageGridTile<E>> tileSupplier;

    public ImageGrid(Supplier<ImageGridTile<E>> tileSupplier) {
        if (tileSupplier == null) throw new IllegalArgumentException();
        this.tileSupplier = tileSupplier;

        getStyleClass().add("image-grid");
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);
    }

    public List<E> getItems() {
        return tiles.stream()
                .map(ImageGridTile::getItem)
                .collect(Collectors.toList());
    }

    public void setItems(List<E> newItems) {
        if (newItems == null) throw new IllegalArgumentException();
        clear();
        newItems.forEach(this::addItem);
        onSelectionChange();
    }

    public void addItem(E item) {
        if (item == null) throw new IllegalArgumentException();
        ImageGridTile<E> tile = tileSupplier.get();
        tile.setItem(item);
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

    public void updateItem(E item) {
        if (item == null) throw new IllegalArgumentException();
        ImageGridTile<E> tile = findTile(item);
        if (tile == null) return;
        tile.setItem(item);
    }

    public void removeItem(E item) {
        if (item == null) throw new IllegalArgumentException();
        ImageGridTile<E> tile = findTile(item);
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

    public Set<E> getSelectedItems() {
        return tiles.stream()
                .filter(tile -> tile.isSelected())
                .map(tile -> tile.getItem())
                .collect(Collectors.toSet());
    }

    public void select(E item) {
        if (item == null) throw new IllegalArgumentException();
        ImageGridTile<E> tile = findTile(item);
        if (tile == null) return;
        tile.select();
        onSelectionChange();
    }

    public void selectAll() {
        LOGGER.debug("selecting all items");
        tiles.forEach(ImageGridTile<E>::select);
        onSelectionChange();
    }

    public void deselect(E item) {
        if (item == null) throw new IllegalArgumentException();
        ImageGridTile<E> tile = findTile(item);
        if (tile == null) return;
        tile.deselect();
        onSelectionChange();
    }

    public void deselectAll() {
        LOGGER.debug("deselecting all items");
        tiles.forEach(ImageGridTile::deselect);
        onSelectionChange();
    }

    public void setSelectionChangeAction(Consumer<Set<E>> selectionChangeAction) {
        this.selectionChangeAction = selectionChangeAction;
    }

    private void onSelectionChange() {
        if (selectionChangeAction == null) return;
        selectionChangeAction.accept(getSelectedItems());
    }

    private ImageGridTile<E> findTile(E item) {
        for (ImageGridTile<E> tile : tiles) {
            if (item == tile.getItem()) return tile;
        }
        return null;
    }
}
