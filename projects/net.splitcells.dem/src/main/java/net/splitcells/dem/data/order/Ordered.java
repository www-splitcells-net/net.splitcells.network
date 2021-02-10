package net.splitcells.dem.data.order;

import static net.splitcells.dem.data.atom.BoolI.bool;

/**
 * natural order is ascending (from lower to higher)
 * <p>
 * TODO Helper Method in order to generate instances of this interface via
 * lambdas.
 *
 * @param <T>
 */
public interface Ordered<T> extends Comparable<T>, OrderingCheck<T> {
	default boolean equalz(T other) {
		return compare_to(other) == Ordering.EQUAL;
	}

	/**
	 * TODO check if compare_to is implemented for subclass (i.e. interfaces) of T.
	 * 
	 * One of the default methods have to be implemented. If this is not done this
	 * will cause an Exception because of an infinite recursion.
	 * 
	 * The relation of arg to this: this is <EQUAL|LESSER_THAN|GREATER_THANT> arg.
	 * Default order is ascending.
	 */
	default Ordering compare_to(T arg) {
		final int rBase = compareTo(arg);
		if (rBase == 0) {
			bool(equals(arg)).required();
			return Ordering.EQUAL;
		} else if (rBase < 0) {
			return Ordering.LESSER_THAN;
		} else {
			assert rBase > 0;
			return Ordering.GREATER_THAN;
		}
	}

	@Override
	default int compareTo(T arg) {
		final Ordering rBase = compare_to(arg);
		if (rBase == Ordering.EQUAL) {
			bool(equals(arg)).required();
			return 0;
		} else if (rBase == Ordering.LESSER_THAN) {
			return -1;
		} else {
			assert rBase == Ordering.GREATER_THAN;
			return 1;
		}
	}

	@Override
	default boolean smallerThan(T other) {
		return compare_to(other).equals(Ordering.LESSER_THAN);
	}

	@Override
	default boolean smallerThanOrEqual(T other) {
		var rBase = compare_to(other);
		return rBase.equals(Ordering.LESSER_THAN) || rBase.equals(Ordering.EQUAL);
	}

	@Override
	default boolean greaterThan(T other) {
		return compare_to(other).equals(Ordering.GREATER_THAN);
	}

	@Override
	default boolean greaterThanOrEqual(T other) {
		var rBase = compare_to(other);
		return rBase.equals(Ordering.GREATER_THAN) || rBase.equals(Ordering.EQUAL);
	}

}
