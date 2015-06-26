package at.ac.tuwien.qse.sepm.dao;

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
