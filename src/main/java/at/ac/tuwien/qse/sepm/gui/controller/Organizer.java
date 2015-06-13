package at.ac.tuwien.qse.sepm.gui.controller;

import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;

/**
 * View for browsing and filtering photos by various criteria.
 *
 * This view also lets the user invoke several global actions.
 *
 */
public interface Organizer {

    // TODO: move Flickr, present, import, journey actions to another class
    // These actions do not have anything to do with the Organizer.

    /**
     * Set the action invoked when the user wants to present.
     *
     * @param callback action that should be invoked
     */
    void setPresentAction(Runnable callback);

    /**
     * Set the action invoked when the user wants to import from local storage.
     *
     * @param callback action that should be invoked
     */
    void setImportAction(Runnable callback);

    /**
     * Set the action invoked when the user wants to import from Flickr.
     *
     * @param callback action that should be invoked
     */
    void setFlickrAction(Runnable callback);

    /**
     * Set the action invoked when the user wants to add a journey.
     *
     * @param callback action that should be invoked
     */
    void setJourneyAction(Runnable callback);

    /**
     * Set the action invoked when the filter changes.
     *
     * @param callback action that should be invoked
     */
    void setFilterChangeAction(Runnable callback);

    /**
     * Get the current filter.
     *
     * @return current filter
     */
    PhotoFilter getFilter();
}
