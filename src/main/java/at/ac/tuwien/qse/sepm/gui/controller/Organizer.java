package at.ac.tuwien.qse.sepm.gui.controller;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.service.PhotoFilter;

/**
 * View for browsing and filtering photos by various criteria.
 *
 */
public interface Organizer {

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
    PhotoFilter getUsedFilter();

    /**
     * only check Place pl
     * @param pl the Place
     */
    void setWorldMapPlace(Place pl);
}
