package at.ac.tuwien.qse.sepm.gui;


import at.ac.tuwien.qse.sepm.entities.Photo;

import java.util.function.Predicate;


/**
 * Predicate which checks if a photo matches a given set of constraints.
 */
public interface PhotoSelector {

    /**
     * Checks if the given photo matches the selector.
     *
     * @param photo The photo to check.
     * @return true if the photo matches else false
     */
    boolean matches(Photo photo);
}
