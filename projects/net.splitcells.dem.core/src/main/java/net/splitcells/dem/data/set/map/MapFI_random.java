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

import java.util.HashMap;

import static net.splitcells.dem.data.set.map.MapLegacyWrapper.mapLegacyWrapper;

/**
 * FIXME Randomization is currently not present.
 */
public class MapFI_random implements MapF {
    
    public static MapF mapFI_random() {
        return new MapFI_random();
    }
    
    private MapFI_random() {

    }

    @Override
    public <K, V> Map<K, V> map() {
        return mapLegacyWrapper(new HashMap<K, V>(), false);
    }

    @Override
    public <K, V> Map<K, V> map(java.util.Map<K, V> arg) {
        return mapLegacyWrapper(new HashMap<K, V>(arg), false);
    }

    @Override
    public void close() {
    }
}
