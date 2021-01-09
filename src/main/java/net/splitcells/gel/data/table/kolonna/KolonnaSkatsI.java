package net.splitcells.gel.data.table.kolonna;

import net.splitcells.gel.data.table.Tabula;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

public class KolonnaSkatsI<T> implements KolonnaSkats<T> {
    public static <T> KolonnaSkats<T> kolonnasSkats(Kolonna<T> kolonna) {
        return new KolonnaSkatsI<>(kolonna);
    }
    private final Kolonna<T> kolonna;
    private KolonnaSkatsI(Kolonna<T> kolonna) {
        this.kolonna = kolonna;
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int i) {
        return kolonna.get(i);
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
        return kolonna.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return kolonna.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return kolonna.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return kolonna.listIterator(i);
    }

    @Override
    public List<T> subList(int i, int i1) {
        throw not_implemented_yet();
    }

    @Override
    public int size() {
        return kolonna.size();
    }

    @Override
    public boolean isEmpty() {
        return kolonna.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return kolonna.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return kolonna.iterator();
    }

    @Override
    public Object[] toArray() {
        return kolonna.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return kolonna.toArray(t1s);
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
        return kolonna.containsAll(collection);
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
    public Tabula uzmeklēšana(T vertība) {
        return kolonna.uzmeklēšana(vertība);
    }

    @Override
    public Tabula uzmeklēšana(Predicate<T> predikāts) {
        return kolonna.uzmeklēšana(predikāts);
    }
}
