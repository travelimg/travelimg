package at.ac.tuwien.qse.sepm.gui.util;

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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * A buffer for an operation which can be executed as a batch.
 * <p>
 * Elements for the operation can be added and the operation will be executed after a fixed time
 * interval as a batch.
 */
public class BufferedBatchOperation<T> {

    private final ScheduledExecutorService scheduler;
    private final List<T> buffer = new LinkedList<>();
    private ScheduledFuture<?> future = null;
    private int delay = 2;
    private Consumer<List<T>> callback;

    public BufferedBatchOperation(Consumer<List<T>> callback, ScheduledExecutorService scheduler) {
        this.callback = callback;
        this.scheduler = scheduler;
    }

    /**
     * Add an element to the batch.
     *
     * @param element The element to be added.
     */
    public synchronized void add(T element) {
        buffer.add(element);

        if (future == null) {
            future = scheduler.schedule(this::commit, delay, TimeUnit.SECONDS);
        }
    }

    private synchronized void commit() {
        if (buffer.isEmpty()) {
            future = null;
            return;
        }

        callback.accept(new ArrayList<>(buffer));
        buffer.clear();

        future = scheduler.schedule(this::commit, delay, TimeUnit.SECONDS);
    }

}
