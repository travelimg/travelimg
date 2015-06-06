package at.ac.tuwien.qse.sepm.dao.repo;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Storage for photo files.
 */
public interface PhotoRepository extends PhotoProvider {

    /**
     * Creates a photo in the repository under the specified file.
     *
     * The photo can be read immediately after the method returns.
     *
     * @param file path under which the photo should be added
     * @param source stream from which the photo data is read
     * @throws PersistenceException failed to perform operation
     * @throws PhotoNotFoundException path is not valid in this repository
     * @throws PhotoAlreadyExistsException path is already a photo in this repository
     */
    void create(Path file, InputStream source) throws PersistenceException;

    /**
     * Updates a photo.
     *
     * The changes can be read immediately after the method returns.
     *
     * @param photo photo that should be updated
     * @throws PersistenceException failed to perform operation
     * @throws PhotoNotFoundException photo does not exist in the repository
     */
    void update(Photo photo) throws PersistenceException;

    /**
     * Deletes a photo from the repository.
     *
     * The photo can no longer be read as soon as the method returns.
     *
     * @param file path of photo that should be deleted
     * @throws PersistenceException failed to perform operation
     * @throws PhotoNotFoundException photo does not exist in the repository
     */
    void delete(Path file) throws PersistenceException;

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
        void onCreate(PhotoRepository repository, Path file);

        /**
         * Notifies the listener that a photo was updated in a repository.
         *
         * @param repository repository in which the photo was updated
         * @param file path of photo that was updated
         */
        void onUpdate(PhotoRepository repository, Path file);

        /**
         * Notifies the listener that a photo was deleted from a repository.
         *
         * @param repository repository from which the photo was deleted
         * @param file path of photo that was deleted
         */
        void onDelete(PhotoRepository repository, Path file);
    }
}
