package net.splitcells.dem.data.set;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public interface SetT<T> extends Collection<T> {
    default <R> List<R> mapped(Function<T, R> mapper) {
        return Lists.<R>list().withAppended(
                stream().map(mapper).collect(toList())
        );
    }

    default <R> List<R> flatMapped(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return Lists.<R>list().withAppended(
                stream().flatMap(mapper).collect(toList())
        );
    }

    default Optional<T> reduced(BinaryOperator<T> accumulator) {
        return stream().reduce(accumulator);
    }
}
