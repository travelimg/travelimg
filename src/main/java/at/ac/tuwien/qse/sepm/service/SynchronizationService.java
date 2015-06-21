package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.dao.repo.Operation;

import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Service for observing and querying the current synchronization status of the photo data.
 */
public interface SynchronizationService {

    /**
     * Get the operations that have not yet completed.
     *
     * @return queue of operations
     */
    Queue<Operation> getQueue();

    /**
     * Listen for new operations that are queued.
     *
     * @param callback callback that receives the new operation
     */
    void subscribeQueue(Consumer<Operation> callback);

    /**
     * Listen for operations that have been completed successfully.
     *
     * @param callback callback that receives the completed operation
     */
    void subscribeComplete(Consumer<Operation> callback);

    /**
     * Listen for operations that have failed to complete.
     *
     * @param callback callback that receives the failed operation and error
     */
    void subscribeError(BiConsumer<Operation, ServiceException> callback);
}
