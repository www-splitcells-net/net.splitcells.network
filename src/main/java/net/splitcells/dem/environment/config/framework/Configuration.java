package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.lang.annotations.Private;
import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.resource.communication.Closeable;
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
    default <T> Configuration with_inited_option(Class<? extends Option<T>> key) {
        try {
            return withConfigValue(key, key.getDeclaredConstructor().newInstance().defaultValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Returns_this
    <T> Configuration withConfigValue(Class<? extends Option<T>> key, T value);

    <T> void subscribe(Class<? extends Option<T>> option, @Private OptionSubscriber<Object> consumer);

    /**
     * Process a certain type of resource values.
     *
     * @param type      Type of resource values, that will be processed.
     * @param processor Function that replaces current resource values.
     * @param <T>       Type of resource values, that will be processed.
     */
    <T> void process(Class<? extends T> type, Function<T, T> processor);
}
