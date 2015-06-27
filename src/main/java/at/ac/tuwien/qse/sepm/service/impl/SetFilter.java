package at.ac.tuwien.qse.sepm.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Simple predicate for testing a sets of values.
 */
public class SetFilter<T> implements Predicate<Set<T>> {

    private final Set<T> required = new HashSet<>();
    private final Set<T> forbidden = new HashSet<>();

    /**
     * Get values that have to be in the set for it to match the filter.
     *
     * @return required values
     */
    public Set<T> getRequired() {
        return required;
    }

    /**
     * Get values that must not be in the set for it to match the filter.
     *
     * @return forbidden values
     */
    public Set<T> getForbidden() {
        return forbidden;
    }

    /**
     * {@inheritDoc}
     *
     * Returns {@code true} if the set contains all values returned from {@link #getRequired()}
     * and none from {@link #getForbidden()}.
     */
    @Override public boolean test(Set<T> t) {
        if (t == null) throw new IllegalArgumentException();
        if (!t.containsAll(getRequired())) return false;
        Set<T> intersection = new HashSet<>(getForbidden());
        intersection.retainAll(t);
        return intersection.isEmpty();
    }
}
