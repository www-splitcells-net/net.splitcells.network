package net.splitcells.dem.data.set.map;

import java.util.AbstractMap.SimpleImmutableEntry;

public class Pair<K, V> extends SimpleImmutableEntry<K, V> {
	private static final long serialVersionUID = 6380721809354886151L;

	private Pair(K key, V value) {
		super(key, value);
	}

	public static <A, B> Pair<A, B> pair(A key, B value) {
		return new Pair<A, B>(key, value);
	}
}