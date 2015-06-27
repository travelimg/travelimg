package at.ac.tuwien.qse.sepm.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Predicate that additionally counts occurrences of values.
 */
public class Filter<T> implements Predicate<T> {

    private final Predicate<T> predicate;
    private final Map<T, Integer> counts = new HashMap<>();

    public Filter(Predicate<T> predicate) {
        if (predicate == null) throw new IllegalArgumentException();
        this.predicate = predicate;
    }

    /**
     * Get the number of times the value was tested since the last reset.
     *
     * @param t value for which to get the count
     * @return number of times the value was passed to {@link #test(T)}
     */
    public int getCount(T t) {
        if (!counts.containsKey(t)) {
            counts.put(t, 0);
        }
        return counts.get(t);
    }

    /**
     * Sets the counts for all values to zero.
     */
    public void reset() {
        counts.clear();
    }

    /**
     * {@inheritDoc}
     *
     * Increments the count for the tested value.
     */
    @Override public boolean test(T t) {
        counts.put(t, getCount(t) + 1);
        return predicate.test(t);
    }
}
