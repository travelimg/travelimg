package at.ac.tuwien.qse.sepm.dao.repo;

import java.util.Queue;

/**
 * Repository that performs operations asynchronously.
 *
 * The PhotoRepository interface requires changes to be readable immediately. An asynchronous
 * repository would break this contract. To avoid this an intermediate storage layer has to be used,
 * that is updated and read synchronously, while the changes are still being applied to the actual
 * storage in the background.
 *
 * All asynchronous operations are stored in an operation queue. Clients can receive notifications
 * when the queue is updated by adding a listener.
 */
public interface AsyncPhotoRepository extends PhotoRepository {

    /**
     * Updates the intermediate storage so it has the same content as the actual storage.
     *
     * Every photo that is created, updated, or deleted from the intermediate
     *
     * @throws PersistenceException failed to perform operation
     */
    void synchronize() throws PersistenceException;

    /**
     * Completes the next operation in the queue, waiting if necessary until an operation becomes
     * available.
     *
     * Notifies the listeners that the operation either was completed or that an error occurred.
     */
    void completeNext();

    /**
     * Get the operations that have not yet been completed.
     *
     * @return queue of operations
     */
    Queue<Operation> getQueue();

    /**
     * Add a listener that is notified about the operation queue.
     *
     * @param listener listener that should be added
     */
    void addListener(AsyncListener listener);

    /**
     * Remove a listener from the repository.
     *
     * @param listener listener that should be removed
     */
    void removeListener(AsyncListener listener);

    /**
     * Listener that processes progress notifications from asynchronous photo repositories.
     */
    public interface AsyncListener {

        /**
         * Notifies the listener that an operation was added to the queue.
         *
         * @param repository repository in which the operation will be performed
         * @param operation operation that was added
         */
        void onAdd(AsyncPhotoRepository repository, Operation operation);

        /**
         * Notifies the listener that an operation was completed.
         *
         * @param repository repository in which the operation was performed
         * @param operation operation that was completed
         */
        void onComplete(AsyncPhotoRepository repository, Operation operation);

        /**
         * Notifies the listener that an operation failed.
         *
         * @param repository repository in which the operation was performed
         * @param operation operation that failed
         * @param error error that caused the failure
         */
        void onError(AsyncPhotoRepository repository, Operation operation, PersistenceException error);
    }
}