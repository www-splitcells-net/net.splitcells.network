package net.splitcells.dem.environment.config;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

/**
 * 
 */
public interface DependencyManager
		extends DependencyManagerV, Configuration, Closeable, Flushable {

	/**
	 * It is not allowed to be called multiple times.
	 */
	void init();

}
