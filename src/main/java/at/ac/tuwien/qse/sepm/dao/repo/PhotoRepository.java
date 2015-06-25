package at.ac.tuwien.qse.sepm.dao.repo;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.entities.Photo;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Storage for photo files.
 */
public interface PhotoRepository extends PhotoProvider {

    /**
     * Checks whether the path is valid in this repository.
     *
     * @param file path that should be checked
     * @return true if the path is valid, otherwise false
     * @throws DAOException failed to perform operation
     */
    boolean accepts(Path file) throws DAOException;

    /**
     * Creates a photo in the repository under the specified file.
     *
     * The photo can be read immediately after the method returns.
     *
     * @param file path under which the photo should be added
     * @param source stream from which the photo data is read
     * @throws DAOException failed to perform operation
     * @throws DAOException path is not accepted by this repository
     * @throws PhotoAlreadyExistsException path is already a photo in this repository
     */
    void create(Path file, InputStream source) throws DAOException;

    /**
     * Updates a photo.
     *
     * The changes can be read immediately after the method returns.
     *
     * @param photo photo that should be updated
     * @throws DAOException failed to perform operation
     * @throws PhotoNotFoundException photo does not exist in the repository
     */
    void update(Photo photo) throws DAOException;

    /**
     * Deletes a photo from the repository.
     *
     * The photo can no longer be read as soon as the method returns.
     *
     * @param file path of photo that should be deleted
     * @throws DAOException failed to perform operation
     * @throws PhotoNotFoundException photo does not exist in the repository
     */
    void delete(Path file) throws DAOException;

    /**
     * Add a listener that is notified about changes to the repository.
     *
     * @param listener listener that should be added
     */
    void addListener(Listener listener);

    /**
     * Remove a listener from the repository.
     *
     * @param listener listener that should be removed
     */
    void removeListener(Listener listener);

    /**
     * Listener that processes change notifications from photo repositories.
     */
    public static interface Listener {

        /**
         * Notifies the listener that a photo was created in a repository.
         *
         * @param repository repository in which the photo was created
         * @param file path of photo that was created
         */
        default void onCreate(PhotoRepository repository, Path file) { }

        /**
         * Notifies the listener that a photo was updated in a repository.
         *
         * @param repository repository in which the photo was updated
         * @param file path of photo that was updated
         */
        default void onUpdate(PhotoRepository repository, Path file) { }

        /**
         * Notifies the listener that a photo was deleted from a repository.
         *
         * @param repository repository from which the photo was deleted
         * @param file path of photo that was deleted
         */
        default void onDelete(PhotoRepository repository, Path file) { }

        /**
         * Notifies the listener that an error occurred.
         *
         * @param repository repository in which the error occurred
         * @param error the error that occurred
         */
        default void onError(PhotoRepository repository, DAOException error) { }
    }
}
