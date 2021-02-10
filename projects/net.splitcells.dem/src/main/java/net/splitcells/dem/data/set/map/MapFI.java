package net.splitcells.dem.data.set.map;

import java.util.HashMap;
import java.util.Map;

public final class MapFI implements MapF {
	@Override
	public <K, V> Map<K, V> map() {
		return new HashMap<K, V>();
	}

	@Override
	public <K, V> Map<K, V> map(Map<K, V> content) {
		return new HashMap<K, V>(content);
	}

	@Override
	public void close() {
	}

}
