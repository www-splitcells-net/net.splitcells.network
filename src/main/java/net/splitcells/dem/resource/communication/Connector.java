package net.splitcells.dem.resource.communication;

/**
 * TODO This is an unfinished concept. Maybe this needs to be removed.
 *
 * TODO Why does this extends Flushable? This could extend Closeable.
 * 
 * TODO Can be used for Receivers.
 */
@Deprecated
public interface Connector<T> extends Flushable {
	T connect(Class<?> address, Class<?>... addressModifiers);
}
