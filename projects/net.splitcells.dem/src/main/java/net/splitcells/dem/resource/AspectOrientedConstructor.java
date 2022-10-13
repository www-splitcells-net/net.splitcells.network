package net.splitcells.dem.resource;

import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.util.function.Function;

/**
 * <p>This is the basis for technically very simple aspect oriented programming.</p>
 * <p>The name of classes, that provide an aspect, should end with `Aspect`,
 * in order to hint at their main functionality,
 * if the class name does not get too long.</p>
 *
 * @param <T> This is the type to be wrapped by aspects.
 */
public interface AspectOrientedConstructor<T> {
    /**
     * Registers an {@code aspect} to the this.
     * An {@code aspect} takes a given argument and returns a wrapper of it.
     *
     * @param aspect Aspect To Be Registered
     * @return This
     */
    @ReturnsThis
    AspectOrientedConstructor<T> withAspect(Function<T, T> aspect);

    /**
     * Takes the argument, applies all aspects to it and returns the result.
     *
     * @param arg Argument To Be Wrapped
     * @return
     */
    T joinAspects(T arg);
}
