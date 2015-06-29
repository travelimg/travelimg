package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.*;

import java.util.HashSet;
import java.util.Set;

public class PhotoSet {

    private final Set<Integer> ids = new HashSet<>();

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

    public boolean contains(Photo photo) {
        return ids.contains(photo.getId());
    }

    public boolean add(Photo photo) {
        if (contains(photo)) return false;
        ids.add(photo.getId());
        photo.getData().getTags().forEach(getTags()::add);
        getRatings().add(photo.getData().getRating());
        getPhotographers().add(photo.getData().getPhotographer());
        getJourneys().add(photo.getData().getJourney());
        getPlaces().add(photo.getData().getPlace());
        return true;
    }

    public boolean remove(Photo photo) {
        if (!contains(photo)) return false;
        ids.remove(photo.getId());
        photo.getData().getTags().forEach(getTags()::remove);
        getRatings().remove(photo.getData().getRating());
        getPhotographers().remove(photo.getData().getPhotographer());
        getJourneys().remove(photo.getData().getJourney());
        getPlaces().remove(photo.getData().getPlace());
        return true;
    }

    public void clear() {
        ids.clear();
        getTags().clear();
        getRatings().clear();
        getPhotographers().clear();
        getJourneys().clear();
        getPlaces().clear();
    }
}
