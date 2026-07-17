/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.order;

/**
 * Provides functions in order to check the ordering of this and other values.
 * `{@link Object#equals}` is part of the interface implicitly.
 *
 * @author splitcells
 */
public interface OrderingCheck<T> {

    boolean smallerThan(T other);

    boolean smallerThanOrEqual(T other);

    boolean greaterThan(T other);

    boolean greaterThanOrEqual(T other);

    boolean equalz(T other);

}
