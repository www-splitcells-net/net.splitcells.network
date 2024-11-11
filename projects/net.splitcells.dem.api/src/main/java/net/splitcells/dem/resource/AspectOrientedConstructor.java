/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.data.Identifiable;
import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.util.function.Function;

/**
 * <p>This is the basis for technically very simple aspect oriented programming.
 * An aspect is basically something, that changes the behaviour of methods for a given type.
 * In this case, an aspect is implemented as an object wrapper, that wraps the original instance.
 * Thereby, the functionality can be changed or extended, without code generation or reflection.
 * For instance, such a wrapper could cache the results of method calls,
 * in order to improve the runtime performance by avoiding calling the original instance.
 * </p>
 * <p>The name of classes, that provide an aspect, should end with `Aspect`,
 * in order to hint at their main functionality,
 * if the class name does not get too long.</p>
 *
 * @param <T> This is the type to be wrapped by aspects.
 */
public interface AspectOrientedConstructor<T> {
    /**
     * <p>Registers an {@code aspect} to the this.
     * An {@code aspect} takes a given argument and returns a wrapper of it.</p>
     * <p>Keep in mind, that {@link Object#equals(Object)} only works,
     * if the base implementations and all aspects override
     * {@link Object#equals(Object)} and {@link Object#hashCode()} in such a way,
     * that contents are only compared via {@link Identifiable#identity()}.
     * The reason for this, is the fact that the base implementations and aspects,
     * may use this and give it to other objects or function calls.
     * When an object collection contains both versions of the same object, this leads to issues,
     * as equals will not return true for both.
     * Also keep in mind, that implementing {@link Object#equals(Object)} and {@link Object#hashCode()} in such a way,
     * is basically a hack and a leaky abstraction.</p>
     * <p>Sometimes the question arises, if one or multiple aspects should be created for multiple functionalities.
     * Writing wrappers can sometimes take a bit much time, if the corresponding interface is large.
     * In this case it is often easier to use one aspect for multiple features,
     * where each feature is controlled via a boolean flag.</p>
     * <p>When backwards compatibility is important:
     * When a new method with a default implementation is added to the interface of the aspect's subject,
     * the default implementation needs to be thread safe,
     * as some aspects make a subject thread safe.
     * A not thread safe default implementation would make the thread safe aspect not thread safe as well.</p>
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
