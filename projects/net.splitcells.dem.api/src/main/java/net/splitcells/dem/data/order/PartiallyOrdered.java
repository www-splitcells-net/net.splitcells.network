/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.order;

import java.util.Optional;

/**
 * This interface represents a partially ordered set.
 *
 * @param <T> Type Of Ordered Types
 * @author splitcells
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
        return rBase.get().equals(Ordering.LESSER_THAN)
                || rBase.get().equals(Ordering.EQUAL);
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
        return rBase.get().equals(Ordering.GREATER_THAN)
                || rBase.get().equals(Ordering.EQUAL);
    }

    default boolean equalz(T other) {
        var rBase = compare_partially_to(other);
        if (!rBase.isPresent()) {
            return false;
        }
        return rBase.get().equals(Ordering.EQUAL);
    }

}
