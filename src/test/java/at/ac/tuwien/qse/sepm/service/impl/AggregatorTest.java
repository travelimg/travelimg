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

import org.junit.Test;

import static org.junit.Assert.*;

public class AggregatorTest {

    @Test
    public void add_new_returnsTrue() {
        Aggregator<Integer> object = new Aggregator<>();
        int value = 12;
        assertTrue(object.add(value));
    }

    @Test
    public void getCount_notContained_returnsZero() {
        Aggregator<Integer> object = new Aggregator<>();
        int value = 12;
        assertEquals(0, object.getCount(value));
    }

    @Test
    public void getCount_contained_returnsCorrect() {
        Aggregator<Integer> object = new Aggregator<>();
        int value = 12;
        object.add(value);
        object.add(value);
        assertEquals(2, object.getCount(value));
    }

    @Test
    public void contains_addedOnceRemovedOnce_returnsFalse() {
        Aggregator<Integer> object = new Aggregator<>();
        int value = 12;
        object.add(value);
        object.remove(value);
        assertFalse(object.contains(value));
    }

    @Test
    public void contains_addedTwiceRemovedOnce_returnsTrue() {
        Aggregator<Integer> object = new Aggregator<>();
        int value = 12;
        object.add(value);
        object.add(value);
        object.remove(value);
        assertTrue(object.contains(value));
    }

    @Test
    public void iterator_addedOnceRemovedOnce_returnsFalse() {
        Aggregator<Integer> object = new Aggregator<>();
        int value = 12;
        object.add(value);
        object.remove(value);
        assertFalse(object.iterator().hasNext());
    }

    @Test
    public void iterator_addedTwiceRemovedOnce_contains() {
        Aggregator<Integer> object = new Aggregator<>();
        int value = 12;
        object.add(value);
        object.add(value);
        object.remove(value);
        assertEquals(value, (int)object.iterator().next());
    }
}
