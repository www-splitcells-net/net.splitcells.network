package net.splitcells.dem.data.order;

import java.util.function.BiFunction;

public interface Comparator<T> extends java.util.Comparator<T> {

	static <T> Comparator<T> comparator(BiFunction<T, T, Ordering> comparator) {
		return new Comparator<T>() {
			@Override
			public Ordering compareTo(T a, T b) {
				return comparator.apply(a, b);
			}
		};
	}

	/**
	 * RENAME
	 * 
	 * @param comparator
	 * @return
	 */
	static <T> Comparator<T> comparator_(BiFunction<T, T, Integer> comparator) {
		return new Comparators<>(comparator);

	}

	default Ordering compareTo(T a, T b) {
		final int rBase = compare(a, b);
		if (rBase == 0) {
			return Ordering.EQUAL;
		} else if (rBase < 0) {
			return Ordering.LESSER_THAN;
		} else {
			assert rBase > 0;
			return Ordering.GREATER_THAN;
		}
	}

	@Override
	default int compare(T a, T b) {
		final Ordering rBase = compareTo(a, b);
		if (rBase == Ordering.EQUAL) {
			return 0;
		} else if (rBase == Ordering.LESSER_THAN) {
			return -1;
		} else {
			assert rBase == Ordering.GREATER_THAN;
			return 1;
		}
	}

	/**
	 * PERFORMANCE improvement of multiple inversions via public parent Comparator
	 * access method
	 */
	default Comparator<T> inverted() {
		var fThis = this;
		return new Comparator<T>() {

			@Override
			public Ordering compareTo(T a, T b) {
				return fThis.compareTo(b, a);
			}

			@Override
			public int compare(T a, T b) {
				return fThis.compare(b, a);
			}
		};
	}

}
