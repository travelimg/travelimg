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
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhotoFilterTest {

    private Journey createJourney() {
        return new Journey(1, "Asia 2014",
                LocalDateTime.of(2014, 1, 1, 0, 0),
                LocalDateTime.of(2015, 1, 1, 0, 0));
    }

    private Place createPlace() {
        return new Place(1, "Bombay", "India", 10, 20);
    }

    private Photo createMatchingPhoto() {
        Photo photo = new Photo();
        photo.getData().setRating(Rating.NONE);
        photo.getData().setPhotographer(new Photographer(1, "John"));
        photo.getData().setDatetime(LocalDateTime.of(2015, 12, 1, 0, 0));
        photo.getData().setPlace(createPlace());
        photo.getData().setJourney(createJourney());
        return photo;
    }

    private PhotoFilter createFilter() {
        PhotoFilter filter = new PhotoFilter();
        filter.getPhotographerFilter().getIncluded().add(new Photographer(1, "John"));
        filter.getRatingFilter().getIncluded().add(Rating.NONE);
        filter.getJourneyFilter().getIncluded().add(createJourney());
        filter.getPlaceFilter().getIncluded().add(createPlace());
        filter.getJourneyFilter().getIncluded().add(createJourney());
        return filter;
    }

    @Test
    public void filter_categoriesPhotoHasDifferent_noMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getTagFilter().getRequired().clear();
        filter.getTagFilter().getRequired().add(new Tag(1, "blue"));
        filter.getTagFilter().getRequired().add(new Tag(2, "red"));
        photo.getData().getTags().clear();
        photo.getData().getTags().add(new Tag(3, "green"));
        photo.getData().getTags().add(new Tag(3, "pink"));

        assertFalse(filter.test(photo));
    }

    @Test
    public void filter_categoriesPhotoHasSame_isMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getTagFilter().getRequired().clear();
        filter.getTagFilter().getRequired().add(new Tag(1, "blue"));
        filter.getTagFilter().getRequired().add(new Tag(2, "red"));
        photo.getData().getTags().clear();
        photo.getData().getTags().add(new Tag(1, "blue"));
        photo.getData().getTags().add(new Tag(2, "red"));

        assertTrue(filter.test(photo));
    }

    @Test
    public void filter_categoriesPhotoHasOverlap_noMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getTagFilter().getRequired().clear();
        filter.getTagFilter().getRequired().add(new Tag(1, "blue"));
        filter.getTagFilter().getRequired().add(new Tag(2, "red"));
        photo.getData().getTags().clear();
        photo.getData().getTags().add(new Tag(2, "red"));
        photo.getData().getTags().add(new Tag(3, "green"));

        assertFalse(filter.test(photo));
    }

    @Test
    public void filter_ratingPhotoHasDifferent_noMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getRatingFilter().getIncluded().clear();
        filter.getRatingFilter().getIncluded().add(Rating.GOOD);
        filter.getRatingFilter().getIncluded().add(Rating.NEUTRAL);
        photo.getData().setRating(Rating.NONE);

        assertFalse(filter.test(photo));
    }

    @Test
    public void filter_ratingPhotoHasSame_isMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getRatingFilter().getIncluded().clear();
        filter.getRatingFilter().getIncluded().add(Rating.GOOD);
        filter.getRatingFilter().getIncluded().add(Rating.NEUTRAL);
        photo.getData().setRating(Rating.GOOD);

        assertTrue(filter.test(photo));
    }

    @Test
    public void filter_photographerPhotoHasDifferent_noMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getPhotographerFilter().getIncluded().clear();
        filter.getPhotographerFilter().getIncluded().add(new Photographer(1, "John"));
        filter.getPhotographerFilter().getIncluded().add(new Photographer(2, "Bill"));
        photo.getData().setPhotographer(new Photographer(3, "Tina"));

        assertFalse(filter.test(photo));
    }

    @Test
    public void filter_photographerPhotoHasSame_isMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getPhotographerFilter().getIncluded().clear();
        filter.getPhotographerFilter().getIncluded().add(new Photographer(1, "John"));
        filter.getPhotographerFilter().getIncluded().add(new Photographer(2, "Bill"));
        photo.getData().setPhotographer(new Photographer(2, "Bill"));

        assertTrue(filter.test(photo));
    }

    @Test
    public void filter_placeAndJourneyIsNull_isMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        photo.getData().setPlace(null);
        photo.getData().setJourney(null);
        filter.getJourneyFilter().getIncluded().clear();
        filter.getJourneyFilter().getIncluded().add(null);
        filter.getPlaceFilter().getIncluded().clear();
        filter.getPlaceFilter().getIncluded().add(null);

        assertTrue(filter.test(photo));
    }

    @Test
    public void filter_placePhotoHasDifferent_noMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        Place place = createPlace();
        place.setCity("Mubai");
        filter.getPlaceFilter().getIncluded().clear();
        filter.getPlaceFilter().getIncluded().add(place);

        assertFalse(filter.test(photo));
    }

    @Test
    public void filter_journeyPhotoHasDifferent_noMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        Journey journey = createJourney();
        journey.setName("White Lodge");
        filter.getJourneyFilter().getIncluded().clear();
        filter.getJourneyFilter().getIncluded().add(journey);

        assertFalse(filter.test(photo));
    }
}
