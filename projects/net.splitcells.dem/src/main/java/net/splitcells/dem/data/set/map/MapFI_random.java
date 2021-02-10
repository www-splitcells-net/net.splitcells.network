package net.splitcells.dem.data.set.map;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * FIXME Randomization is currently not present.
 */
public class MapFI_random implements MapF {
	@Override
	public <K, V> Map<K, V> map() {
		return new LinkedHashMap<K, V>();
	}

	@Override
	public <K, V> Map<K, V> map(Map<K, V> arg) {
		return new LinkedHashMap<K, V>(arg);
	}

	@Override
	public void close() {
	}
}
