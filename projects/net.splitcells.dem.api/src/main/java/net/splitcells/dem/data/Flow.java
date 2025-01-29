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
package net.splitcells.dem.data;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.utils.ExecutionException;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static net.splitcells.dem.data.Flows.flow;
import static net.splitcells.dem.utils.ExecutionException.execException;

public interface Flow<T> extends java.util.stream.Stream<T> {

    default Flow<T> withAppended(Stream<T> other) {
        return flow(java.util.stream.Stream.concat(this, other));
    }

    @Override
    default List<T> toList() {
        return collect(Lists.toList());
    }

    default Set<T> toSetOfUniques() {
        return collect(Sets.toSetOfUniques());
    }

    @Override
    Flow<T> filter(Predicate<? super T> predicate);

    @Override
    <R> Flow<R> map(Function<? super T, ? extends R> mapper);

    @Override
    <R> Flow<R> flatMap(Function<? super T, ? extends java.util.stream.Stream<? extends R>> mapper);

    @Override
    Flow<T> distinct();

    @Override
    Flow<T> sorted();

    @Override
    Flow<T> sorted(Comparator<? super T> comparator);

    @Override
    Flow<T> peek(Consumer<? super T> action);

    @Override
    Flow<T> limit(long maxSize);

    @Override
    Flow<T> skip(long n);

    @Override
    Flow<T> sequential();

    @Override
    Flow<T> parallel();

    @Override
    Flow<T> unordered();

    @Override
    Flow<T> onClose(Runnable closeHandler);

    default void requireSizeOf(int targetSize) {
        final int actualSize = mapToInt(element -> 1).sum();
        if (actualSize != targetSize) {
            throw ExecutionException.execException("Flow should have the size of " + targetSize + ", but has a size of " + actualSize + " instead.");
        }
    }
}
