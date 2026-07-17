/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.map.typed;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import static net.splitcells.dem.data.set.map.Maps.map;

@JavaLegacy
public class TypedMapI implements TypedMap {

    private final Map<Object, Object> content = map();

    public static TypedMap typedMap() {
        return new TypedMapI();
    }

    private TypedMapI() {

    }

    @Override
    public <T> T value(Class<? extends TypedKey<T>> typedKey) {
        return (T) content.get(typedKey);
    }

    @Override
    public <T> TypedMap withAssignment(Class<? extends TypedKey<T>> key, T value) {
        content.put(key, value);
        return this;
    }
}
