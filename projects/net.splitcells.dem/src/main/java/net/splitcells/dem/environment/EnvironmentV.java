package net.splitcells.dem.environment;

import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.dem.environment.config.framework.ConfigurationV;
import net.splitcells.dem.resource.communication.Closeable;

/**
 * TODO Implement ExecutorService.
 * 
 * TODO Implement Stacking and sandboxing.
 */
public interface EnvironmentV extends Closeable {

    ConfigurationV config();
}