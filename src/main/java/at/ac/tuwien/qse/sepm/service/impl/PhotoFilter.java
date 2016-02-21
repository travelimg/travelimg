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
