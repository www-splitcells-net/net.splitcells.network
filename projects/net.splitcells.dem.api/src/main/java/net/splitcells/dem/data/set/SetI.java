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
package net.splitcells.dem.data.set;

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.Flows;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

@JavaLegacy
public class SetI<T> implements Set<T> {

    public static <T> Set<T> make() {
        return new SetI<>();
    }

    private final java.util.Set<T> values;

    private SetI() {
        values = new HashSet<>();
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return values.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return values.iterator();
    }

    @Override
    public Object[] toArray() {
        return values.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        throw notImplementedYet();
    }

    @Override
    public boolean add(T t) {
        return values.add(t);
    }

    @Override
    public void ensureContains(T e) {
        values.add(e);
    }

    @Override
    public void ensureRemoved(T arg) {
        values.remove(arg);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return values.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return values.addAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return values.retainAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return values.removeAll(collection);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public Flow<T> stream() {
        return Flows.flow(values.stream());
    }
}
