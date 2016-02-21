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

import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

public interface TagDAO {

    /**
     * Create a Tag in the data store
     *
     * @param t Tag which to create
     * @return the created Tag
     * @throws DAOException If the Tag can not be created or the data store fails to create a record.
     */
    Tag create(Tag t) throws DAOException, ValidationException;

    /**
     * Retrieve an existing Tag
     *
     * @param t Specifies which Tag to retrive by providing the id.
     * @return the Tag-Objekt
     * @throws DAOException If the Tag can not be retrieved or the data store fails to select the record.
     */
    Tag read(Tag t) throws DAOException;

    /**
     * Retrieve an existing Tag
     *
     * @param t Specifies which Tag to retrieve by providing the name.
     * @return the Tag-Objekt
     * @throws DAOException If the Tag can not be retrieved or the data store fails to select the record.
     */
    Tag readName(Tag t) throws DAOException;

    /**
     * Check if a tag already exists.
     * @param tag The tag to be checked.
     * @return true if a tag with the same name already exists else false.
     * @throws DAOException If the data store fails to select the record.
     */
    boolean exists(Tag tag) throws DAOException;

    /**
     * Delete an existing Tag.
     *
     * @param t Specifies which Tag to delete by providing the id.
     * @throws DAOException If the Tag can not be deleted or the data store fails to delete the record.
     */
    void delete(Tag t) throws DAOException;

    /**
     * Retrieve a list of all existing Tags
     *
     * @return the list of Tags
     * @throws DAOException If the Tag can not be retrieved or the data store fails to select the record.
     */
    List<Tag> readAll() throws DAOException;

}
