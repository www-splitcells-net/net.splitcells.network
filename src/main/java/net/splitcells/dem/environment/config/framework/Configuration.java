package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.lang.annotations.Private;
import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.resource.communication.Flushable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * TODO Split up into generic dynamically typed and type safe table and this configuration class.
 */
public interface Configuration extends ConfigurationV {
    @Returns_this
    <T> Configuration withConfigValue(Class<? extends Option<T>> key, T value);

    <T> void subscribe(Class<? extends Option<T>> option, @Private BiConsumer<Object, Object> consumer);

    <T> void process(Class<? extends T> type, Function<T, T> processor);
}
