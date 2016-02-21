package at.ac.tuwien.qse.sepm.service;

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
