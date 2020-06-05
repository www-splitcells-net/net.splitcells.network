package net.splitcells.dem.environment.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Every implementation has to have a public default constructor.
 * <p>
 * TODO Check if every option has public constructor without arguments.
 */
public interface Option<T extends Object> {
    T defaultValue();

    /**
     * BiFunction first argument is the old configuration value and the second argument is the new configuration value.
     */
    default Map<Class<? extends Option<?>>, BiFunction<Object, Object, Object>> consistencyEnforcers() {
        return new HashMap<>();
    }
}
