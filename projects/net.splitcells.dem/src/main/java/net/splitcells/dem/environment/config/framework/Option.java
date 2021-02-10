package net.splitcells.dem.environment.config.framework;

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
}
