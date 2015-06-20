package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class PhotoFilter implements Predicate<Photo> {

    private static final Logger LOGGER = LogManager.getLogger(PhotoFilter.class);

    private final Set<Tag> includedCategories = new HashSet<>();
    private final Set<Photographer> includedPhotographers = new HashSet<>();
    private final Set<Rating> includedRatings = new HashSet<>();
    private final Set<Journey> includedJourneys = new HashSet<>();
    private final Set<Place> includedPlaces = new HashSet<>();
    private boolean untaggedIncluded = false;

    /**
     * Get the names of the categories included by the filter. A photo is included if it is tagged
     * with at least one of these categories.
     *
     * @return included category names
     */
    public Set<Tag> getIncludedCategories() {
        return includedCategories;
    }

    /**
     * Get the names of photographers a photo must be assigned to.
     *
     * @return included photographer names
     */
    public Set<Photographer> getIncludedPhotographers() {
        return includedPhotographers;
    }

    /**
     * Get the journeys during one of which the photos must have been made.
     *
     * @return included journeys
     */
    public Set<Journey> getIncludedJourneys() {
        return includedJourneys;
    }

    /**
     * Get the ratings a photo must have.
     *
     * @return included ratings
     */
    public Set<Rating> getIncludedRatings() {
        return includedRatings;
    }

    /**
     * Get the places at one of which the photos must have been made.
     *
     * @return included places
     */
    public Set<Place> getIncludedPlaces() {
        return includedPlaces;
    }

    /**
     * Get a value indicating that photos without tags are included.
     *
     * @return true if untagged photos are included, else false
     */
    public boolean isUntaggedIncluded() {
        return untaggedIncluded;
    }

    /**
     * Set a value indicating that photos without tags are included.
     *
     * @param untaggedIncluded true if untagged photos should be included, else false
     */
    public void setUntaggedIncluded(boolean untaggedIncluded) {
        this.untaggedIncluded = untaggedIncluded;
    }

    @Override
    public boolean test(Photo photo) {
        return testRating(photo)
                && testCategories(photo)
                && testPhotographer(photo)
                && testJourney(photo)
                && testPlace(photo);
    }

    private boolean testRating(Photo photo) {
        return getIncludedRatings().contains(photo.getData().getRating());
    }

    private boolean testPhotographer(Photo photo) {
        // TODO: implement correct filter for null
        if (photo.getData().getPhotographer() == null) {
            return true;
        }
        return getIncludedPhotographers().contains(photo.getData().getPhotographer());
    }

    private boolean testCategories(Photo photo) {
        if (photo.getData().getTags().isEmpty()) {
            return isUntaggedIncluded();
        }
        return hasCategory(photo);
    }

    private boolean testJourney(Photo photo) {
        // TODO: implement correct filter for null
        if (photo.getData().getJourney() == null) {
            return true;
        }
        for (Journey journey : getIncludedJourneys()) {
            if (journey == null) {
                if (photo.getData().getJourney() == null) {
                    return true; // belongs to no journey
                }
            } else {
                if (journey.equals(photo.getData().getJourney()))
                    return true;
            }
        }

        return false;
    }

    private boolean testPlace(Photo photo) {
        // TODO: implement correct filter for null
        if (photo.getData().getJourney() == null) {
            return true;
        }
        return getIncludedPlaces().contains(photo.getData().getPlace());
    }

    private boolean hasCategory(Photo photo) {
        for (Tag category : getIncludedCategories()) {
            if (photo.getData().getTags().contains(category)) {
                return true;
            }
        }
        return false;
    }
}
