/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
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
