package net.splitcells.gel.kodols.dati.tabula.kolonna;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.gel.kodols.dati.uzmeklēšana.Uzmeklēšanas.uzmeklē;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;

import net.splitcells.gel.kodols.dati.uzmeklēšana.Uzmeklēšana;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;

public class KolonnaI<T> implements Kolonna<T> {
	public static <R> Kolonna<R> kolonna(Tabula tabula, Atribūts<R> atribūts) {
		return new KolonnaI<>(tabula, atribūts);
	}

	private final List<T> saturs = list();
	private final Atribūts<T> atribūts;
	private final Tabula tabula;
	private Optional<Uzmeklēšana<T>> uzmeklēšana = Optional.empty();

	private KolonnaI(Tabula tabula, Atribūts<T> atribūts) {
		this.atribūts = atribūts;
		this.tabula = tabula;
	}

	private Uzmeklēšana<T> nodrošinātUzmeklēšanasInicializēsānu() {
		if (uzmeklēšana.isEmpty()) {
			uzmeklēšana = Optional.of(uzmeklē(tabula, atribūts));
		}
		return uzmeklēšana.get();
	}

	@Override
	public int size() {
		return saturs.size();
	}

	@Override
	public boolean isEmpty() {
		return saturs.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return saturs.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return saturs.iterator();
	}

	@Override
	public Object[] toArray() {
		return saturs.toArray();
	}

	@Override
	public <R> R[] toArray(R[] a) {
		return saturs.toArray(a);
	}

	@Override
	public boolean add(T e) {
		return saturs.add(e);
	}

	@Override
	public boolean remove(Object o) {
		throw not_implemented_yet();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return saturs.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		var satursMaiņīts = false;
		for (T e : c) {
			satursMaiņīts |= add(e);
		}
		return satursMaiņīts;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw not_implemented_yet();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw not_implemented_yet();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw not_implemented_yet();
	}

	@Override
	public void clear() {
		saturs.clear();
	}

	@Override
	public T get(int indekss) {
		return saturs.get(indekss);
	}

	@Override
	public T set(int indekss, T papīlduElements) {
		saturs.set(indekss, papīlduElements);
		return papīlduElements;
	}

	@Override
	public void add(int indekss, T elements) {
		throw not_implemented_yet();
	}

	@Override
	public T remove(int indekss) {
		throw not_implemented_yet();
	}

	@Override
	public int indexOf(Object o) {
		return saturs.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return saturs.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return saturs.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return saturs.listIterator(index);
	}

	@Override
	public List<T> subList(int noIndekss, int uzIndekss) {
		return saturs.subList(noIndekss, uzIndekss);
	}

	@Override
	public Tabula uzmeklēšana(T vertība) {
		nodrošinātUzmeklēšanasInicializēsānu();
		return uzmeklēšana.get().uzmeklēšana(vertība);
	}

	@Override
	public Tabula uzmeklēšana(Predicate<T> predikāts) {
		nodrošinātUzmeklēšanasInicializēsānu();
		return uzmeklēšana.get().uzmeklēšana(predikāts);
	}

	@Override
	public void reģistrē_papildinājumi(Rinda papildinājums) {
		uzmeklēšana.ifPresent(i -> i.reģistrē_papildinājums(papildinājums.vērtība(atribūts), papildinājums.indekss()));
	}

	@Override
	public void rēgistrē_pirms_noņemšanas(Rinda noņemšana) {
		uzmeklēšana.ifPresent(i -> i.reģistē_noņemšana(noņemšana.vērtība(atribūts), noņemšana.indekss()));
	}
}
