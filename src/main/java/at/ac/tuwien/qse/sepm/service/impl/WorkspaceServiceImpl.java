package at.ac.tuwien.qse.sepm.service.impl;

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
