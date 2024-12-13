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
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.resource.communication.log.Logs;
import net.splitcells.dem.utils.random.Randomness;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static org.assertj.core.api.Assertions.assertThat;

@JavaLegacyArtifact
public interface List<T> extends java.util.List<T>, ListView<T>, SetT<T> {

    /**
     * This helper method makes it easier to distinguish {@code isEmpty} and {@code !isEmpty}.
     *
     * @return Whether this list has a size bigger than zero.
     */
    @Override
    default boolean hasElements() {
        return !isEmpty();
    }

    @JavaLegacyBody
    /**
     * This method avoids problems caused by {@link java.util.List#remove(Object)}.
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
            logs().appendWarning("Could not clear list natively. Using fallback instead.", e);
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

    default List<T> withRemovedByIndex(int index) {
        remove(index);
        return this;
    }

    /**
     *
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

    default List<T> shallowCopy() {
        final List<T> shallowCopy = Lists.list();
        shallowCopy.addAll(this);
        return shallowCopy;
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
