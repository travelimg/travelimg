package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Autowired
    private ClusterService clusterService;
    private boolean untaggedIncluded = false;

    public PhotoFilter() {
    }

    /**
     * Instantiate new PhotoFilter as a copy of <tt>from</tt>.
     *
     * @param from object to be cloned; must not be null
     */
    public PhotoFilter(PhotoFilter from) {
        getIncludedCategories().addAll(from.getIncludedCategories());
        getIncludedPhotographers().addAll(from.getIncludedPhotographers());
        getIncludedRatings().addAll(from.getIncludedRatings());
        getIncludedJourneys().addAll(from.getIncludedJourneys());
        getIncludedPlaces().addAll(from.getIncludedPlaces());
        setUntaggedIncluded(from.isUntaggedIncluded());
    }

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
        return getIncludedRatings().contains(photo.getRating());
    }

    private boolean testPhotographer(Photo photo) {
        return getIncludedPhotographers().contains(photo.getPhotographer());
    }

    private boolean testCategories(Photo photo) {
        if (photo.getTags().isEmpty()) {
            return isUntaggedIncluded();
        }
        return hasCategory(photo);
    }

    private boolean testJourney(Photo photo) {
        Journey journey = null;
        if (photo.getPlace() != null)
            journey = photo.getPlace().getJourney();

        for (Journey j : getIncludedJourneys()) {
            if (j == null) {
                if (journey == null) {
                    return true; // belongs to no journey
                }
            } else {
                if (j.equals(journey))
                    return true;
            }
        }

        return false;
    }

    private boolean testPlace(Photo photo) {
        return getIncludedPlaces().contains(photo.getPlace());
    }

    private boolean hasCategory(Photo photo) {
        for (Tag category : getIncludedCategories()) {
            if (photo.getTags().contains(category)) {
                return true;
            }
        }
        return false;
    }
}
