package net.splitcells.gel.data.uzmeklēšana;

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
import net.splitcells.gel.data.table.Rinda;
import net.splitcells.gel.data.table.Tabula;
import net.splitcells.gel.data.table.atribūts.Atribūts;
import net.splitcells.gel.data.table.kolonna.Kolonna;

public class UzmeklēšanasKolonna<T> implements Kolonna<T> {

    private final UzmeklēšanasTabula tabula;
    private Optional<Uzmeklēšana<T>> uzmeklēšana = Optional.empty();
    private final Atribūts<T> atribūts;

    public static <T> UzmeklēšanasKolonna<T> lookupColumn(UzmeklēšanasTabula tabula, Atribūts<T> atribūts) {
        return new UzmeklēšanasKolonna<>(tabula, atribūts);
    }

    private UzmeklēšanasKolonna(UzmeklēšanasTabula tabula, Atribūts<T> atribūts) {
        this.tabula = tabula;
        this.atribūts = atribūts;
    }

    @Override
    public int size() {
        return tabula.izmērs();
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
        return vertības().iterator();
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
        return tabula.base().kolonnaSkats(atribūts).get(indekss);
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
            uzmeklēšana = Optional.of(Uzmeklēšanas.uzmeklē(tabula, atribūts));
        }
    }

    @Override
    public Tabula uzmeklēšana(T vertība) {
        nodrošinatUzmeklēšanaInitializets();
        return uzmeklēšana.get().uzmeklēšana(vertība);
    }

    @Override
    public Tabula uzmeklēšana(Predicate<T> predikāts) {
        nodrošinatUzmeklēšanaInitializets();
        return uzmeklēšana.get().uzmeklēšana(predikāts);
    }

    @Override
    public void reģistrē_papildinājumi(Rinda addition) {
        uzmeklēšana.ifPresent(l -> l.reģistrē_papildinājums(addition.vērtība(atribūts), addition.indekss()));
    }

    @Override
    public void rēgistrē_pirms_noņemšanas(Rinda removal) {
        uzmeklēšana.ifPresent(l -> l.reģistē_noņemšana(removal.vērtība(atribūts), removal.indekss()));
    }

    @Override
    public net.splitcells.dem.data.set.list.List<T> vertības() {
        return Lists.<T>list().withAppended(
                tabula.jēlasRindas().stream()//
                        .map(e -> e.vērtība(atribūts))//
                        .collect(Collectors.toList())
        );
    }

}
