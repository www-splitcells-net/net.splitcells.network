/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.map.typed;

/**
 * This is a container for arbitrary typed values.
 * They can be retrieved, by a key determining the type of the value.
 * Thereby the interface is type safe.
 */
public interface TypedMapView {
    <T> T value(Class<? extends TypedKey<T>> typedKey);
}
