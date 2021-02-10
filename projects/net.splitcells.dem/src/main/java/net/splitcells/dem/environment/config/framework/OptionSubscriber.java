package net.splitcells.dem.environment.config.framework;

@FunctionalInterface
public interface OptionSubscriber<T> {
    void accept(T old_value, T new_value);
}
