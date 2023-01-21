package net.splitcells.dem.data.atom;

import static net.splitcells.dem.utils.ExecutionException.executionException;

public interface Thing {
    default <T> void requireEqualityTo(T arg) {
        if (!equals(arg)) {
            throw executionException("Should be equal, but are not: " + this + ", " + arg);
        }
    }
}
