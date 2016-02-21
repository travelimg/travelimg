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

import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

public interface SlideshowDAO {

    /**
     *
     * @param slideshow to create
     * @return The created slideshow
     * @throws DAOException if the slideshow cannot be created or the data store fails to create a record
     * @throws ValidationException if the diashow is invalid
     */
    Slideshow create(Slideshow slideshow) throws DAOException, ValidationException;

    /**
     * Update given slideshow
     * @param slideshow to be updated
     * @return The newly updated slideshow.
     * @throws DAOException if the slideshow can not be updated
     * @throws ValidationException if the slideshow is invalid
     */
    Slideshow update(Slideshow slideshow) throws DAOException, ValidationException;

    /**
     * Delete a selected slideshow
     * @param slideshow of the desired slideshow
     * @return the deleted slideshow
     * @throws DAOException If the data store fails to retrieve the record or if the slideshow doesnt exists
     */
    void delete(Slideshow slideshow) throws DAOException, ValidationException;

    /**
     * Retrieve all existing slideshows
     * @return a List of all existing slideshows
     * @throws DAOException If the data store fails to retrieve the record or if the slideshow doesnt exists
     */
    List<Slideshow> readAll() throws DAOException;
}
