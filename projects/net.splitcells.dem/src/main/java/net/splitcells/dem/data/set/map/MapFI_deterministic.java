package net.splitcells.dem.data.set.map;

import java.util.LinkedHashMap;

import static net.splitcells.dem.data.set.map.MapLegacyWrapper.mapLegacyWrapper;

public final class MapFI_deterministic implements MapF {

    @Override
    public <K, V> Map<K, V> map() {
        return mapLegacyWrapper(new LinkedHashMap<K, V>(), true);
    }

    @Override
    public <K, V> Map<K, V> map(java.util.Map<K, V> arg) {
        return mapLegacyWrapper(new LinkedHashMap<K, V>(arg), true);
    }

    @Override
    public void close() {
    }

}
