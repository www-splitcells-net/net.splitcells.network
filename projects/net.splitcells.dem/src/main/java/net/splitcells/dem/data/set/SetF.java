package net.splitcells.dem.data.set;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

import java.util.Collection;

/**
 * TODO Specify expected min/average/max size
 * <p>
 * TODO Specify required performance signature.
 */
public interface SetF extends Closeable, Flushable {

    <T> Set<T> set();

    <T> Set<T> set(Collection<T> arg);

    @Deprecated
    <T> java.util.Set<T> lagacySet();

    @Deprecated
    <T> java.util.Set<T> legacySet(Collection<T> arg);

    /**
     * Most implementations won't have to do anything.
     */
    @Override
    default void close() {
        return;
    }

    /**
     * Most implementations won't have to do anything.
     */
    @Override
    default void flush() {
        return;
    }
}
