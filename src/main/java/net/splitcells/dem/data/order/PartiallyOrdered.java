package net.splitcells.dem.data.order;

import java.util.Optional;

/**
 * TODO Is equal to https://en.wikipedia.org/wiki/Partially_ordered_set
 * 
 * @author splitcells
 *
 * @param <T>
 */
@FunctionalInterface
public interface PartiallyOrdered<T> extends OrderingCheck<T> {

	Optional<Ordering> compare_partially_to(T arg);

	@Override
	default boolean smallerThan(T other) {
		var rBase = compare_partially_to(other);
		if (!rBase.isPresent()) {
			return false;
		}
		return rBase.get().equals(Ordering.LESSER_THAN);
	}

	@Override
	default boolean smallerThanOrEqual(T other) {
		var rBase = compare_partially_to(other);
		if (!rBase.isPresent()) {
			return false;
		}
		return rBase.get().equals(Ordering.LESSER_THAN) || rBase.get().equals(Ordering.EQUAL);
	}

	@Override
	default boolean greaterThan(T other) {
		var rBase = compare_partially_to(other);
		if (!rBase.isPresent()) {
			return false;
		}
		return rBase.get().equals(Ordering.GREATER_THAN);
	}

	@Override
	default boolean greaterThanOrEqual(T other) {
		var rBase = compare_partially_to(other);
		if (!rBase.isPresent()) {
			return false;
		}
		return rBase.get().equals(Ordering.GREATER_THAN) || rBase.get().equals(Ordering.EQUAL);
	}

	/**
	 * RENAME
	 * 
	 * @return
	 */
	default boolean equalz(T other) {
		var rBase = compare_partially_to(other);
		if (!rBase.isPresent()) {
			return false;
		}
		return rBase.get().equals(Ordering.EQUAL);
	}

}
