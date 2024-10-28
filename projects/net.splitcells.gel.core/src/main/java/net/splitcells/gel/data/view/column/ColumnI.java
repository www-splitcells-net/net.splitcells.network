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
package net.splitcells.gel.data.view.column;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.lookup.Lookup;
import net.splitcells.gel.data.lookup.Lookups;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.Table;
import net.splitcells.gel.data.view.attribute.Attribute;

public class ColumnI<T> implements Column<T> {
	public static <R> Column<R> column(Table table, Attribute<R> attribute) {
		return new ColumnI<>(table, attribute);
	}

	private final List<T> content = list();
	private final Attribute<T> attribute;
	private final Table table;
	private Optional<Lookup<T>> lookup = Optional.empty();

	private ColumnI(Table table, Attribute<T> attribute) {
		this.attribute = attribute;
		this.table = table;
	}

	private Lookup<T> ensureInitializedLookup() {
		if (lookup.isEmpty()) {
			lookup = Optional.of(Lookups.lookup(table, attribute));
		}
		return lookup.get();
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
	public <R> R[] toArray(R[] a) {
		return content.toArray(a);
	}

	@Override
	public boolean add(T e) {
		return content.add(e);
	}

	@Override
	public boolean remove(Object o) {
		throw notImplementedYet();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return content.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		var contentChanged = false;
		for (T e : c) {
			contentChanged |= add(e);
		}
		return contentChanged;
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
		content.clear();
	}

	@Override
	public T get(int index) {
		return content.get(index);
	}

	@Override
	public T set(int index, T additionalElement) {
		content.set(index, additionalElement);
		return additionalElement;
	}

	@Override
	public void add(int index, T element) {
		throw notImplementedYet();
	}

	@Override
	public T remove(int index) {
		throw notImplementedYet();
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
	public ListIterator<T> listIterator(int index) {
		return content.listIterator(index);
	}

	@Override
	public List<T> subList(int startIndex, int endIndex) {
		return listWithValuesOf(content.subList(startIndex, endIndex));
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
		lookup.ifPresent(i -> i.register_addition(addition.value(attribute), addition.index()));
	}

	@Override
	public void registerBeforeRemoval(Line removal) {
		lookup.ifPresent(i -> i.register_removal(removal.value(attribute), removal.index()));
	}
}
