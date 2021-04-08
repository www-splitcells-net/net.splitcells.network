package net.splitcells.dem.data.set;

import java.util.Collection;
import java.util.stream.Collector;

import static java.util.Arrays.asList;

public interface Sets {
    static <T> Collector<T, ?, Set<T>> toSetOfUniques() {
        return Collector.of(
                () -> setOfUniques(),
                (a, b) -> a.addAll(b),
                (a, b) -> {
                    a.addAll(b);
                    return a;
                }
        );
    }

    @SafeVarargs
    static <T> Set<T> merge(Collection<T>... collections) {
        final var rVal = Sets.<T>setOfUniques();
        for (Collection<T> collection : collections) {
            rVal.addAll(collection);
        }
        return rVal;
    }

    static <T> Set<T> setOfUniques() {
        return SetI.make();
    }

    @SafeVarargs
    static <T> Set<T> setOfUniques(T... args) {
        return setOfUniques(asList(args));
    }

    static <T> Set<T> setOfUniques(Collection<T> arg) {
        final var rVal = SetI.make();
        rVal.addAll(arg);
        return rVal;
    }
}
