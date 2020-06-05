package net.splitcells.dem.environment.config;

public interface ConfigurationV {
	<T> T configValue(Class<? extends Option<T>> key);
}
