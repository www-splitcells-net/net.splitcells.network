/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.map;

import net.splitcells.dem.lang.annotations.JavaLegacy;

import static net.splitcells.dem.data.set.map.MapLegacyWrapper.mapLegacyWrapper;

/**
 * FIXME Randomization is currently not present.
 */
public class MapFactoryRandom implements MapFactory {
    
    public static MapFactory mapFI_random() {
        return new MapFactoryRandom();
    }
    
    private MapFactoryRandom() {

    }

    @JavaLegacy
    @Override
    public <K, V> Map<K, V> map() {
        return mapLegacyWrapper(new java.util.HashMap<K, V>(), false);
    }

    @JavaLegacy
    @Override
    public <K, V> Map<K, V> map(java.util.Map<K, V> arg) {
        return mapLegacyWrapper(new java.util.HashMap<K, V>(arg), false);
    }

    @Override
    public void close() {
        // Nothing needs to be done.
    }
}
