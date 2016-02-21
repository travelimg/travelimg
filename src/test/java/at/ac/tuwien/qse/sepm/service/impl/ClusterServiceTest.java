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

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class ClusterServiceTest extends ServiceTestBase {

    @Autowired
    private ClusterServiceImpl clusterService;
    @Autowired
    private PhotoDAO photoDAO;

    private Journey getViennaJourney() {
        return new Journey(4, "Vienna", LocalDateTime.of(2010, 8, 10, 0, 0, 0), LocalDateTime.of(2010, 8, 15, 0, 0, 0));
    }

    private Journey getDenverJourney() {
        return new Journey(3, "Denver", LocalDateTime.of(2005, 9, 10, 0, 0, 0), LocalDateTime.of(2005, 9, 12, 0, 0, 0));
    }

    private List<Photo> photos = Arrays.asList(
            new Photo(2, Paths.get("2.jpg"), makeMetaDenver(LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9)),
            new Photo(3, Paths.get("3.jpg"), makeMetaDenver(LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9)),
            new Photo(4, Paths.get("4.jpg"), makeMetaDenver(LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9))
    );

    private List<Photo> photosExtended = new ArrayList<>(photos);

    private PhotoMetadata makeMetaDenver(LocalDateTime datetime, double lat, double lon) {
        PhotoMetadata data = new PhotoMetadata();
        data.setPhotographer(new Photographer(1, "Test Photographer"));
        data.setJourney(getDenverJourney());
        data.setPlace(new Place(1, "Denver", "United States", 39.7, -104.9));
        data.setDatetime(datetime);
        data.setLatitude(lat);
        data.setLongitude(lon);
        return data;
    }

    private PhotoMetadata makeMetaSanFrancisco(LocalDateTime datetime, double lat, double lon) {
        PhotoMetadata data = new PhotoMetadata();
        data.setPhotographer(new Photographer(1, "Test Photographer"));
        data.setJourney(getDenverJourney());
        data.setPlace(new Place(1, "San Francisco", "United States", 39.7, -104.9));
        data.setDatetime(datetime);
        data.setLatitude(lat);
        data.setLongitude(lon);
        return data;
    }

    @Test(expected = ServiceException.class)
    public void test_create_place_malformed_throws1() throws ServiceException {
        clusterService.addPlace(null);
    }

    @Test(expected = ServiceException.class)
    public void test_create_place_malformed_throws2() throws ServiceException {
        clusterService.addPlace(new Place(1, null, "city", 0, 0));
    }

    @Test(expected = ServiceException.class)
    public void test_create_place_malformed_throws3() throws ServiceException {
        clusterService.addPlace(new Place(1, "country", null, 0, 0));
    }

    @Test
    public void test_create_place_persists() throws ServiceException {
        assertThat(clusterService.getAllPlaces(), empty());

        // create a place
        Place place = new Place(-1, "Amerika", "San Francisco", 0, 0);
        Place created = clusterService.addPlace(place);

        // check that place was added correctly
        place.setId(created.getId());
        assertThat(clusterService.getAllPlaces(), contains(place));
    }

    @Test(expected = ServiceException.class)
    public void test_create_journey_malformed_throws1() throws ServiceException {
        clusterService.addJourney(null);
    }

    @Test(expected = ServiceException.class)
    public void test_create_journey_malformed_throws2() throws ServiceException {
        Journey journey = getViennaJourney();
        journey.setName(null);
        clusterService.addJourney(journey);
    }

    @Test(expected = ServiceException.class)
    public void test_create_journey_malformed_throws3() throws ServiceException {
        Journey journey = getViennaJourney();
        journey.setStartDate(null);
        clusterService.addJourney(journey);
    }

    @Test(expected = ServiceException.class)
    public void test_create_journey_malformed_throws4() throws ServiceException {
        Journey journey = getViennaJourney();
        journey.setEndDate(null);
        clusterService.addJourney(journey);
    }

    @Test(expected = ServiceException.class)
    public void test_create_journey_malformed_throws5() throws ServiceException {
        Journey journey = getViennaJourney();
        LocalDateTime start = journey.getStartDate();
        journey.setStartDate(journey.getEndDate());
        journey.setEndDate(start);
        clusterService.addJourney(journey);
    }

    @Test
    public void test_create_journey_persists() throws ServiceException {
        assertThat(clusterService.getAllJourneys(), empty());

        // create a journey
        Journey journey = getViennaJourney();
        Journey created = clusterService.addJourney(journey);

        // check that journey was added correctly
        journey.setId(created.getId());
        assertThat(clusterService.getAllJourneys(), contains(journey));
    }

    @Test
    @WithData
    public void test_simple_cluster() throws ServiceException, DAOException {
        Journey denverJourney = getDenverJourney();

        assertThat(clusterService.getAllJourneys().size(), is(2));
        List<Place> places = clusterService.clusterJourney(denverJourney);
        assertThat(clusterService.getAllJourneys().size(), is(3));
        assertThat(clusterService.getAllJourneys(), hasItem(denverJourney));

        assertThat(places.size(), is(1));
        assertThat(places.get(0).getCity(), equalTo("Denver"));
        assertThat(places.get(0).getCountry(), equalTo("United States"));

        List<Photo> journeyPhotos = photoDAO.readPhotosByJourney(denverJourney);
        assertThat(journeyPhotos.size(), is(3));
        assertThat(journeyPhotos, containsInAnyOrder(photos.get(0), photos.get(1), photos.get(2)));

        assertThat(journeyPhotos.get(0).getData().getPlace(), equalTo(places.get(0)));
        assertThat(journeyPhotos.get(1).getData().getPlace(), equalTo(places.get(0)));
        assertThat(journeyPhotos.get(2).getData().getPlace(), equalTo(places.get(0)));
    }

    @Test
    @WithData
    public void test_multiple_cluster() throws ServiceException, DAOException {
        Journey usaJourney = getDenverJourney();

        photosExtended.add(photoDAO.create(new Photo(6, Paths.get("6.jpg"), makeMetaSanFrancisco(LocalDateTime.of(2005, 9, 12, 0, 0, 0), 37.77, -122.42))));
        photosExtended.add(photoDAO.create(new Photo(7, Paths.get("7.jpg"), makeMetaSanFrancisco(LocalDateTime.of(2005, 9, 12, 0, 0, 0), 37.78, -122.419))));

        assertThat(clusterService.getAllJourneys().size(), is(2));
        List<Place> places = clusterService.clusterJourney(usaJourney);
        assertThat(clusterService.getAllJourneys().size(), is(3));
        assertThat(clusterService.getAllJourneys(), hasItem(usaJourney));

        assertThat(places.size(), is(2));
        assertThat(places.stream().filter(p -> p.getCity().equals("San Francisco")).count() == 1, is(Boolean.TRUE));
        assertThat(places.stream().filter(p -> p.getCity().equals("Denver")).count() == 1, is(Boolean.TRUE));
    }
}
