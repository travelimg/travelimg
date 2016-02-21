package at.ac.tuwien.qse.sepm.dao;

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
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;


public interface PlaceDAO {
    /**
     * Create the place in the data store. If the place already exists the existing entry is returned.
     *
     * @param place Place which to create.
     * @return The created Place
     * @throws DAOException        If the data store fails to create a record.
     * @throws ValidationException If the place is not a valid entity.
     */
    Place create(Place place) throws DAOException, ValidationException;

    /**
     * Update an existing place.
     *
     * @param place Description of the place to update together with the new values.
     * @throws DAOException        If the data store fails to update the record.
     * @throws ValidationException If the place is not a valid entity.
     */
    void update(Place place) throws DAOException, ValidationException;

    /**
     * Retrieve a list of all places.
     *
     * @return List with all places in the data store.
     * @throws DAOException If the data store fails to deliver all place records.
     */
    List<Place> readAll() throws DAOException;

    /**
     * Retrive a place by its id
     *
     * @param id The id of the data store entry.
     * @return Returns place record with the given id.
     * @throws DAOException        If the data store fails to deliver the record.
     * @throws ValidationException If the id is invalid.
     */
    Place getById(int id) throws DAOException, ValidationException;

    /**
     * Retrieves the first place with the given country and city.
     *
     * @param country name of the country
     * @param city name of the city
     * @return first place that matches it
     * @throws DAOException If the data store fails to deliver the record.
     */
    Place readByCountryCity(String country, String city) throws DAOException;
}
