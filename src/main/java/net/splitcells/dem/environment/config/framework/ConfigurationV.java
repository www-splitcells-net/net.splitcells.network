package net.splitcells.dem.environment.config.framework;

public interface ConfigurationV {
	<T> T configValue(Class<? extends Option<T>> key);
}
