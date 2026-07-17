/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.object;

/**
 * This is an alternative to {@link Object#equals(Object)}.
 * It has the benefit of being typed.
 *
 * Every instance implementing this method should have consistent {@link #equalContents(Object)}
 * {@link Object#equals} and {@link Object#hashCode}.
 *
 * Here, no fish will be forced to fly and no bird will be forced to swim. - Motto of an Ottoman school?
 */
public interface Equality<T> {
    <A extends T> boolean equalContents(A arg);
}
