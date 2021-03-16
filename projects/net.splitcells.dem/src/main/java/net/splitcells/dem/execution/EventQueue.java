package net.splitcells.dem.execution;

@Deprecated
public interface EventQueue<T> {
    void registerEvent(T event);
}
