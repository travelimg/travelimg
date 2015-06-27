package at.ac.tuwien.qse.sepm.service.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AggregatorTest<T> {

    protected abstract T getValue();
    protected abstract Aggregator<T> getObject();

    @Test
    public void add_new_returnsTrue() {
        Aggregator<T> object = getObject();
        T value = getValue();
        assertTrue(object.add(value));
    }

    @Test
    public void getCount_notContained_returnsZero() {
        Aggregator<T> object = getObject();
        T value = getValue();
        assertEquals(0, object.getCount(value));
    }

    @Test
    public void getCount_contained_returnsCorrect() {
        Aggregator<T> object = getObject();
        T value = getValue();
        object.add(value);
        object.add(value);
        assertEquals(2, object.getCount(value));
    }

    @Test
    public void contains_addedOnceRemovedOnce_returnsFalse() {
        Aggregator<T> object = getObject();
        T value = getValue();
        object.add(value);
        object.remove(value);
        assertFalse(object.contains(value));
    }

    @Test
    public void contains_addedTwiceRemovedOnce_returnsTrue() {
        Aggregator<T> object = getObject();
        T value = getValue();
        object.add(value);
        object.add(value);
        object.remove(value);
        assertTrue(object.contains(value));
    }

    @Test
    public void iterator_addedOnceRemovedOnce_returnsFalse() {
        Aggregator<T> object = getObject();
        T value = getValue();
        object.add(value);
        object.remove(value);
        assertFalse(object.iterator().hasNext());
    }

    @Test
    public void iterator_addedTwiceRemovedOnce_contains() {
        Aggregator<T> object = getObject();
        T value = getValue();
        object.add(value);
        object.add(value);
        object.remove(value);
        assertEquals(value, object.iterator().next());
    }
}
