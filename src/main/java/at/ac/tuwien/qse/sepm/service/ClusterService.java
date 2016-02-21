package at.ac.tuwien.qse.sepm.service;

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

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Place;

import java.util.List;
import java.util.function.Consumer;

public interface ClusterService {

    /**
     * Read and return all currently saved journeys.
     *
     * @return list of all currently saved journeys
     * @throws ServiceException propagates DAOExceptions
     */
    List<Journey> getAllJourneys() throws ServiceException;

    /**
     * Read and return all currently saved places.
     *
     * @return list of all currently saved places
     * @throws ServiceException propagates DAOExceptions
     */
    List<Place> getAllPlaces() throws ServiceException;

    /**
     * Read and return a list of places of a journey sorted in chronological (visiting) order.
     *
     * @param journey The journey for which the places are desired.
     * @return The places which where visited during the journey in ascending order.
     * @throws ServiceException If the places can not be fetched.
     */
    List<Place> getPlacesByJourneyChronological(Journey journey) throws ServiceException;

    /**
     * Adds a new place to the datastore.
     *
     * @param place Place to be created in the datastore.
     * @return the newly created place
     * @throws ServiceException If creation of record fails.
     */
    Place addPlace(Place place) throws ServiceException;

    /**
     * Adds a new journey to the datastore.
     *
     * @param journey Journey to be created in the datastore.
     * @return the newly created journey
     * @throws ServiceException If creation of record fails.
     */
    Journey addJourney(Journey journey) throws ServiceException;

    /**
     * Creates a journey and clusters this journey in places.
     * <p>
     * A journey entry is made in the data record.
     * This journey is then clustered into places (places are at least 1 degree apart from each other)
     *
     * @param journey Journey to be clustered.
     * @return Returns a list with all the clusters(places).
     * @throws ServiceException If cluster-service fails due to database errors.
     */
    List<Place> clusterJourney(Journey journey) throws ServiceException;

    /**
     * Finds a place near to where the photo was taken.
     * @param photo
     * @return the place
     * @throws ServiceException
     */
    Place getPlaceNearTo(Photo photo) throws ServiceException;

    /**
     * Subscribe a callback to refresh places
     * @param callback
     */
    void subscribePlaceChanged(Consumer<Place> callback);

    /**
     * Subscribe a callback to refresh journeys
     * @param callback
     */
    void subscribeJourneyChanged(Consumer<Journey> callback);
}
