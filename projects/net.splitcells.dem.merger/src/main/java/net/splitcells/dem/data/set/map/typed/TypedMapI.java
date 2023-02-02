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
package net.splitcells.dem.data.set.map.typed;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import static net.splitcells.dem.data.set.map.Maps.map;

@JavaLegacyArtifact
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
