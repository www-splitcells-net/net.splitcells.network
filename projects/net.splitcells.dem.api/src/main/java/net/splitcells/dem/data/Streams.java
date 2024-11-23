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

import net.splitcells.dem.lang.annotations.JavaLegacy;

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

@JavaLegacy
public class Streams<T> implements Stream<T> {

    public static <R> Stream<R> stream(java.util.stream.Stream<R> arg) {
        return new Streams<>(arg);
    }

    private final java.util.stream.Stream<T> content;

    private Streams(java.util.stream.Stream<T> argContent) {
        content = argContent;
    }

    @Override
    public Stream<T> filter(Predicate<? super T> predicate) {
        return stream(content.filter(predicate));
    }

    @Override
    public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
        return stream(content.map(mapper));
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super T> mapper) {
        return content.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super T> mapper) {
        return content.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
        return content.mapToDouble(mapper);
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super T, ? extends java.util.stream.Stream<? extends R>> mapper) {
        return stream(content.flatMap(mapper));
    }

    @Override
    public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
        return content.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
        return content.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
        return content.flatMapToDouble(mapper);
    }

    @Override
    public Stream<T> distinct() {
        return stream(content.distinct());
    }

    @Override
    public Stream<T> sorted() {
        return stream(content.sorted());
    }

    @Override
    public Stream<T> sorted(Comparator<? super T> comparator) {
        return stream(content.sorted(comparator));
    }

    @Override
    public Stream<T> peek(Consumer<? super T> action) {
        return stream(content.peek(action));
    }

    @Override
    public Stream<T> limit(long maxSize) {
        return stream(content.limit(maxSize));
    }

    @Override
    public Stream<T> skip(long n) {
        return stream(content.skip(n));
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        content.forEach(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super T> action) {
        content.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return content.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return content.toArray(generator);
    }

    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        return content.reduce(identity, accumulator);
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        return content.reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
        return content.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        return content.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        return content.collect(collector);
    }

    @Override
    public Optional<T> min(Comparator<? super T> comparator) {
        return content.min(comparator);
    }

    @Override
    public Optional<T> max(Comparator<? super T> comparator) {
        return content.max(comparator);
    }

    @Override
    public long count() {
        return content.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        return content.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        return content.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
        return content.noneMatch(predicate);
    }

    @Override
    public Optional<T> findFirst() {
        return content.findFirst();
    }

    @Override
    public Optional<T> findAny() {
        return content.findAny();
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return content.spliterator();
    }

    @Override
    public boolean isParallel() {
        return content.isParallel();
    }

    @Override
    public Stream<T> sequential() {
        return stream(content.sequential());
    }

    @Override
    public Stream<T> parallel() {
        return stream(content.parallel());
    }

    @Override
    public Stream<T> unordered() {
        return stream(content.unordered());
    }

    @Override
    public Stream<T> onClose(Runnable closeHandler) {
        return stream(content.onClose(closeHandler));
    }

    @Override
    public void close() {
        content.close();
    }
}
