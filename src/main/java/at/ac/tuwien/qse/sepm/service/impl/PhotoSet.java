package at.ac.tuwien.qse.sepm.service.impl;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
