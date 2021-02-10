package net.splitcells.dem.object;

/**
 * This is an alternative to {@link Object#equals(Object)}.
 * It has the benefit of being typed.
 *
 * Every instance implementing this method should have consistent {@link#equalCOntents}
 * {@Object#equals} and {@Object#hashCode}.
 */
public interface Equality_<T> {
    <A extends T> boolean equalContents(A arg);
}
