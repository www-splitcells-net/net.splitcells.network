package net.splitcells.dem.data.set.map;

import java.util.function.Supplier;

public interface Map<Key, Value> extends java.util.Map<Key, Value> {
	/**
	 * RENAME
	 * /
	default Map<Key, Value> with(Key key, Value value) {
		put(key, value);
		return this;
	}

	/**
	 * RENAME
	 */
	default Value addIfAbsent(Key key, Supplier<Value> valueSupplier) {
		Value rVal = get(key);
		if (!containsKey(key)) {
			rVal = valueSupplier.get();
			put(key, rVal);
		}
		return rVal;
	}
}
