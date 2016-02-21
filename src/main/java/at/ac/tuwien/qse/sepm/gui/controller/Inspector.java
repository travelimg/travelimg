package at.ac.tuwien.qse.sepm.gui.controller;

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

import java.util.Collection;

/**
 * GUI component for viewing and editing the details of one or more entities.
 *
 * @param <E> type of entities that can be inspected
 */
public interface Inspector<E> {

    /**
     * Get the entities the inspector currently operates on.
     *
     * @return collection of entities
     */
    Collection<E> getEntities();

    /**
     * Set the entities the inspector should operate on.
     *
     * @param entities entities that should be operated on
     */
    void setEntities(Collection<E> entities);

    /**
     * Set a function that is invoked when the entities are modified.
     *
     * @param updateHandler function when the entities are modified
     */
    void setUpdateHandler(Runnable updateHandler);

    /**
     * Synchronizes the view to match the data available to the inspector.
     */
    void refresh();
}
