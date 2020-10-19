package net.splitcells.dem.utils.reflection;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * <p>
 * This package contains all complex reflection code.
 * Complex reflection code should not be used anywhere used.
 * </p>
 * <p>
 * There some execptions:
 * <ol>
 *     <li>{@link net.splitcells.dem.environment.config.framework.Configuration}</li>
 *     <li>Return value casts of methods, that are annoted with {@link net.splitcells.dem.lang.annotations.Returns_this}</li>
 * </ol>
 */
public final class Reflection {
    private Reflection() {
        throw constructorIllegal();
    }
}
