package at.ac.tuwien.qse.sepm.gui.grid;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.controller.Menu;
import at.ac.tuwien.qse.sepm.gui.util.ImageSize;
import at.ac.tuwien.qse.sepm.gui.util.LRUCache;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PaginatedImageGrid extends StackPane {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int PADDING = 2;
    private static final int GAP = 2;

    private Menu menu;
    private List<Photo> photos = new ArrayList<>();

    private LRUCache<Integer, ImageGridPage> pageCache = new LRUCache<>(10);
    private Consumer<Set<Photo>> selectionChangeAction = null;

    private int photosPerPage = 24;
    private ObjectProperty<ImageGridPage> activePageProperty = new SimpleObjectProperty<>(null);

    public PaginatedImageGrid(Menu menu) {
        this.menu = menu;

        heightProperty().addListener(this::handleSizeChange);
        widthProperty().addListener(this::handleSizeChange);
        activePageProperty.addListener(this::handlePageChange);
        setAlignment(Pos.CENTER);

        getChildren().add(getPage(0)); // set the initial page

        menu.addListener(new PageSwitchListener());
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * Set a list of photos to be displayed in the grid
     *
     * @param photos The photos to show in the grid
     */
    public void setPhotos(List<Photo> photos) {
        LOGGER.debug("setPhotos {}", photos);
        this.photos = photos;

        pageCache.clear();

        // set the new page count
        menu.setPageCount(calculatePageCount());
        updatePage();
    }

    /**
     * Add a photo to the grid.
     *
     * @param photo The photo to be added
     */
    public void addPhoto(Photo photo) {
        // insert sorted
        int index = 0;
        for (Photo p : photos) {
            if (p.getData().getDatetime().isBefore(photo.getData().getDatetime())) {
                photos.add(index, photo);
                setPhotos(photos);
                return;
            }
        }

        // photo is the oldest, insert at end
        photos.add(photo);
        setPhotos(photos);
    }

    public void setSelectionChangeAction(Consumer<Set<Photo>> selectionChangeAction) {
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
        if (activePageProperty.get() != null) {
            activePageProperty.get().selectAll();
        }
    }

    /**
     * Get the currently selected photos.
     *
     * @return set of selected photos
     */
    public Set<Photo> getSelected() {
        if (activePageProperty.get() == null) {
            return new HashSet<>();
        }
        return activePageProperty.get().getSelected();
    }

    /**
     * Remove a photo from the grid
     *
     * @param photo The photo to be removed.
     */
    public void removePhoto(Photo photo) {
        int oldPageCount = calculatePageCount();

        photos.remove(photo);
        pageCache.clear();

        int newPageCount = calculatePageCount();

        menu.setPageCount(newPageCount);

        if (oldPageCount == newPageCount) {
            updatePage();
        }
    }

    /**
     * Update the tile for given photo in the grid.
     *
     * @param photo The photo to be updated.
     */
    public void updatePhoto(Photo photo) {
        int index = 0;
        boolean found = false;

        for (Photo p : photos) {
            if (p.getPath().equals(photo.getPath())) {
                found = true;
                break;
            }
            index++;
        }

        if (!found) {
            LOGGER.debug("Photo not in grid {}", photo);
            addPhoto(photo);
            return;
        }

        ImageGridPage page = getPageForPhoto(photo);
        page.updatePhoto(photo);

        // update photo in list
        photos.set(index, photo);
    }

    private ImageGridPage createPage(int pageIndex) {
        if (pageCache.containsKey(pageIndex))
            return pageCache.get(pageIndex);

        int endIndex = Math.min((pageIndex + 1) * photosPerPage, photos.size());
        int startIndex = Math.min(pageIndex * photosPerPage, endIndex);

        ImageGridPage page = new ImageGridPage(photos.subList(startIndex, endIndex));
        pageCache.put(pageIndex, page);
        return page;
    }

    private ImageGridPage getPage(int pageIndex) {
        ImageGridPage page = createPage(pageIndex);

        page.setSelectionChangeAction(selectionChangeAction);

        activePageProperty.set(page);

        return page;
    }

    private int getIndexForPhoto(Photo photo) {
        int i = 0;
        for (Photo p : photos) {
            i++;
            if (photo.getPath().equals(p.getPath())) {
                return i;
            }
        }
        return -1;
    }

    private int getPageIndexForPhoto(Photo photo) {
        int index = getIndexForPhoto(photo);
        LOGGER.debug("index of photo is {}", index);
        int result = (int) Math.floor(index / (double) photosPerPage);
        LOGGER.debug("page index for photo is {}", result);
        return result;
    }

    private ImageGridPage getPageForPhoto(Photo photo) {
        return getPage(getPageIndexForPhoto(photo));
    }

    private int calculatePageCount() {
        return Math.max(1, (int) Math.ceil(photos.size() / (double) photosPerPage));
    }

    /**
     * Calculate how many photos we can fit inside the page.
     */
    private void handleSizeChange(Object observable) {
        // (over) estimate tile size
        int size = ImageSize.MEDIUM.pixels();
        int tileSize = (int)Math.ceil(PADDING + GAP + 1.1 * size);

        int photosPerRow = (int) getWidth() / tileSize;
        int photosPerCol = (int) getHeight() / tileSize;

        int totalPhotos = Math.max(photosPerRow * photosPerCol, 1);

        if (totalPhotos != photosPerPage) {
            photosPerPage = totalPhotos;
            pageCache.clear();

            menu.setPageCount(calculatePageCount());
            updatePage();
        }
    }

    private void handlePageChange(Object observable, ImageGridPage oldValue, ImageGridPage newValue) {
        if (oldValue == null) {
            return;
        }

        // carry the selection from the old page to the new page
        // if more than one photo is selected then only select the first photo from the previous selection
        int index = oldValue.getFirstSelectedIndex();

        oldValue.deselectAll();

        if (index != -1) {
            newValue.selectAt(index);
        }
    }

    private void updatePage() {
        ImageGridPage page = getPage(menu.getCurrentPage());

        getChildren().set(0, page);
    }

    private class PageSwitchListener implements Menu.Listener {
        @Override
        public void onPageSwitch(Menu sender) {
            updatePage();
        }
    }
}
