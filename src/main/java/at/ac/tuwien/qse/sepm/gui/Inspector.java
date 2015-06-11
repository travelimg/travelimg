package at.ac.tuwien.qse.sepm.gui;

import at.ac.tuwien.qse.sepm.entities.Photo;

import java.util.Collection;

/**
 * Controller for view that allows viewing and modifying meta-data of one or multiple photos.
 */
public interface Inspector extends MapUser {

    /**
     * Get the photos the inspector currently operates on.
     *
     * @return collection of photos
     */
    Collection<Photo> getActivePhotos();

    /**
     * Set the photos the inspector should operate on.
     *
     * @param photos photos that should be operated on
     */
    void setActivePhotos(Collection<Photo> photos);

    /**
     * Set a function that is invoked when the active photos are modified.
     *
     * @param updateHandler
     */
    void setUpdateHandler(Runnable updateHandler);

    /**
     * Set a function that is invoked when the active photos are deleted.
     *
     * @param deleteHandler
     */
    void setDeleteHandler(Runnable deleteHandler);

    /**
     * Reloads data.
     */
    void refresh();
}
