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

import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

public interface PhotographerDAO {

    /**
     * Store a new photographer.
     *
     * @param photographer Photographer to create
     * @return The created photographer
     * @throws ValidationException If the photographer is invalid
     * @throws DAOException        If the photographer cannot be created or the data store fails to create a record.
     */
    Photographer create(Photographer photographer) throws DAOException, ValidationException;

    /**
     * Update given photgrapher
     *
     * @param photographer The photographer to be updated.
     * @throws DAOException        If the photographer can not be updated.
     * @throws ValidationException If the photographer is invalid
     */
    void update(Photographer photographer) throws DAOException, ValidationException;

    /**
     * Reads a single photographer by the id
     *
     * @param id The id of the desired photographer
     * @return the read photographer
     * @throws DAOException If the data store fails to retrieve the record or if the photographer doesn't exist.
     */
    Photographer getById(int id) throws DAOException;

    /**
     * Reads a single photographer by its name.
     *
     * @param name The name of the desired photographer
     * @return the read photographer
     * @throws DAOException If the data store fails to retrieve the record or if the photographer doesn't exist.
     */
    Photographer getByName(String name) throws DAOException;

    /**
     * Retrieve all existing photographers.
     *
     * @return A List of all currently known photographers.
     * @throws DAOException If the data store fails to retrieve the records.
     */
    List<Photographer> readAll() throws DAOException;
}
