package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.service.impl.SetFilter;
import at.ac.tuwien.qse.sepm.service.impl.ValueFilter;

import java.util.Set;
import java.util.function.Predicate;

public class PhotoFilter implements Predicate<Photo> {

    private final SetFilter<Tag> tags = new SetFilter<>();
    private final ValueFilter<Rating> ratings = new ValueFilter<>();
    private final ValueFilter<Photographer> photographers = new ValueFilter<>();
    private final ValueFilter<Journey> journeys = new ValueFilter<>();
    private final ValueFilter<Place> places = new ValueFilter<>();

    private final Filter<Set<Tag>> tagFilter = new Filter<>(tags);
    private final Filter<Rating> ratingFilter = new Filter<>(ratings);
    private final Filter<Photographer> photographerFilter = new Filter<>(photographers);
    private final Filter<Journey> journeyFilter = new Filter<>(journeys);
    private final Filter<Place> placeFilter = new Filter<>(places);

    public SetFilter<Tag> getTags() {
        return tags;
    }

    public ValueFilter<Rating> getRatings() {
        return ratings;
    }

    public ValueFilter<Photographer> getPhotographers() {
        return photographers;
    }

    public ValueFilter<Journey> getJourneys() {
        return journeys;
    }

    public ValueFilter<Place> getPlaces() {
        return places;
    }

    public Filter<Set<Tag>> getTagFilter() {
        return tagFilter;
    }

    public Filter<Rating> getRatingFilter() {
        return ratingFilter;
    }

    public Filter<Photographer> getPhotographerFilter() {
        return photographerFilter;
    }

    public Filter<Journey> getJourneyFilter() {
        return journeyFilter;
    }

    public Filter<Place> getPlaceFilter() {
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

    public void reset() {
        getTagFilter().reset();
        getRatingFilter().reset();
        getPhotographerFilter().reset();
        getJourneyFilter().reset();
        getPlaceFilter().reset();
    }
}
