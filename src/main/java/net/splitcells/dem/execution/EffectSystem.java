package net.splitcells.dem.execution;

/**
 * The idea is to create an event based effect system, which can also be used in functional(=side effect free)
 * programs. Currently there is no effort being made, to make the executor of such a system explicitly
 * side effect free. It is enough to ensure that a side effect free executor can be implemented
 * and use it as an alternative executor without changing the code that uses the effect system.
 */
public interface EffectSystem<T> extends EventProcessor, EventQueue<T> {
}
