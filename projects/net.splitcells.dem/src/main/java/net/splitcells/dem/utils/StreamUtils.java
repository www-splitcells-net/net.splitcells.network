package net.splitcells.dem.utils;

import java.util.Collections;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public final class StreamUtils {

    private StreamUtils() {
        throw new ConstructorIllegal();
    }

    public static <T> Stream<T> reverse(Stream<T> stream) {
        return stream.collect(
                collectingAndThen(
                        toList(), l -> {
                            Collections.reverse(l);
                            return l;
                        }))
                .stream();
    }

    public static <T, E extends RuntimeException> BinaryOperator<T> ensureSingle() {
        return (element, otherElement) -> {
            throw new IllegalArgumentException();
        };
    }
}
