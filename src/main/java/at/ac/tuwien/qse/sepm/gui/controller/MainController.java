package at.ac.tuwien.qse.sepm.gui.controller;

import at.ac.tuwien.qse.sepm.entities.Place;

public interface MainController {

    /**
     * Switch to the main grid and show photos for the given place.
     * @param place The place for which to show photos.
     */
    void showGridWithPlace(Place place);
}
