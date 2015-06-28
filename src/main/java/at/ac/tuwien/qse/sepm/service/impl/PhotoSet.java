package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.*;

import java.util.HashSet;

public class PhotoSet extends HashSet<Photo> {

    private final Aggregator<Tag> tags = new Aggregator<>();
    private final Aggregator<Rating> ratings = new Aggregator<>();
    private final Aggregator<Photographer> photographers = new Aggregator<>();
    private final Aggregator<Journey> journeys = new Aggregator<>();
    private final Aggregator<Place> places = new Aggregator<>();

    public Aggregator<Tag> getTags() {
        return tags;
    }

    public Aggregator<Rating> getRatings() {
        return ratings;
    }

    public Aggregator<Photographer> getPhotographers() {
        return photographers;
    }

    public Aggregator<Journey> getJourneys() {
        return journeys;
    }

    public Aggregator<Place> getPlaces() {
        return places;
    }

    @Override public boolean add(Photo photo) {
        if (!super.add(photo)) return false;
        photo.getData().getTags().forEach(getTags()::add);
        getRatings().add(photo.getData().getRating());
        getPhotographers().add(photo.getData().getPhotographer());
        getJourneys().add(photo.getData().getJourney());
        getPlaces().add(photo.getData().getPlace());
        return true;
    }

    @Override public boolean remove(Object o) {
        if (!super.remove(o)) return false;
        Photo photo = (Photo)o;
        photo.getData().getTags().forEach(getTags()::remove);
        getRatings().remove(photo.getData().getRating());
        getPhotographers().remove(photo.getData().getPhotographer());
        getJourneys().remove(photo.getData().getJourney());
        getPlaces().remove(photo.getData().getPlace());
        return true;
    }

    @Override public void clear() {
        super.clear();
        getTags().clear();
        getRatings().clear();
        getPhotographers().clear();
        getJourneys().clear();
        getPlaces().clear();
    }
}
