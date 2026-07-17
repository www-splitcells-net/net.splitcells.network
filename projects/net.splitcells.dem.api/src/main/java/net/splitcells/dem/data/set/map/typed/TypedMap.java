/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.map.typed;

import net.splitcells.dem.lang.annotations.ReturnsThis;

public interface TypedMap extends TypedMapView {
    @ReturnsThis
    <T> TypedMap withAssignment(Class<? extends TypedKey<T>> key, T value);
}
