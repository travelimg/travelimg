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

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

public interface PhotoTagDAO {

    /**
     * Create a persistent photo-tag entry that links Tag <tt>tag</tt> to Photo <tt>photo</tt>.
     * If an equal entry already exists, nothing happens.
     *
     * @param photo must not be null; photo.id must not be null;
     * @param tag   must not be null; tag.id must not be null;
     * @throws DAOException:        if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    void createPhotoTag(Photo photo, Tag tag) throws DAOException, ValidationException;

    /**
     * Remove if exists the photo-tag entry where Photo = <tt>photo</tt> and Tag = <tt>tag</tt>
     *
     * @param photo must not be null; photo.id must not be null;
     * @param tag   must not be null; tag.id must not be null;
     * @throws DAOException:        if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    void removeTagFromPhoto(Photo photo, Tag tag) throws DAOException, ValidationException;

    /**
     * Delete if existent all photo-tag entries where Tag = <tt>tag</tt>
     *
     * @param tag must not be null; tag.id must not be null;
     * @throws DAOException:        if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    void deleteAllEntriesOfSpecificTag(Tag tag) throws DAOException, ValidationException;

    /**
     * Delete if existent all photo-tag entries where Photo = <tt>photo</tt>
     *
     * @param photo must not be null; photo.id must not be null;
     * @throws DAOException:        if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    void deleteAllEntriesOfSpecificPhoto(Photo photo) throws DAOException;

    /**
     * Return list of all tags which are currently set for <tt>photo</tt>.
     *
     * @param photo must not be null; photo.id must not be null;
     * @return List with all tags which are linked to <tt>photo</tt> as a PhotoTag;
     * If no tag exists, return an empty List.
     * @throws DAOException         if an exception occurs on persistence layer
     * @throws ValidationException: if parameter validation fails
     */
    List<Tag> readTagsByPhoto(Photo photo) throws DAOException, ValidationException;
}
