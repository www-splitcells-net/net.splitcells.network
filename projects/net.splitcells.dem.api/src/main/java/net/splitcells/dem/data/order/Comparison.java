/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.order;

import net.splitcells.dem.lang.annotations.JavaLegacy;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Provides an easy to understand for comparing 2 values,
 * that is also type safe and easier to use than {@link java.util.Comparator}.
 * The word Comparator is not used for this class,
 * in order to avoid confusion with the name of {@link java.util.Comparator}.
 *
 * @param <T>
 */
public interface Comparison<T> extends java.util.Comparator<T> {

    default Ordering compareTo(T a, T b) {
        final int rBase = compare(a, b);
        if (rBase == 0 ) {
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

    @JavaLegacy
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
    default Comparison<T> inverted() {
        var fThis = this;
        return new Comparison<T>() {

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
