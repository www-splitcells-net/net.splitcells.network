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
package net.splitcells.gel.data.table.linebased;

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.lookup.Lookup;
import net.splitcells.gel.data.lookup.Lookups;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.attribute.IndexedAttribute;
import net.splitcells.gel.data.view.column.Column;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.ExecutionException.unsupportedOperation;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.view.attribute.IndexedAttribute.indexedAttribute;

public class LineBasedColumn<T> implements Column<T> {

    public static <R> LineBasedColumn<R> lineBasedColumn(View view, Attribute<R> attribute) {
        return new LineBasedColumn<>(view, attribute);
    }

    private final IndexedAttribute<T> attribute;
    private final View view;
    private Optional<Lookup<T>> lookup = Optional.empty();

    private LineBasedColumn(View view, Attribute<T> attribute) {
        this.view = view;
        this.attribute = indexedAttribute(attribute, view);

    }

    @Override
    public int size() {
        return view.size();
    }

    @Override
    public boolean isEmpty() {
        return view.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return view.unorderedLinesStream().anyMatch(l -> o.equals(l.value(attribute)));
    }

    @Override
    public Iterator<T> iterator() {
        return view.orderedLinesStream().map(l -> l.value(attribute)).iterator();
    }

    @Override
    public Object[] toArray() {
        return view.orderedLinesStream().map(l -> l.value(attribute)).collect(toList()).toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return view.orderedLinesStream().map(l -> l.value(attribute)).collect(toList()).toArray(a);
    }

    @Override
    public boolean add(T t) {
        throw unsupportedOperation();
    }

    @Override
    public boolean remove(Object o) {
        throw unsupportedOperation();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return view.unorderedLinesStream().map(l -> l.value(attribute)).collect(toList()).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw unsupportedOperation();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw unsupportedOperation();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw unsupportedOperation();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw unsupportedOperation();
    }

    @Override
    public void clear() {
        throw unsupportedOperation();
    }

    @Override
    public T get(int index) {
        return view.rawLine(index).value(attribute);
    }

    @Override
    public T set(int index, T element) {
        throw unsupportedOperation();
    }

    @Override
    public void add(int index, T element) {
        throw unsupportedOperation();
    }

    @Override
    public T remove(int index) {
        throw unsupportedOperation();
    }

    @Override
    public int indexOf(Object o) {
        throw notImplementedYet();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw notImplementedYet();
    }

    @Override
    public ListIterator<T> listIterator() {
        throw notImplementedYet();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw notImplementedYet();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw notImplementedYet();
    }

    @Override
    public void registerAddition(Line addition) {
        lookup.ifPresent(i -> i.register_addition(addition.value(attribute), addition.index()));
    }

    @Override
    public void registerBeforeRemoval(Line removal) {
        lookup.ifPresent(i -> i.register_removal(removal.value(attribute), removal.index()));
    }

    @Override
    public View persistedLookup(T value) {
        ensureInitializedLookup();
        return lookup.get().persistedLookup(value);
    }

    @Override
    public View persistedLookup(Predicate<T> predicate) {
        ensureInitializedLookup();
        return lookup.get().persistedLookup(predicate);
    }

    private Lookup<T> ensureInitializedLookup() {
        if (lookup.isEmpty()) {
            lookup = Optional.of(Lookups.persistedLookup(view, attribute.attribute()));
        }
        return lookup.get();
    }

    @Override
    public Flow<T> stream() {
        throw notImplementedYet();
    }
}
