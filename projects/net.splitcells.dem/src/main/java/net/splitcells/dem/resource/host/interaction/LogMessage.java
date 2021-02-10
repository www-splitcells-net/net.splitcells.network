package net.splitcells.dem.resource.host.interaction;

import net.splitcells.dem.object.Discoverable;

/**
 * TODO Support log messages without or default priority.
 */
public interface LogMessage<T> extends Discoverable {
	T content();

	LogLevel priority();
}
