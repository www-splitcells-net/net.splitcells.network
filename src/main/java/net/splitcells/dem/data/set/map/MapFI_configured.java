package net.splitcells.dem.data.set.map;

import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.environment.config.DependencyManager;

import java.util.Map;
import java.util.Optional;

public class MapFI_configured implements MapF {

	private MapF mapF = new MapFI();

	public MapFI_configured(DependencyManager m) {
	}

	@Override
	public <K, V> Map<K, V> map() {
		return mapF.map();
	}

	@Override
	public <K, V> Map<K, V> map(Map<K, V> arg) {
		return mapF.map(arg);
	}

	public void update(Optional<Bool> oldValue, Optional<Bool> newValue) {
		if (newValue.isEmpty()) {
			mapF = new MapFI();
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
