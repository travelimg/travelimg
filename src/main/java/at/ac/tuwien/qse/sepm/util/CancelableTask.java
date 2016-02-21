package at.ac.tuwien.qse.sepm.util;


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

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CancelableTask implements Runnable, Cancelable {

    private AtomicBoolean finished = new AtomicBoolean(false);
    private AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Return the current execution status of the task.
     *
     * @return true if the task is currently executing else false.
     */
    protected boolean isRunning() {
        return running.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        running.set(true);
        execute();
        running.set(false);
        finished.set(true);
    }

    /**
     * Cancel a running task.
     * <p>
     * The processing will stop as soon as possible, but it may not be immediately
     */
    @Override
    public void cancel() {
        running.set(false);
    }

    @Override
    public boolean isFinished() {
        return finished.get();
    }

    /**
     * Perform the work the task is intended for.
     * <p>
     * The running status must continuously be checked and the processing must stop when the task was canceled.
     */
    protected abstract void execute();
}
