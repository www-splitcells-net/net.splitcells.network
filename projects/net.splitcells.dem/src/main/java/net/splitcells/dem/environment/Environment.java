package net.splitcells.dem.environment;

import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

public interface Environment
		extends EnvironmentV, Closeable, Flushable {

	/**
	 * It is not allowed to be called multiple times.
	 */
	void init();

	Configuration config();

}
