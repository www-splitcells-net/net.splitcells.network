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
package net.splitcells.gel.data.database.linebased;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.lookup.Lookup;
import net.splitcells.gel.data.lookup.Lookups;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.attribute.IndexedAttribute;
import net.splitcells.gel.data.table.column.Column;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.ExecutionException.unsupportedOperation;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.table.attribute.IndexedAttribute.indexedAttribute;

public class LineBasedColumn<T> implements Column<T> {

    public static <R> LineBasedColumn<R> lineBasedColumn(Table table, Attribute<R> attribute) {
        return new LineBasedColumn<>(table, attribute);
    }

    private final IndexedAttribute<T> attribute;
    private final Table table;
    private Optional<Lookup<T>> lookup = Optional.empty();

    private LineBasedColumn(Table table, Attribute<T> attribute) {
        this.table = table;
        this.attribute = indexedAttribute(attribute, table);

    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public boolean isEmpty() {
        return table.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return table.unorderedLinesStream().anyMatch(l -> o.equals(l.value(attribute)));
    }

    @Override
    public Iterator<T> iterator() {
        return table.orderedLinesStream().map(l -> l.value(attribute)).iterator();
    }

    @Override
    public Object[] toArray() {
        return table.orderedLinesStream().map(l -> l.value(attribute)).collect(toList()).toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return table.orderedLinesStream().map(l -> l.value(attribute)).collect(toList()).toArray(a);
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
        return table.unorderedLinesStream().map(l -> l.value(attribute)).collect(toList()).containsAll(c);
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
        return table.rawLine(index).value(attribute);
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
    public Table lookup(T value) {
        ensureInitializedLookup();
        return lookup.get().lookup(value);
    }

    @Override
    public Table lookup(Predicate<T> predicate) {
        ensureInitializedLookup();
        return lookup.get().lookup(predicate);
    }

    private Lookup<T> ensureInitializedLookup() {
        if (lookup.isEmpty()) {
            lookup = Optional.of(Lookups.lookup(table, attribute.attribute()));
        }
        return lookup.get();
    }
}
