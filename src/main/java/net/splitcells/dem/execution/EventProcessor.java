package net.splitcells.dem.execution;

public interface EventProcessor<T> {
    void processEvents();

    void registerEvent(T event);
}