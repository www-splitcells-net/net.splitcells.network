package net.splitcells.dem.resource.communication;

import java.util.function.Consumer;

/**
 * TODO Is this still needed?
 */
public interface Subscribable<T> {
	void subscribe(Consumer<T> consumer);

	void unsubscribe(Consumer<T> consumer);
}
