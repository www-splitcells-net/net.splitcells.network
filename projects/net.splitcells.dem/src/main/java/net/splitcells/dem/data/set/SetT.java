package net.splitcells.dem.data.set;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import org.assertj.core.api.Assertions;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

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

    default boolean hasDuplicates() {
        final java.util.Set<T> uniques = new HashSet<>();
        for(T e : this) {
            if(uniques.contains(e)) {
                return true;
            }
            uniques.add(e);
        }
        return false;
    }
    
    default void requireUniqueness() {
        assertThat(!this.hasDuplicates());
    }
}
