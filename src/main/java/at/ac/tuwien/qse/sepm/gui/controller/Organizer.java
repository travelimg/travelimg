package at.ac.tuwien.qse.sepm.gui.controller;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;

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
     * Refreshes the category list in the organizer
     */
    void refreshCategoryList(Tag tag);

    /**
     * Refreshes the place list in the organizer
     */
    void refreshPlaceList(Place place);

    /**
     * Refreshes the journey list in the organizer
     */
    void refreshJourneyList(Journey journey);

    /**
     * Refreshes the photographer list in the organizer
     */
    void refreshPhotographerList(Photographer photographer);

    /**
     * only check Place pl
     * @param pl the Place
     */
    void setWorldMapPlace(Place pl);
}
