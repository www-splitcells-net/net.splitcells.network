/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.list;

import net.splitcells.dem.data.set.SetT;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.dem.utils.random.Randomness;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static org.assertj.core.api.Assertions.assertThat;

@JavaLegacy
public interface List<T> extends java.util.List<T>, ListView<T>, SetT<T> {

    default void forEachIndexed(BiConsumer<T, Integer> consumer) {
        IntStream.range(0, size()).forEach(i -> {
            consumer.accept(get(i), i);
        });
    }

    default IntStream streamIndexes() {
        return IntStream.range(0, size());
    }

    default void forEachIndex(Consumer<Integer> consumer) {
        IntStream.range(0, size()).forEach(consumer::accept);
    }

    default <R> Stream<R> mapEachIndex(Function<Integer, R> function) {
        return IntStream.range(0, size()).mapToObj(function::apply);
    }


    /**
     * This helper method makes it easier to distinguish {@code isEmpty} and {@code !isEmpty}.
     *
     * @return Whether this list has a size bigger than zero.
     */
    @Override
    default boolean hasElements() {
        return !isEmpty();
    }

    /**
     * @param arg element to be removed from this list, if present
     * @return
     * @deprecated Use {@link #delete(Object)} or {@link #deleteIfPresent(Object)} instead.
     * When the caller executes the default remove method,
     * it is highly likely, that there is a programming error, if the element is already not present.
     */
    @Deprecated
    @Override
    boolean remove(Object arg);

    /**
     * @param arg Deletes this given argument. The argument has to be present in this {@link List}.
     */
    default void delete(T arg) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY && !contains(arg)) {
            throw ExecutionException.execException(tree("A list should contain an element, but does not.")
                    .withProperty("element", "" + arg)
                    .withProperty("list", toString()));
        }
        remove(arg);
    }

    default void deleteIfPresent(T arg) {
        remove(arg);
    }

    @JavaLegacy
    /**
     * This method avoids confusion with {@link java.util.List#remove(Object)}.
     *
     * @param index
     */
    default T removeAt(int index) {
        return this.remove(index);
    }

    default List<T> removeAll() {
        try {
            clear();
        } catch (UnsupportedOperationException e) {
            logs().warn("Could not clear list natively. Using fallback instead.", e);
            while (hasElements()) {
                removeAt(0);
            }
        }
        return this;
    }

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

    default List<T> withAppendedValues(Collection<T> args) {
        this.addAll(args);
        return this;
    }

    default List<T> withRemovedByIndex(int index) {
        remove(index);
        return this;
    }

    /**
     * @param offset An offset of 0 removes the last element in the list.
     *               An offset of 1 removes the second last element in the list.
     * @return
     */
    default List<T> withRemovedFromBehind(int offset) {
        remove(size() - 1 - offset);
        return this;
    }

    default Optional<T> lastValue() {
        if (isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(get(size() - 1));
    }

    default Optional<T> firstValue() {
        if (isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(get(0));
    }

    @ReturnsThis
    default List<T> reverse() {
        Collections.reverse(this);
        return this;
    }

    @ReturnsThis
    default List<T> shuffle(Randomness rnd) {
        Collections.shuffle(this, rnd.asRandom());
        return this;
    }

    /**
     * Allows the list allocate memory in advance in relation to the expected {@link #size()} of this.
     * This method is currently only intended for runtime improvements.
     *
     * @param targetSize The expected future return value of {@link #size()}.
     */
    default void prepareForSizeOf(int targetSize) {

    }

    default List<T> withRemovedUntilExcludedIndex(int excludedIndex) {
        while (size() >= excludedIndex) {
            withRemovedFromBehind(0);
        }
        return this;
    }
}
