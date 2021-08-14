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
package net.splitcells.gel.data.lookup;

import static net.splitcells.dem.data.set.list.Lists.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.splitcells.dem.utils.NotImplementedYet;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;

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
        throw new NotImplementedYet();
    }

    @Override
    public boolean contains(Object o) {
        throw new NotImplementedYet();
    }

    @Override
    public Iterator<T> iterator() {
        return values().iterator();
    }

    @Override
    public Object[] toArray() {
        throw new NotImplementedYet();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean add(T e) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean remove(Object o) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new NotImplementedYet();
    }

    @Override
    public void clear() {
        throw new NotImplementedYet();
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
        throw new NotImplementedYet();
    }

    @Override
    public T remove(int index) {
        throw new NotImplementedYet();
    }

    @Override
    public int indexOf(Object o) {
        throw new NotImplementedYet();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new NotImplementedYet();
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new NotImplementedYet();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new NotImplementedYet();
    }

    @Override
    public List<T> subList(int startIndex, int endIndex) {
        throw new NotImplementedYet();
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
    public void register_addition(Line addition) {
        lookup.ifPresent(l -> l.register_addition(addition.value(attribute), addition.index()));
    }

    @Override
    public void register_before_removal(Line removal) {
        lookup.ifPresent(l -> l.register_removal(removal.value(attribute), removal.index()));
    }

    @Override
    public net.splitcells.dem.data.set.list.List<T> values() {
        return Lists.<T>list().withAppended(
                table.rawLines().stream()//
                        .map(e -> e.value(attribute))//
                        .collect(Collectors.toList())
        );
    }

}
