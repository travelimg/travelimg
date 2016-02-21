package at.ac.tuwien.qse.sepm.dao.repo;

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

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.entities.Photo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A source from which photos can be read.
 */
public interface PhotoProvider {

    /**
     * Get all photo files in the provider.
     *
     * @return collection of files that can be read
     * @throws DAOException failed to perform operation
     */
    Collection<Path> index() throws DAOException;

    /**
     * Checks whether a provider contains a certain photo.
     *
     * @param file path of the photo that should be checked
     * @return true if the provider contains the photo, otherwise false
     * @throws DAOException failed to perform operation
     */
    default boolean contains(Path file) throws DAOException {
        return index().contains(file);
    }

    /**
     * Read a single photo.
     *
     * @param file path of the photo that should be read
     * @return photo that was read
     * @throws DAOException failed to perform operation
     * @throws PhotoNotFoundException photo does not exist in the provider
     */
    Photo read(Path file) throws DAOException;

    /**
     * Read all photos.
     *
     * @return all photos in the provider
     * @throws DAOException failed to perform operation
     */
    default Collection<Photo> readAll() throws DAOException {
        Collection<Path> index = index();
        Collection<Photo> result = new ArrayList<>(index.size());
        for (Path file : index) {
            result.add(read(file));
        }
        return result;
    }
}
