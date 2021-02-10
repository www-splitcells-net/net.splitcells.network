package net.splitcells.dem.data.order;

import java.util.function.BiFunction;

// RENAME
public class Comparators<T> implements Comparator<T> {

	private final BiFunction<T, T, Integer> comparator;

	public Comparators(BiFunction<T, T, Integer> comparator) {
		this.comparator = comparator;
	}

	@Override
	public int compare(T a, T b) {
		return comparator.apply(a, b);
	}

	@Override
	public boolean equals(Object arg) {
		if (arg != null && arg instanceof Comparators) {
			return this.comparator.equals(((Comparators) arg).comparator)//
			;
		}
		return false;
	}
}
