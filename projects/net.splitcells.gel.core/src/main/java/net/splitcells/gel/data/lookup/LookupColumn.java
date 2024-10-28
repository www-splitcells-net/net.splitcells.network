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
package net.splitcells.gel.data.lookup;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.Table;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.Column;

public class LookupColumn<T> implements Column<T> {

    private final LookupTable table;
    private Optional<Lookup<T>> lookup = Optional.empty();
    private final Attribute<T> attribute;

    public static <T> LookupColumn<T> lookupColumn(LookupTable table, Attribute<T> attribute) {
        return new LookupColumn<>(table, attribute);
    }

    private LookupColumn(LookupTable table, Attribute<T> attribute) {
        this.table = table;
        this.attribute = attribute;
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public boolean isEmpty() {
        throw notImplementedYet();
    }

    @Override
    public boolean contains(Object o) {
        throw notImplementedYet();
    }

    @Override
    public Iterator<T> iterator() {
        return values().iterator();
    }

    @Override
    public Object[] toArray() {
        throw notImplementedYet();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw notImplementedYet();
    }

    @Override
    public boolean add(T e) {
        throw notImplementedYet();
    }

    @Override
    public boolean remove(Object o) {
        throw notImplementedYet();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw notImplementedYet();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw notImplementedYet();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw notImplementedYet();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw notImplementedYet();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw notImplementedYet();
    }

    @Override
    public void clear() {
        throw notImplementedYet();
    }

    @Override
    public T get(int index) {
        // TODO FIX Filter elements, that are not part of lookup.
        return table.base().columnView(attribute).get(index);
    }

    @Override
    public T set(int indekss, T elements) {
        // TODO FIX Has something else to be doen here?
        return elements;
    }

    @Override
    public void add(int index, T value) {
        throw notImplementedYet();
    }

    @Override
    public T remove(int index) {
        throw notImplementedYet();
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
    public List<T> subList(int startIndex, int endIndex) {
        throw notImplementedYet();
    }

    private void ensureInitializedLookup() {
        if (!lookup.isPresent()) {
            lookup = Optional.of(Lookups.lookup(table, attribute));
        }
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

    @Override
    public void registerAddition(Line addition) {
        lookup.ifPresent(l -> l.register_addition(addition.value(attribute), addition.index()));
    }

    @Override
    public void registerBeforeRemoval(Line removal) {
        lookup.ifPresent(l -> l.register_removal(removal.value(attribute), removal.index()));
    }

    @Override
    public List<T> values() {
        return table.unorderedLinesStream()
                .map(e -> e.value(attribute))//
                .collect(Lists.toList());
    }

}
