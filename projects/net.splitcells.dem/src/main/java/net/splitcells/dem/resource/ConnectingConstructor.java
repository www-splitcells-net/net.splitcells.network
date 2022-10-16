package net.splitcells.dem.resource;

import net.splitcells.dem.environment.resource.Resource;

import java.util.function.Consumer;

/**
 * This interface is intended for {@link Resource} instances, that represent factories.
 * In this case, this interface is used, in order to process newly created objects.
 * This process is called {@link #connect},
 * because the processor may subscribe to events created by the processed subject.
 *
 * @param <T> This is the type of object being listened to.
 */
public interface ConnectingConstructor<T> {
    /**
     * This adds another object creation listener.
     *
     * @param connector This is the object creation listener.
     * @return This
     */
    ConnectingConstructor withConnector(Consumer<T> connector);

    /**
     * Processes the given instances via the listeners.
     *
     * @param subject Object to be processed.
     */
    void connect(T subject);
}
