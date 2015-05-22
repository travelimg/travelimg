package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.gui.PhotoSelector;

import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

public class PhotoFilter implements PhotoSelector {

    private final Set<Tag> includedCategories = new HashSet<>();
    private final Set<Photographer> includedPhotographers = new HashSet<>();
    private final Set<Rating> includedRatings = new HashSet<>();
    private final Set<YearMonth> includedMonths = new HashSet<>();
    private boolean untaggedIncluded = false;
    private boolean unassignedIncluded = false;
    private boolean unratedIncluded = false;

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

    /**
     * Get a value indicating that photos without photographer are included.
     *
     * @return true if photos without photographer are included, else false
     */
    public boolean isUnassignedIncluded() {
        return unassignedIncluded;
    }

    /**
     * Set a value indicating that photos without photographer are included.
     *
     * @param unassignedIncluded true if photos without photographer should be included, else false
     */
    public void setUnassignedIncluded(boolean unassignedIncluded) {
        this.unassignedIncluded = unassignedIncluded;
    }

    /**
     * Get a value indicating that photos without tags are included.
     *
     * @return true if untagged photos are included, else false
     */
    public boolean isUnratedIncluded() {
        return unratedIncluded;
    }

    /**
     * Set a value indicating that photos without photographer are included.
     *
     * @param unratedIncluded true if photos without rating should be included, else false
     */
    public void setUnratedIncluded(boolean unratedIncluded) {
        this.unratedIncluded = unratedIncluded;
    }

    @Override
    public boolean matches(Photo photo) {
        return true;
    }
}
