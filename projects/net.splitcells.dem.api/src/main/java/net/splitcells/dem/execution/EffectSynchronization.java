/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.execution;

import java.util.function.Function;

/**
 * <p>This annotation is used, in order to mark a method call as synchronization code,
 * when an object is wrapped with {@link ExplicitEffect} and {@link net.splitcells.dem.resource.AspectOrientedConstructor#withAspect(Function)}.
 * This annotation is the recommended way to mark such code for documentation purposes,
 * as some code cannot be synchronized implicitly without a none purpose function call.
 * For example, any method returning void could be executed in parallel by {@link ExplicitEffect} by default,
 * but sometimes such code needs to be run synchronously.
 * There is currently no other way to force a synchronization as to call another method,
 * that returns anything and thereby forces synchronization implicitly.</p>
 */
public @interface EffectSynchronization {
    /**
     *
     * @return The classes for which the synchronization code is present.
     */
    Class<?>[] value();
}
