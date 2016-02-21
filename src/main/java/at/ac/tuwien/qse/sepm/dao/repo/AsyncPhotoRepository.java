package at.ac.tuwien.qse.sepm.dao.repo;

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
     * @throws DAOException failed to perform operation
     */
    void synchronize() throws DAOException;

    /**
     * Drop all contents currently in the queue.
     */
    void clearQueue();

    /**
     * Completes the next operation in the queue. Does nothing if the queue is empty.
     *
     * Notifies the listeners that the operation either was completed or that an error occurred.
     *
     * @return true if there is another item in the queue, otherwise false
     */
    boolean completeNext();

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
        default void onQueue(AsyncPhotoRepository repository, Operation operation) { }

        /**
         * Notifies the listener that an operation was completed.
         *
         * @param repository repository in which the operation was performed
         * @param operation operation that was completed
         */
        default void onComplete(AsyncPhotoRepository repository, Operation operation) { }

        /**
         * Notifies the listener that an operation failed.
         *
         * @param repository repository in which the operation was performed
         * @param operation operation that failed
         * @param error error that caused the failure
         */
        default void onError(AsyncPhotoRepository repository, Operation operation, DAOException error) { }
    }
}
