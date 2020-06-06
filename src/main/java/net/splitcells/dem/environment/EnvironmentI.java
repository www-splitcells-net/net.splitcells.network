package net.splitcells.dem.environment;

import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.environment.config.ProgramLocalIdentity;
import net.splitcells.dem.environment.config.ProgramRepresentative;
import net.splitcells.dem.environment.config.StartTime;
import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.dem.environment.config.framework.ConfigurationI;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.dem.resource.host.OutputPath;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static net.splitcells.dem.environment.config.framework.ConfigurationI.configuration;

public class EnvironmentI implements Environment {

    private final Configuration config = configuration();

    public EnvironmentI(Class<?> programRepresentative) {
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
    public <T> void process(Class<? extends T> type, Function<T, T> processor) {
        config.process(type, processor);
    }

    @Override
    public void init() {
        configValue(ProgramLocalIdentity.class);
        configValue(IsDeterministic.class);
        configValue(OutputPath.class);
    }

    @Override
    public void flush() {
        config.process(Flushable.class, r -> {
            r.flush();
            return r;
        });
    }

    @Override
    public void close() {
        config.process(Closeable.class, r -> {
            r.close();
            return r;
        });
    }

}
