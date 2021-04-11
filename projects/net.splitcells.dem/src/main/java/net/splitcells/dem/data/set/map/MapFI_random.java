package net.splitcells.dem.data.set.map;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static net.splitcells.dem.data.set.map.MapLegacyWrapper.mapLegacyWrapper;

/**
 * FIXME Randomization is currently not present.
 */
public class MapFI_random implements MapF {
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
