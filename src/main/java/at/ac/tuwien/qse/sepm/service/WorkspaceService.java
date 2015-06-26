package at.ac.tuwien.qse.sepm.service;

import java.nio.file.Path;

public interface WorkspaceService {

    /**
     * Expand the current workspace by the given Path <tt>path</tt>.
     * The photos contained by <tt>path</tt>will be loaded into the application.
     *
     * @param path must be a an existing directory
     * @throws ServiceException if an Exception on persistence layer causes the operation to fail
     */
    void addDirectory(Path path) throws ServiceException;

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
    void removeDirectory(Path path) throws ServiceException;
}
