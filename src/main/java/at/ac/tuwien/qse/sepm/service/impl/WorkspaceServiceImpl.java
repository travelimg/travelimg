package at.ac.tuwien.qse.sepm.service.impl;

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
import at.ac.tuwien.qse.sepm.dao.DirectoryPathDAO;
import at.ac.tuwien.qse.sepm.dao.repo.FileWatcher;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.WorkspaceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class WorkspaceServiceImpl implements WorkspaceService{

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired FileWatcher watcher;
    @Autowired DirectoryPathDAO directoryPathDAO;

    /**
     * Expand the current workspace by the given Path <tt>path</tt>.
     * The photos contained by <tt>path</tt>will be loaded into the application.
     *
     * @param path must be a an existing directory
     * @throws ServiceException if an Exception on persistence layer causes the operation to fail
     */
    @Override public void addDirectory(Path path) throws ServiceException {
        LOGGER.debug("Adding directory to workspace: {}", path);

        if (!isValid(path)) {
            LOGGER.error("Path is not an existing directory: {}", path);
            throw new ServiceException("Invalid path for directory.");
        }

        try {
            directoryPathDAO.create(path);
            watcher.register(path);
        } catch (DAOException ex) {
            LOGGER.error("Failed to add directory to workspace: {}", path);
            throw new ServiceException("Failed to add directory to workspace.", ex);
        }
        watcher.index(); //to refresh
    }

    /**
     * Remove given directory with Path <tt>path</tt> from current workspace.
     * Photos from the removed directory won't be visible anymore.
     * <br />
     * If a Place does not have any corresponding images after the directory removal, the Place
     * itself will be removed.
     * If a Journey does not have any corresponding images after the directory removal, the
     * Journey itself will be removed.
     * //TODO handle Slideshow
     *
     * @param path must be a directory which is currently used as workspace
     * @throws ServiceException if an Exception on persistence layer causes the operation to fail
     */
    @Override public void removeDirectory(Path path) throws ServiceException {
        LOGGER.debug("Removing directory from workspace: {}", path);

        if (!isValid(path)) {
            LOGGER.error("Path is not an existing directory: {}", path);
            throw new ServiceException("Invalid path for directory.");
        }

        try {
            directoryPathDAO.delete(path);
            watcher.unregister(path);
        } catch (DAOException ex) {
            LOGGER.error("Failed to remove directory from workspace: {}", path);
            throw new ServiceException("Failed to remove directory from workspace",ex);
        }
        watcher.index(); //to refresh
    }

    /**
     * Return Collection of all directory-paths which are currently used as workspace.
     * @return collection of all directory-paths that are currently used as workspace.
     * @throws ServiceException if path retrieval failed
     */
    @Override
    public Collection<Path> getDirectories() throws ServiceException {
        try {
            return directoryPathDAO.read();
        } catch (DAOException ex) {
            LOGGER.error("Failed to read workspace directories.");
            throw new ServiceException("Failed to read workspace directories.",ex);
        }
    }

    /**
     * Checks if <tt>path</tt> represents an existing directory.
     *
     * @param path Path to be checked
     * @return true iff <tt>path</tt> represents an existing directory
     */
    private boolean isValid(Path path) {
        if (path == null) {
            return false;
        }

        File pathFile = path.toFile();

        if (!pathFile.exists() || !pathFile.isDirectory()) {
            return false;
        }

        return true;
    }


}
