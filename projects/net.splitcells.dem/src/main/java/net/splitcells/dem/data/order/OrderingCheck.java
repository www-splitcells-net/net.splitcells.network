package net.splitcells.dem.data.order;

/**
 * `{@link Object#equals}` is part of the interface implicitly.
 * 
 * RENAME
 * 
 * @author splitcells
 *
 */
public interface OrderingCheck<T> {

	boolean smallerThan(T other);

	boolean smallerThanOrEqual(T other);

	boolean greaterThan(T other);

	boolean greaterThanOrEqual(T other);

	boolean equalz(T other);

}
