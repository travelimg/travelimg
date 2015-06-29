package at.ac.tuwien.qse.sepm.service.impl;

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
