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
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.Flows;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.Column;

public class LookupColumn<T> implements Column<T> {

    private final PersistedLookupView table;
    private Optional<Lookup<T>> lookup = Optional.empty();
    private final Attribute<T> attribute;

    public static <T> LookupColumn<T> lookupColumn(PersistedLookupView table, Attribute<T> attribute) {
        return new LookupColumn<>(table, attribute);
    }

    private LookupColumn(PersistedLookupView table, Attribute<T> attribute) {
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
        if (ENFORCING_UNIT_CONSISTENCY &&  !table.contentIndexes().contains(index)) {
            throw executionException(tree("Given index is not present in lookup table.")
                    .withProperty("index", index + "")
                    .withProperty("lookup table", table.path().toString()));
        }
        return table.base().columnView(attribute).get(index);
    }

    @Override
    public T set(int indekss, T elements) {
        throw notImplementedYet();
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
    public View persistedLookup(T value) {
        ensureInitializedLookup();
        return lookup.get().persistedLookup(value);
    }

    @Override
    public View persistedLookup(Predicate<T> predicate) {
        ensureInitializedLookup();
        return lookup.get().persistedLookup(predicate);
    }

    @Override
    public View lookup(T value) {
        ensureInitializedLookup();
        return lookup.get().lookup(value);
    }

    @Override
    public View lookup(Predicate<T> predicate) {
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

    @Override
    public Flow<T> stream() {
        final var content = table.contentIndexes();
        return Flows.flow(table.base().unorderedLinesStream()
                .filter(l -> l != null && content.has(l.index()))
                .map(l -> l.value(attribute)));
    }
}
