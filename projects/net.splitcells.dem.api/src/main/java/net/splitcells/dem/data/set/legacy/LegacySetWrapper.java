/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.legacy;

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.Flows;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.utils.ExecutionException;

import java.util.Optional;

@JavaLegacy
public class LegacySetWrapper<T> implements Set<T> {
    public static <R> Set<R> legacySetWrapper(java.util.Set<R> arg) {
        return new LegacySetWrapper<>(arg, Optional.empty());
    }

    public static <R> Set<R> legacySetWrapper(java.util.Set<R> arg, boolean isDeterministic) {
        return new LegacySetWrapper<>(arg, Optional.of(isDeterministic));
    }

    private final java.util.Set<T> content;
    private final Optional<Boolean> isDeterministic;

    private LegacySetWrapper(java.util.Set<T> content, Optional<Boolean> isDeterministic) {
        this.content = content;
        this.isDeterministic = isDeterministic;
    }

    public int size() {
        return this.content.size();
    }

    /**
     * This implementation speeds up the default {@link Set#remove(Object)} implementation.
     *
     * @param arg This is the object to be removed from this set, if present.
     * @return
     */
    @Override
    public boolean remove(Object arg) {
        if (!content.remove(arg)) {
            throw ExecutionException.execException("" + arg);
        }
        return true;
    }

    @SuppressWarnings("all")
    public boolean isEmpty() {
        return this.content.isEmpty();
    }

    @SuppressWarnings("all")
    public boolean contains(final Object arg0) {
        return this.content.contains(arg0);
    }

    @SuppressWarnings("all")
    public java.util.Iterator<T> iterator() {
        return this.content.iterator();
    }

    @SuppressWarnings("all")
    public Object[] toArray() {
        return this.content.toArray();
    }

    @SuppressWarnings("all")
    public <T extends Object> T[] toArray(final T[] arg0) {
        return this.content.<T>toArray(arg0);
    }

    @SuppressWarnings("all")
    public boolean add(final T arg0) {
        return this.content.add(arg0);
    }

    @Override
    public void ensureContains(T e) {
        content.add(e);
    }

    @Override
    public void ensureRemoved(T arg) {
        content.remove(arg);
    }

    @SuppressWarnings("all")
    public boolean containsAll(final java.util.Collection<?> arg0) {
        return this.content.containsAll(arg0);
    }

    @SuppressWarnings("all")
    public boolean addAll(final java.util.Collection<? extends T> arg0) {
        return this.content.addAll(arg0);
    }

    @SuppressWarnings("all")
    public boolean retainAll(final java.util.Collection<?> arg0) {
        return this.content.retainAll(arg0);
    }

    @SuppressWarnings("all")
    public boolean removeAll(final java.util.Collection<?> arg0) {
        return this.content.removeAll(arg0);
    }

    @SuppressWarnings("all")
    public void clear() {
        this.content.clear();
    }

    @SuppressWarnings("all")
    public java.util.Spliterator<T> spliterator() {
        return this.content.spliterator();
    }

    @Override
    public Optional<Boolean> _isDeterministic() {
        return isDeterministic;
    }

    @Override
    public String toString() {
        return content.toString();
    }

    @Override
    public Flow<T> stream() {
        return Flows.flow(content.stream());
    }
}
