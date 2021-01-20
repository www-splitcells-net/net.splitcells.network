package net.splitcells.dem.data.set;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static java.util.Arrays.asList;

public interface Set<T> extends java.util.Set<T> {
    
    default boolean add(T e) {
        if (contains(e)) {
            throw new IllegalArgumentException("Element " + e + " already present in " + this);
        }
        ensureContains(e);
        return true;
    }

    void ensureContains(T e);

    default void addAll(T... objects) {
        Arrays.stream(objects).forEach(e -> add(e));
    }

    default boolean containsAny(T... objects) {
        boolean rVal = false;
        final var containment = Arrays.stream(objects)
                .map(e -> contains(e))
                .reduce((a, b) -> a || b);
        return rVal || containment.orElse(false);
    }

    default Set<T> with(T... args) {
        addAll(asList(args));
        return this;
    }

    default Set<T> with(Collection<T> args) {
        addAll(args);
        return this;
    }

    default void delete(T arg) {
        if (!remove(arg)) {
            throw new IllegalArgumentException("" + arg);
        }
    }
}
