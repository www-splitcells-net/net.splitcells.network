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
package net.splitcells.gel.data.table.column;

import net.splitcells.gel.data.table.Table;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class ColumnViewI<T> implements ColumnView<T> {
    public static <T> ColumnView<T> columnView(Column<T> kolonna) {
        return new ColumnViewI<>(kolonna);
    }
    private final Column<T> column;
    private ColumnViewI(Column<T> column) {
        this.column = column;
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int i) {
        return column.get(i);
    }

    @Override
    public T set(int i, T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int i, T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return column.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return column.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return column.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return column.listIterator(i);
    }

    @Override
    public List<T> subList(int i, int i1) {
        throw notImplementedYet();
    }

    @Override
    public int size() {
        return column.size();
    }

    @Override
    public boolean isEmpty() {
        return column.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return column.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return column.iterator();
    }

    @Override
    public Object[] toArray() {
        return column.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return column.toArray(t1s);
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return column.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Table lookup(T value) {
        return column.lookup(value);
    }

    @Override
    public Table lookup(Predicate<T> selector) {
        return column.lookup(selector);
    }
}
