package net.splitcells.gel.data.table.column;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;

import net.splitcells.gel.data.lookup.Lookup;
import net.splitcells.gel.data.lookup.Lookups;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

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
		throw not_implemented_yet();
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
		throw not_implemented_yet();
	}

	@Override
	public T remove(int index) {
		throw not_implemented_yet();
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
		return content.subList(startIndex, endIndex);
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
		lookup.ifPresent(i -> i.register_addition(addition.value(attribute), addition.index()));
	}

	@Override
	public void register_before_removal(Line removal) {
		lookup.ifPresent(i -> i.register_removal(removal.value(attribute), removal.index()));
	}
}
