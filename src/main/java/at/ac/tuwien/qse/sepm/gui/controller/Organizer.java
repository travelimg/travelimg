package at.ac.tuwien.qse.sepm.gui.controller;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;

import java.util.Collection;

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

    void reset();

    boolean accept(Photo photo);

    void remove(Photo photo);

    /**
     * only check Place pl
     * @param pl the Place
     */
    void setWorldMapPlace(Place pl);
}
