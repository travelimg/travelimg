package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.service.ClusterService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class PhotoFilter implements Predicate<Photo> {

    private static final Logger LOGGER = LogManager.getLogger(PhotoFilter.class);

    @Autowired private ClusterService clusterService;

    private final Set<Tag> includedCategories = new HashSet<>();
    private final Set<Photographer> includedPhotographers = new HashSet<>();
    private final Set<Rating> includedRatings = new HashSet<>();
    private final Set<Journey> includedJourneys = new HashSet<>();
    private final Set<YearMonth> includedMonths = new HashSet<>();
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
        getIncludedMonths().addAll(from.getIncludedMonths());
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
     * Get the months a photo must be made in.
     *
     * @return included photographer names
     */
    public Set<YearMonth> getIncludedMonths() {
        return includedMonths;
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

    @Override public boolean test(Photo photo) {
        return testRating(photo)
                && testCategories(photo)
                && testPhotographer(photo)
                && testJourney(photo)
                && testMonth(photo);
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
        boolean isBelongsToNoJourneySelected = false;
        for (Journey journey : getIncludedJourneys()) {
            if (journey == null) {
                isBelongsToNoJourneySelected = true;
            } else if (journey.getStartDate().isBefore(photo.getDatetime())
                    && journey.getEndDate()
                    .isAfter(photo.getDatetime())) {
                return true;
            }
        }
        if (isBelongsToNoJourneySelected) {
            List<Journey> allJourneys;
            try {
                allJourneys = clusterService.getAllJourneys();
            } catch (ServiceException ex) {
                LOGGER.error("Retrieving all Journeys failed");
                allJourneys = new ArrayList<>();
            }
            for (Journey journey : allJourneys) {
                if (journey.getStartDate().isBefore(photo.getDatetime())
                        && journey.getEndDate()
                        .isAfter(photo.getDatetime())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean testMonth(Photo photo) {
        return getIncludedMonths().contains(YearMonth.from(photo.getDatetime()));
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
