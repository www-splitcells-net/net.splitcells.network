package net.splitcells.dem.data;

import net.splitcells.dem.resource.AspectOrientedConstructor;

/**
 * An {@link Identifiable} has a method, that returns the identity of the object,
 * whose {@link Object#equals(Object)} and {@link Object#hashCode()} can be used for comparing instances of {@link Identifiable}.
 * It is a replacement for {@code this}, as {@code this} is needed for aspects(see {@link AspectOrientedConstructor}) and wrappers,
 * as otherwise the {@link Object#equals(Object)} method could not be implemented in such wrappers correctly.
 */
public interface Identifiable {
    Object identity();
}
