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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public interface Stream<T> extends java.util.stream.Stream<T> {
    default List<T> toList() {
        return collect(Lists.toList());
    }

    default Set<T> toSetOfUniques() {
        return collect(Sets.toSetOfUniques());
    }

    @Override
    Stream<T> filter(Predicate<? super T> predicate);

    @Override
    <R> Stream<R> map(Function<? super T, ? extends R> mapper);

    @Override
    <R> Stream<R> flatMap(Function<? super T, ? extends java.util.stream.Stream<? extends R>> mapper);

    @Override
    Stream<T> distinct();

    @Override
    Stream<T> sorted();

    @Override
    Stream<T> sorted(Comparator<? super T> comparator);

    @Override
    Stream<T> peek(Consumer<? super T> action);

    @Override
    Stream<T> limit(long maxSize);

    @Override
    Stream<T> skip(long n);

    @Override
    Stream<T> sequential();

    @Override
    Stream<T> parallel();

    @Override
    Stream<T> unordered();

    @Override
    Stream<T> onClose(Runnable closeHandler);
}
