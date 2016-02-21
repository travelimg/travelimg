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
import at.ac.tuwien.qse.sepm.gui.controller.Menu;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import at.ac.tuwien.qse.sepm.gui.util.LRUCache;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;

public class PaginatedImageGrid extends StackPane {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int PADDING = 2;
    private static final int GAP = 2;

    private Menu menu;
    private List<Photo> photos = new ArrayList<>();

    private LRUCache<Integer, ImageGridPage> pageCache = new LRUCache<>(10);
    private Consumer<Collection<Photo>> selectionChangeAction = null;
    private boolean allSelected = false;

    private int photosPerPage = 24;
    private ObjectProperty<ImageGridPage> activePageProperty = new SimpleObjectProperty<>(null);

    public PaginatedImageGrid(Menu menu) {
        this.menu = menu;

        heightProperty().addListener(this::handleSizeChange);
        widthProperty().addListener(this::handleSizeChange);
        activePageProperty.addListener(this::handlePageChange);
        setAlignment(Pos.CENTER);

        menu.addListener(new PageSwitchListener());
    }

    /**
     * Get a list of photos which are currently being displayed in the grid.
     * @return The list of photos in the grid.
     */
    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * Set a list of photos to be displayed in the grid
     *
     * @param photos The photos to show in the grid
     */
    public void setPhotos(List<Photo> photos) {
        this.photos.clear();
        this.photos.addAll(photos);

        Collections.sort(this.photos, new PhotoTimeComparator());
        refresh();
    }

    /**
     * Add a collection of photos to the grid.
     * @param photos The photos to be added.
     */
    public void addPhotos(Collection<Photo> photos) {
        this.photos.addAll(photos);

        Collections.sort(this.photos, new PhotoTimeComparator());
        refresh();
    }

    /**
     * Update the list of photos in the grid.
     * @param photos The photos to be updated
     */
    public void updatePhotos(List<Photo> photos) {
        photos.forEach(this::updatePhoto);
    }

    private void updatePhoto(Photo photo) {
        int index = 0;
        boolean found = false;
        for (Photo p : photos) {
            if (p.getId().equals(photo.getId())) {
                found = true;
                break;
            }

            index++;
        }

        if (found) {
            this.photos.set(index, photo);

            ImageGridPage page = getPageForIndex(index);
            page.updatePhoto(photo);
        }
    }

    /**
     * Remove a collection of photos from the grid.
     * @param photos The photos to be removed.
     */
    public void removePhotos(Collection<Photo> photos) {
        if (photos.isEmpty()) {
            return;
        }

        photos.forEach(this::removePhoto);
        refresh();
    }

    private void removePhoto(Photo photo) {
        photos.removeIf(p -> p.getId().equals(photo.getId()));
    }

    public void setSelectionChangeAction(Consumer<Collection<Photo>> selectionChangeAction) {
        this.selectionChangeAction = selectionChangeAction;
    }

    /**
     * Return the first photo of the currently active page.
     *
     * @return The first photo of the current page or null if no page is active or the page is empty
     */
    public Photo getActivePhoto() {
        if (activePageProperty.get() == null) {
            return null;
        }

        return activePageProperty.get().getActivePhoto();
    }

    /**
     * Select all photos in the currently active page.
     */
    public void selectAll() {
        allSelected = true;

        if (selectionChangeAction != null) {
            selectionChangeAction.accept(new ArrayList<>(photos));
        }

        if (activePageProperty.get() != null) {
            activePageProperty.get().selectAll();
        }
    }

    /**
     * Get the currently selected photos.
     *
     * @return collection of selected photos
     */
    public Collection<Photo> getSelected() {
        if (allSelected) {
            return photos;
        }

        if (activePageProperty.get() == null) {
            return new HashSet<>();
        }

        return activePageProperty.get().getSelected();
    }

    private ImageGridPage createPage(int pageIndex) {
        if (pageCache.containsKey(pageIndex))
            return pageCache.get(pageIndex);

        int endIndex = Math.min((pageIndex + 1) * photosPerPage, photos.size());
        int startIndex = Math.min(pageIndex * photosPerPage, endIndex);

        List<Photo> slice = photos.subList(startIndex, endIndex);

        ImageGridPage page = new ImageGridPage(slice, photos);
        pageCache.put(pageIndex, page);
        return page;
    }

    private ImageGridPage getPage(int pageIndex) {
        ImageGridPage page = createPage(pageIndex);

        page.setSelectionChangeAction(this::handlePageTilesSelected);

        activePageProperty.set(page);

        return page;
    }

    private void handlePageTilesSelected(Collection<Photo> photos) {
        ImageGridPage page = activePageProperty.get();

        if (page.getSelected().isEmpty()) {
            // all items deselected which means the user selected a single tile and the selection was
            // cleared. reset all selected state to false
            allSelected = false;
        }

        if (allSelected) {
            Set<Photo> unselected = page.getUnselected();

            Set<Photo> all = new HashSet<>(this.photos);
            all.removeAll(unselected);

            selectionChangeAction.accept(all);
        } else {
            selectionChangeAction.accept(page.getSelected());
        }
    }

    private ImageGridPage getPageForIndex(int index) {
        int pageIndex = (int) Math.floor(index / (double) photosPerPage);
        return getPage(pageIndex);
    }

    private int calculatePageCount() {
        return Math.max(1, (int) Math.ceil(photos.size() / (double) photosPerPage));
    }

    /**
     * Calculate how many photos we can fit inside the page.
     */
    private void handleSizeChange(Object observable) {
        // estimate tile size
        int size = ImageSize.MEDIUM.pixels();
        double tileSize = GAP + size;

        int photosPerRow = (int) ((getWidth() - 2 * PADDING) / tileSize);
        int photosPerCol = (int) ((getHeight() - 2 * PADDING) / (1.2 * tileSize));

        int totalPhotos = Math.max(photosPerRow * photosPerCol, 1);

        if (totalPhotos != photosPerPage) {
            photosPerPage = totalPhotos;
            refresh();
        }
    }

    private void updatePage() {
        ImageGridPage page = getPage(menu.getCurrentPage());
        getChildren().clear();
        getChildren().add(page);
    }

    private void handlePageChange(Object observable, ImageGridPage oldValue, ImageGridPage newValue) {
        if (oldValue != null) {
            allSelected = false;
            oldValue.deselectAll();
        }
    }

    private void refresh() {
        pageCache.clear();
        menu.setPageCount(calculatePageCount());
        updatePage();
    }

    private class PageSwitchListener implements Menu.Listener {
        @Override
        public void onPageSwitch(Menu sender) {
            updatePage();
        }
    }

    private class PhotoTimeComparator implements Comparator<Photo> {
        @Override public int compare(Photo photo, Photo t1) {
            int res = t1.getData().getDatetime().compareTo(photo.getData().getDatetime());
            if (res == 0) {
                return photo.getId().compareTo(t1.getId());
            }

            return res;
        }
    }
}
