package net.splitcells.dem.data.set.map;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

public interface MapF extends Closeable, Flushable {

    <K, V> java.util.Map<K, V> map();

    <K, V> java.util.Map<K, V> map(java.util.Map<K, V> arg);

    /**
     * Usually nothing needs to be done.
     */
    default void flush() {

    }
}
