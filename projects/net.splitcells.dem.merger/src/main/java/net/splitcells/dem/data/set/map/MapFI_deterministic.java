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
package net.splitcells.dem.data.set.map;

import net.splitcells.dem.lang.annotations.JavaLegacyBody;

import static net.splitcells.dem.data.set.map.MapLegacyWrapper.mapLegacyWrapper;

public final class MapFI_deterministic implements MapF {
    
    public static MapF mapFI_deterministic() {
        return new MapFI_deterministic();
    }
    
    private MapFI_deterministic() {

    }

    @JavaLegacyBody
    @Override
    public <K, V> Map<K, V> map() {
        return mapLegacyWrapper(new java.util.LinkedHashMap<K, V>(), true);
    }

    @JavaLegacyBody
    @Override
    public <K, V> Map<K, V> map(java.util.Map<K, V> arg) {
        return mapLegacyWrapper(new java.util.LinkedHashMap<K, V>(arg), true);
    }

    @Override
    public void close() {
    }

}
