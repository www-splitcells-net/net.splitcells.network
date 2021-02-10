package net.splitcells.dem.execution;

public interface EventQueue<T> {
    void registerEvent(T event);
}
