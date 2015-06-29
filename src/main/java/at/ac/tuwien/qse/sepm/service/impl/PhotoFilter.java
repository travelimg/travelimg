package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.service.impl.SetFilter;
import at.ac.tuwien.qse.sepm.service.impl.ValueFilter;

import java.util.Set;
import java.util.function.Predicate;

public class PhotoFilter implements Predicate<Photo> {

    private final SetFilter<Tag> tagFilter = new SetFilter<>();
    private final ValueFilter<Rating> ratingFilter = new ValueFilter<>();
    private final ValueFilter<Photographer> photographerFilter = new ValueFilter<>();
    private final ValueFilter<Journey> journeyFilter = new ValueFilter<>();
    private final ValueFilter<Place> placeFilter = new ValueFilter<>();

    public SetFilter<Tag> getTagFilter() {
        return tagFilter;
    }

    public ValueFilter<Rating> getRatingFilter() {
        return ratingFilter;
    }

    public ValueFilter<Photographer> getPhotographerFilter() {
        return photographerFilter;
    }

    public ValueFilter<Journey> getJourneyFilter() {
        return journeyFilter;
    }

    public ValueFilter<Place> getPlaceFilter() {
        return placeFilter;
    }

    @Override public boolean test(Photo photo) {
        if (photo == null) throw new IllegalArgumentException();
        return  getTagFilter().test(photo.getData().getTags()) &&
                getRatingFilter().test(photo.getData().getRating()) &&
                getPhotographerFilter().test(photo.getData().getPhotographer()) &&
                getJourneyFilter().test(photo.getData().getJourney()) &&
                getPlaceFilter().test(photo.getData().getPlace());
    }
}
