package net.splitcells.dem.environment.config;

import net.splitcells.dem.lang.annotations.Private;
import net.splitcells.dem.lang.annotations.Returns_this;

import java.util.function.BiConsumer;

public interface Configuration extends ConfigurationV {
    @Returns_this
    <T> Configuration withConfigValue(Class<? extends Option<T>> key, T value);

    <T> void subscribe(Class<? extends Option<T>> option, @Private BiConsumer<Object, Object> consumer);
}
