package net.splitcells.dem.data.set.map;

import net.splitcells.dem.resource.communication.Closeable;

public interface MapF extends Closeable {

	<K, V> java.util.Map<K, V> map();

	<K, V> java.util.Map<K, V> map(java.util.Map<K, V> arg);
}
