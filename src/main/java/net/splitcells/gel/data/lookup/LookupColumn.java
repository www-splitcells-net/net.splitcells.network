package net.splitcells.gel.data.lookup;

import static net.splitcells.dem.data.set.list.Lists.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.splitcells.dem.utils.Not_implemented_yet;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;

public class LookupColumn<T> implements Column<T> {

    private final LookupTable tabula;
    private Optional<Lookup<T>> uzmeklēšana = Optional.empty();
    private final Attribute<T> atribūts;

    public static <T> LookupColumn<T> lookupColumn(LookupTable tabula, Attribute<T> atribūts) {
        return new LookupColumn<>(tabula, atribūts);
    }

    private LookupColumn(LookupTable tabula, Attribute<T> atribūts) {
        this.tabula = tabula;
        this.atribūts = atribūts;
    }

    @Override
    public int size() {
        return tabula.size();
    }

    @Override
    public boolean isEmpty() {
        throw new Not_implemented_yet();
    }

    @Override
    public boolean contains(Object o) {
        throw new Not_implemented_yet();
    }

    @Override
    public Iterator<T> iterator() {
        return values().iterator();
    }

    @Override
    public Object[] toArray() {
        throw new Not_implemented_yet();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new Not_implemented_yet();
    }

    @Override
    public boolean add(T e) {
        throw new Not_implemented_yet();
    }

    @Override
    public boolean remove(Object o) {
        throw new Not_implemented_yet();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new Not_implemented_yet();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new Not_implemented_yet();
    }

    @Override
    public boolean addAll(int indekss, Collection<? extends T> c) {
        throw new Not_implemented_yet();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new Not_implemented_yet();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new Not_implemented_yet();
    }

    @Override
    public void clear() {
        throw new Not_implemented_yet();
    }

    @Override
    public T get(int indekss) {
        // SALABOT Filtrējiet elementus, kas nav daļa no uzmeklēšanas.
        return tabula.base().columnView(atribūts).get(indekss);
    }

    @Override
    public T set(int indekss, T elements) {
        // SALABOT Vai kaut kas ir jādara?
        return elements;
    }

    @Override
    public void add(int indekss, T elements) {
        throw new Not_implemented_yet();
    }

    @Override
    public T remove(int indekss) {
        throw new Not_implemented_yet();
    }

    @Override
    public int indexOf(Object o) {
        throw new Not_implemented_yet();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new Not_implemented_yet();
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new Not_implemented_yet();
    }

    @Override
    public ListIterator<T> listIterator(int indekss) {
        throw new Not_implemented_yet();
    }

    @Override
    public List<T> subList(int noIndekss, int uzIndekss) {
        throw new Not_implemented_yet();
    }

    private void nodrošinatUzmeklēšanaInitializets() {
        if (!uzmeklēšana.isPresent()) {
            uzmeklēšana = Optional.of(Lookups.lookup(tabula, atribūts));
        }
    }

    @Override
    public Table lookup(T vertība) {
        nodrošinatUzmeklēšanaInitializets();
        return uzmeklēšana.get().lookup(vertība);
    }

    @Override
    public Table lookup(Predicate<T> predikāts) {
        nodrošinatUzmeklēšanaInitializets();
        return uzmeklēšana.get().lookup(predikāts);
    }

    @Override
    public void register_addition(Line addition) {
        uzmeklēšana.ifPresent(l -> l.register_addition(addition.value(atribūts), addition.index()));
    }

    @Override
    public void register_before_removal(Line removal) {
        uzmeklēšana.ifPresent(l -> l.register_removal(removal.value(atribūts), removal.index()));
    }

    @Override
    public net.splitcells.dem.data.set.list.List<T> values() {
        return Lists.<T>list().withAppended(
                tabula.rawLines().stream()//
                        .map(e -> e.value(atribūts))//
                        .collect(Collectors.toList())
        );
    }

}
