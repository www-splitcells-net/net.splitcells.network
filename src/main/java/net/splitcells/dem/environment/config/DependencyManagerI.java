package net.splitcells.dem.environment.config;

import net.splitcells.dem.config.IsDeterministic;
import net.splitcells.dem.config.ProgramLocalIdentity;
import net.splitcells.dem.config.ProgramRepresentative;
import net.splitcells.dem.config.StartTime;
import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.dem.resource.host.OutputPath;

import java.util.function.BiConsumer;

public class DependencyManagerI implements DependencyManager {

	private ConfigurationI config = new ConfigurationI();

	public DependencyManagerI(Class<?> programRepresentative) {
		configValue(StartTime.class);
		withConfigValue(ProgramRepresentative.class, programRepresentative);
	}

	@Override
	public <T> T configValue(Class<? extends Option<T>> key) {
		return config.configValue(key);
	}

	@Override
	public <T> Configuration withConfigValue(Class<? extends Option<T>> key, T value) {
		config.withConfigValue(key, value);
		return this;
	}

	@Override
	public <T> void subscribe(Class<? extends Option<T>> option, BiConsumer<Object, Object> consumer) {
		config.subscribe(option, consumer);
	}

	@Override
	public void init() {
		configValue(ProgramLocalIdentity.class);
		configValue(IsDeterministic.class);
		configValue(OutputPath.class);
	}

	/**
	 * HACK Remove duplicate code with close().
	 */
	@Override
	public void flush() {
		config.config_store.entrySet().forEach(entry -> {
			if (Resource.class.isAssignableFrom((Class<?>) entry.getKey())) {
				((Flushable) entry.getValue()).flush();
			}
		});
	}

	/**
	 * HACK duplicate code with flush()
	 */
	@Override
	public void close() {
		config.config_store.entrySet().forEach(entry -> {
			if (Resource.class.isAssignableFrom((Class<?>) entry.getKey())) {
				((Closeable) entry.getValue()).close();
			}
		});
	}

}
