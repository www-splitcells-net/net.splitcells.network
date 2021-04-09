package net.splitcells.dem.data.set.map;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.environment.config.IsDeterministic;

import java.util.Optional;

public class MapFI_configured implements MapF {

    public static MapFI_configured mapFI_configured() {
        return new MapFI_configured();
    }

    private MapF mapF;

    private MapFI_configured() {
        final var isDeterministic = Dem.configValue(IsDeterministic.class);
        if (isDeterministic.isPresent() && isDeterministic.get().is_true()) {
            mapF = new MapFI_deterministic();
        } else {
            mapF = new MapFI_random();
        }
    }

    @Override
    public <K, V> Map<K, V> map() {
        return mapF.map();
    }

    @Override
    public <K, V> Map<K, V> map(java.util.Map<K, V> arg) {
        return mapF.map(arg);
    }

    @Deprecated
    private void update(Optional<Bool> oldValue, Optional<Bool> newValue) {
        if (newValue.isEmpty()) {
            mapF = new MapFI_random();
        } else if (newValue.get().is_true()) {
            mapF = new MapFI_deterministic();
        } else {
            mapF = new MapFI_random();
        }
    }

    @Override
    public void close() {
    }

}
