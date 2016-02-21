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

import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.nio.file.Path;
import java.util.Collection;

/**
 * DAO for workspace directories.
 */
public interface DirectoryPathDAO {

    /**
     * Add a new path entry for a valid and existing directory to workspace.
     *
     * @param directory must be a valid path to an existing directory
     * @throws DAOException if operation fails
     */
    void create(Path directory) throws DAOException;

    /**
     * Read all current workspace directories.
     *
     * @return Collection with all current workspace directories.
     * @throws DAOException if operation fails
     */
    Collection<Path> read() throws DAOException;

    /**
     * Delete a current workspace directory.
     * If the given Path does not lead to a current workspace directory, the workspace will
     * remain unchanged.
     *
     * @param directory must be a valid path to an existing directory
     * @throws DAOException if operation fails
     */
    void delete (Path directory) throws DAOException;
}
