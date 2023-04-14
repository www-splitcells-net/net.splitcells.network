/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
