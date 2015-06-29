package at.ac.tuwien.qse.sepm.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ValueFilter<T> implements Predicate<T> {

    private final Set<T> included = new HashSet<>();

    public Set<T> getIncluded() {
        return included;
    }

    @Override public boolean test(T t) {
        return getIncluded().contains(t);
    }
}
