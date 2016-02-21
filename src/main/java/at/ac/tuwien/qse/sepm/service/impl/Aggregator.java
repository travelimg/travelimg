package at.ac.tuwien.qse.sepm.service.impl;

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

import java.util.*;

public class Aggregator<T> implements Iterable<T> {

    private final Set<T> values = new HashSet<>();
    private final Map<T, Integer> counts = new HashMap<>();

    @Override public Iterator<T> iterator() {
        return values.iterator();
    }

    public boolean contains(T t) {
        return values.contains(t);
    }

    /**
     * Get the number of times the value was added since the last clear call.
     *
     * @param t value for which to get the count
     * @return number of times the value was passed to {@link #add(T)}
     */
    public int getCount(T t) {
        if (!counts.containsKey(t)) {
            counts.put(t, 0);
        }
        return counts.get(t);
    }

    /**
     * Adds the value to the value set and increments its counter.
     *
     * @param value value that should be counted
     */
    public boolean add(T value) {
        counts.put(value, getCount(value) + 1);
        if (contains(value)) return false;
        values.add(value);
        return true;
    }

    /**
     * Decrements the counter for the value and removes it completely if the count hits zero.
     *
     * @param value value that should be counted
     */
    public boolean remove(T value) {
        if (!contains(value)) return false;
        counts.put(value, getCount(value) - 1);
        if (getCount(value) <= 0) {
            values.remove(value);
            counts.remove(value);
        }
        return true;
    }

    /**
     * Clears the recorded values and counts.
     */
    public void clear() {
        counts.clear();
    }
}
