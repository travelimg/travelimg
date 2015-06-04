package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.gui.util.ImageCache;
import at.ac.tuwien.qse.sepm.gui.util.LRUCache;
import javafx.scene.control.Pagination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class PaginatedImageGrid extends Pagination {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private ImageCache imageCache;
    private List<Photo> photos = new ArrayList<>();

    private LRUCache<Integer, ImageGridPage> pageCache = new LRUCache<>(10);
    private Consumer<Set<Photo>> selectionChangeAction = null;

    private int photosPerPage = 20;
    private ImageGridPage activePage = null;

    public PaginatedImageGrid() {
        super(0, 0);

        setPageFactory(this::getPage);
        getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
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
        clear();

        this.photos = photos;

        // reset the cache
        pageCache.clear();

        // force an update of the current page
        // if the page count stayed the same
        if (getPageCount() == calculatePageCount()) {
            setPageCount(calculatePageCount() + 1);

        }

        // set the new page count
        setPageCount(calculatePageCount());
    }

    /**
     * Add a photo to the grid.
     *
     * @param photo The photo to be added
     */
    public void addPhoto(Photo photo) {
        // TODO: insert photo in correct order in this.photo (sorted by time)
        // and invalidate the cache
        LOGGER.error("Not implemented addPhoto");
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
        if (activePage == null) {
            return null;
        }

        return activePage.getActivePhoto();
    }

    /**
     * Select all photos in the currently active page.
     */
    public void selectAll() {
        if (activePage != null)
            activePage.selectAll();
    }

    public void removePhoto(Photo photo) {
        // TODO
        LOGGER.error("Not implemented removePhoto");
        /*int pageIndex = getPageIndexForPhoto(photo);
        ImageGridPage page = getPage(pageIndex);

        if (pageCache.containsKey(pageIndex)) {
            pageCache.remove(pageIndex);
        }

        photos.remove(photo);
        if (getPageCount() != calculatePageCount()) {
            setPageCount(calculatePageCount());
            //activePage.refresh();
        }*/
    }

    /**
     * Update the tile for given photo in the grid.
     *
     * @param photo The photo to be updated.
     */
    public void updatePhoto(Photo photo) {
        ImageGridPage page = getPageForPhoto(photo);
        page.updatePhoto(photo);
    }

    /**
     * Clear the grids and remove all pages except for an empty first one
     */
    public void clear() {
        photos.clear();
        pageCache.clear();

        setPageCount(1);
    }

    private ImageGridPage createPage(int pageIndex) {
        if (pageCache.containsKey(pageIndex))
            return pageCache.get(pageIndex);

        int endIndex = Math.min((pageIndex + 1) * photosPerPage, photos.size());
        int startIndex = Math.min(pageIndex * photosPerPage, endIndex);

        ImageGridPage page = new ImageGridPage(photos.subList(startIndex, endIndex), imageCache);

        pageCache.put(pageIndex, page);
        return page;
    }

    private ImageGridPage getPage(int pageIndex) {
        activePage = createPage(pageIndex);

        activePage.setSelectionChangeAction(selectionChangeAction);
        return activePage;
    }

    private int getPageIndexForPhoto(Photo photo) {
        int index = photos.indexOf(photo);

        return (int)Math.floor(index / (double)photosPerPage);
    }

    private ImageGridPage getPageForPhoto(Photo photo) {
        return getPage(getPageIndexForPhoto(photo));
    }

    private int calculatePageCount() {
        return Math.max(1, (int)Math.ceil(photos.size() / (double)photosPerPage));
    }
}
