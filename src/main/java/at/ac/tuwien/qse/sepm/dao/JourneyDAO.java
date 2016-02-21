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
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

public interface JourneyDAO {
    /**
     * Create the journey in the data store. If the journey already exists the existing entry is returned.
     *
     * @param journey Journey which to create.
     * @return The created Journey
     * @throws DAOException        If the data store fails to create a record.
     * @throws ValidationException If the journey is not a valid entity.
     */
    Journey create(Journey journey) throws DAOException, ValidationException;

    /**
     * Delete an existing journey.
     *
     * @param journey Specifies which journey to delete by providing the id.
     * @throws DAOException        If the data store fails to delete the record.
     * @throws ValidationException If the journey is not a valid entity.
     */
    void delete(Journey journey) throws DAOException, ValidationException;

    /**
     * Update an existing journey.
     *
     * @param journey Description of the journey to update together with the new values.
     * @throws DAOException        If the data store fails to update the record.
     * @throws ValidationException If the journey is not a valid entity.
     */
    void update(Journey journey) throws DAOException, ValidationException;

    /**
     * Retrieve a list of all journeys.
     *
     * @return List with all journeys in the data store.
     * @throws DAOException If the data store fails to deliver all journey records.
     */
    List<Journey> readAll() throws DAOException;

    /**
     * Retrive a journey by its id.
     *
     * @param id The name of the data store entry.
     * @return Returns journey record with the given id.
     * @throws DAOException        If the data store fails to deliver the record.
     * @throws ValidationException If the id is invalid.
     */
    Journey getByID(int id) throws DAOException, ValidationException;

    /**
     * Retrive a journey by its name.
     *
     * @param name The name of the data store entry.
     * @return Returns journey record with the given name.
     * @throws DAOException        If the data store fails to deliver the record.
     */
    Journey getByName(String name) throws DAOException;
}
