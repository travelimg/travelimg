package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.repo.FileWatcher;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.WorkspaceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;

public class WorkspaceServiceImpl implements WorkspaceService{

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired FileWatcher watcher;

    /**
     * Expand the current workspace by the given Path <tt>path</tt>.
     * The photos contained by <tt>path</tt>will be loaded into the application.
     *
     * @param path must be a an existing directory
     * @throws ServiceException if an Exception on persistence layer causes the operation to fail
     */
    @Override public void addDirectory(Path path) throws ServiceException {
        LOGGER.debug("Adding directory {}", path);

        //if (path.to)
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

    }
}
