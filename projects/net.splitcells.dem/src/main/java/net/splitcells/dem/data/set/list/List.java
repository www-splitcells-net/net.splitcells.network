package net.splitcells.dem.data.set.list;

import net.splitcells.dem.data.set.SetT;
import net.splitcells.dem.lang.annotations.Returns_this;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;

public interface List<T> extends java.util.List<T>, ListView<T>, SetT<T> {

    @Deprecated
    default void addAll(T requiredArg, T... args) {
        this.add(requiredArg);
        this.addAll(Arrays.asList(args));
    }

    default List<T> withAppended(T... args) {
        this.addAll(Arrays.asList(args));
        return this;
    }

    default List<T> withAppended(Collection<T> args) {
        this.addAll(args);
        return this;
    }

    default Optional<T> lastValue() {
        if (isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(get(size() - 1));
    }

    default List<T> shallowCopy() {
        final List<T> shallowCopy = list();
        shallowCopy.addAll(this);
        return shallowCopy;
    }
    
    @Returns_this
    default List<T> reverse() {
        Collections.reverse(this);
        return this;
    }
}
