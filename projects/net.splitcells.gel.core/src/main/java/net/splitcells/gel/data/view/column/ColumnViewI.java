/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.view.column;

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.view.View;

import java.util.Collection;
import java.util.Iterator;
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
    public View persistedLookup(T value) {
        return column.persistedLookup(value);
    }

    @Override
    public View persistedLookup(Predicate<T> selector) {
        return column.persistedLookup(selector);
    }

    @Override
    public Flow<T> stream() {
        return column.stream();
    }
}
