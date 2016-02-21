package at.ac.tuwien.qse.sepm.gui.grid;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import at.ac.tuwien.qse.sepm.entities.Photo;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ImageGrid<T extends ImageGridTile> extends TilePane {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Supplier<T> tileFactory;

    protected List<Photo> photos = new ArrayList<>();
    protected List<Photo> allPhotos;
    protected final ArrayList<T> tiles = new ArrayList<>();
    private Consumer<Collection<Photo>> selectionChangeAction = null;

    private boolean suppressSelectEvent = false;

    public ImageGrid(Supplier<T> tileFactory, List<Photo> allPhotos) {
        this.tileFactory = tileFactory;
        this.allPhotos = allPhotos;

        getStyleClass().add("image-grid");
        setAlignment(Pos.CENTER);
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;

        photos.forEach(this::addPhoto);
    }

    public void clear() {
        tiles.clear();
        getChildren().clear();
    }

    public void setSelectionChangeAction(Consumer<Collection<Photo>> selectionChangeAction) {
        this.selectionChangeAction = selectionChangeAction;
    }

    /**
     * Get the currently selected photos.
     *
     * @return set of selected photos
     */
    public Set<Photo> getSelected() {
        return tiles.stream().filter(ImageGridTile::isSelected).map(ImageGridTile::getPhoto)
                .collect(Collectors.toSet());
    }

    public Set<Photo> getUnselected() {
        return tiles.stream().filter(t -> !t.isSelected()).map(ImageGridTile::getPhoto)
                .collect(Collectors.toSet());
    }

    /**
     * Update tile for given photo.
     *
     * @param photo The photo which should be updated in the grid.
     */
    public void updatePhoto(Photo photo) {
        ImageGridTile tile = findTile(photo);
        if (tile == null)
            return;

        // update photo in list
        int index = 0;
        for (Photo p : photos) {
            if (p.getId().equals(photo.getId()))
                break;
            index++;
        }

        photos.set(index, photo);

        tile.setPhoto(photo);
    }

    /**
     * Select all photos in the grid.
     */
    public void selectAll() {
        if (getSelected().size() == photos.size()) {
            return;
        }

        LOGGER.debug("selecting all items");
        suppressSelectEvent = true;
        tiles.forEach(T::select);
        suppressSelectEvent = false;
        onSelectionChange();
    }

    /**
     * Remove selection for all photos in the grid.
     */
    public void deselectAll() {
        if (getSelected().isEmpty()) {
            return;
        }

        LOGGER.debug("deselecting all items");
        suppressSelectEvent = true;
        tiles.forEach(T::deselect);
        suppressSelectEvent = false;
        onSelectionChange();
    }

    private void addPhoto(Photo photo) {
        T tile = tileFactory.get();
        tile.setPhoto(photo);

        tile.setOnMouseClicked(event -> handleTileClicked(tile, event));

        // add tile to page
        tiles.add(tile);
        getChildren().add(tile);

        onTileAdded(tile);
    }

    private void handleTileClicked(T tile, MouseEvent event) {
        suppressSelectEvent = true;

        if (event.isControlDown()) {
            if (tile.isSelected()) {
                deselect(tile);
            } else {
                select(tile);
            }
        } else if (event.isShiftDown()) {
            int index = tiles.indexOf(tile);
            do {
                select(tiles.get(index--));
            } while (index >= 0 && !tiles.get(index).isSelected());
        } else {
            deselectAll();
            select(tile);
        }

        suppressSelectEvent = false;
        onSelectionChange();
    }

    protected void select(T tile) {
        if (tile == null)
            return;
        tile.select();
        onSelectionChange();
    }

    protected void deselect(T tile) {
        if (tile == null)
            return;
        tile.deselect();
        onSelectionChange();
    }

    protected void onSelectionChange() {
        if (selectionChangeAction == null || suppressSelectEvent)
            return;

        selectionChangeAction.accept(getSelectedItems());
    }

    protected void onTileAdded(T tile) {

    }

    private Set<Photo> getSelectedItems() {
        return tiles.stream().filter(T::isSelected).map(T::getPhoto).collect(Collectors.toSet());
    }

    private T findTile(Photo photo) {
        for (T tile : tiles) {
            if (photo.getId().equals(tile.getPhoto().getId()))
                return tile;
        }
        return null;
    }
}
