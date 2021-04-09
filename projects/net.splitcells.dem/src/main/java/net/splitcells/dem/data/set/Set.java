package net.splitcells.dem.data.set;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Arrays.asList;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

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

    /**
     * Determines if actions on this {@link Set} are deterministic.
     * <p>
     * This is only used in order to test {@link Set} factories.
     *
     * @return Is this determinstic.
     */
    default Optional<Boolean> _isDeterministic() {
        return Optional.empty();
    }
}
