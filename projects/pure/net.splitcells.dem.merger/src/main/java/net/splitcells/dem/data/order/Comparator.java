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

import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.function.BiFunction;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public interface Comparator<T> extends java.util.Comparator<T> {

    Comparator<Integer> ASCENDING_INTEGERS = comparator((a, b) -> {
        if (a < b) {
            return Ordering.LESSER_THAN;
        } else if (b < a) {
            return Ordering.GREATER_THAN;
        } else {
            return Ordering.EQUAL;
        }
    });

    Comparator<Double> ASCENDING_DOUBLES = comparator((a, b) -> {
        if (a < b) {
            return Ordering.LESSER_THAN;
        } else if (b < a) {
            return Ordering.GREATER_THAN;
        } else {
            return Ordering.EQUAL;
        }
    });

    Comparator<Boolean> ASCENDING_BOOLEANS = comparator((a, b) -> {
        if (a && !b) {
            return Ordering.GREATER_THAN;
        } else if (!a && b) {
            return Ordering.LESSER_THAN;
        } else {
            return Ordering.EQUAL;
        }
    });

    static <T> Comparator<T> comparator(BiFunction<T, T, Ordering> comparator) {
        return new Comparator<T>() {
            @Override
            public Ordering compareTo(T a, T b) {
                return comparator.apply(a, b);
            }
        };
    }

    /**
     * Create a compatibility wrapper for {@link java.util.Comparator}.
     *
     * @param comparator Comparator Of Java Standard Library
     * @param <T>        Type of Values being Compared
     * @return Wrapped Comparator
     */
    @JavaLegacy
    static <T> Comparator<T> comparatorLegacy(BiFunction<T, T, Integer> comparator) {
        return Comparators.comparator(comparator);

    }

    default Ordering compareTo(T a, T b) {
        final int rBase = compare(a, b);
        if (rBase == 0) {
            return Ordering.EQUAL;
        } else if (rBase < 0) {
            return Ordering.LESSER_THAN;
        } else {
            if (ENFORCING_UNIT_CONSISTENCY) {
                assertThat(rBase).isGreaterThan(0);
            }
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
            if (ENFORCING_UNIT_CONSISTENCY) {
                assertThat(rBase).isEqualTo(Ordering.GREATER_THAN);
            }
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
