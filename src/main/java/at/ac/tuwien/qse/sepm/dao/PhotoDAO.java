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
import at.ac.tuwien.qse.sepm.entities.Photo;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public interface PhotoDAO {

    /**
     * Create the photo in the data store.
     *
     * @param photo Photo which to create.
     * @return The created photo
     * @throws DAOException If the store fails to create a record.
     */
    Photo create(Photo photo) throws DAOException;

    /**
     * Update an existing photo.
     *
     * @param photo Description of the photo to update together with the new values.
     * @throws DAOException If the photo does not exist or the data store fails to update the record.
     */
    void update(Photo photo) throws DAOException;

    /**
     * Delete an existing photo.
     *
     * @param photo Specifies which photo to delete by providing the id.
     * @throws DAOException If the data store fails to delete the record.
     */
    void delete(Photo photo) throws DAOException;

    /**
     * Get a photo by its id
     *
     * @param id the id of the photo
     * @return The photo belonging to the id
     * @throws DAOException        If the photo can not be retrived.
     */
    Photo getById(int id) throws DAOException;

    /**
     * Get a photo by its path.
     *
     * @param file The file of the desired photo.
     * @return The photo belonging to the given path.
     * @throws DAOException If no photo exists under this path.
     */
    Photo getByFile(Path file) throws DAOException;

    /**
     * Retrieve all existing photos.
     *
     * @return A list of all currently known photos.
     * @throws DAOException If the data store fails to retrieve the records.
     */
    List<Photo> readAll() throws DAOException;

    /**
     * Retrieve the paths of all existing photos.
     *
     * @return A list of all currently known photo paths.
     * @throws DAOException If the data store fails to retrieve the records.
     */
    List<Path> readAllPaths() throws DAOException;

    /**
     * Retrieve a list of photos from a given journey
     *
     * @param journey this sets the start and end-date
     * @return the list with the photos
     */
    List<Photo> readPhotosByJourney(Journey journey) throws DAOException;

    /**
     * Retrieve a list of photos from a given time interval.
     *
     * @param start lower bound for photo time.
     * @param end upper bound for photo time.
     * @return a list of photos falling into the time interval.
     * @throws DAOException if retrieval fails due to a datastore error.
     */
    List<Photo> readPhotosBetween(LocalDateTime start, LocalDateTime end) throws DAOException;
}
