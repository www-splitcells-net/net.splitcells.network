/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.data.set.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class ListViewI<T> implements ListView<T> {
    public static <R> ListView<R> listView(List<R> list) {
        return new ListViewI<>(list);
    }

    private final List<T> content;

    private ListViewI(List<T> content) {
        this.content = content;
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int i) {
        return content.get(i);
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
        return content.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return content.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return content.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return content.listIterator(i);
    }

    @Override
    public List<T> subList(int i, int i1) {
        throw notImplementedYet();
    }

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public boolean isEmpty() {
        return content.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return content.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }

    @Override
    public Object[] toArray() {
        return content.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return content.toArray(t1s);
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
        return content.containsAll(collection);
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
    public String toString() {
        return content.toString();
    }
}
