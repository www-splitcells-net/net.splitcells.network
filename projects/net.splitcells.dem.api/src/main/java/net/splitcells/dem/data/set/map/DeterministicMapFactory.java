/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.map;

import net.splitcells.dem.lang.annotations.JavaLegacy;

import static net.splitcells.dem.data.set.map.MapLegacyWrapper.mapLegacyWrapper;

public final class DeterministicMapFactory implements MapFactory {
    
    public static MapFactory deterministicMapFactory() {
        return new DeterministicMapFactory();
    }
    
    private DeterministicMapFactory() {

    }

    @JavaLegacy
    @Override
    public <K, V> Map<K, V> map() {
        return mapLegacyWrapper(new java.util.LinkedHashMap<K, V>(), true);
    }

    @JavaLegacy
    @Override
    public <K, V> Map<K, V> map(java.util.Map<K, V> arg) {
        return mapLegacyWrapper(new java.util.LinkedHashMap<K, V>(arg), true);
    }

    @Override
    public void close() {
        // Nothing needs to be done.
    }

}
