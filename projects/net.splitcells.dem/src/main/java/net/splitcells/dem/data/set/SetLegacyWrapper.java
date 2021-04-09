package net.splitcells.dem.data.set;

import java.util.HashSet;
import java.util.Optional;

public class SetLegacyWrapper<T> implements Set<T> {
    public static <R> Set<R> setLegacyWrapper(java.util.Set<R> arg) {
        return new SetLegacyWrapper<R>(arg, Optional.empty());
    }

    public static <R> Set<R> setLegacyWrapper(java.util.Set<R> arg, boolean isDeterministic) {
        return new SetLegacyWrapper<R>(arg, Optional.of(isDeterministic));
    }

    public static <R> Set<R> setLegacyWrapper() {
        return setLegacyWrapper(new HashSet<>());
    }

    private final java.util.Set<T> content;
    private final Optional<Boolean> isDeterministic;

    private SetLegacyWrapper(java.util.Set<T> content, Optional<Boolean> isDeterministic) {
        this.content = content;
        this.isDeterministic = isDeterministic;
    }

    public int size() {
        return this.content.size();
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

    @SuppressWarnings("all")
    public boolean remove(final Object arg0) {
        return this.content.remove(arg0);
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
}
