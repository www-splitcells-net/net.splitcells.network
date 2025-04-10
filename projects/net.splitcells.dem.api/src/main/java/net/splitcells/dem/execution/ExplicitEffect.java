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
package net.splitcells.dem.execution;

import net.splitcells.dem.resource.AspectOrientedConstructor;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>The {@link ExplicitEffect} is the base interface in order to contain all side effects via an explicit API.
 * An effect or side effect is something one can interact with,
 * but where one cannot determine the state of the effect at any given time precisely.
 * The word is a metaphor commonly used in functional programming languages and
 * is used here in a bit similar way but in the context of an OOP language.
 * </p>
 * <p>This side effect containment is also the bases to multithreading injection and
 * therefore {@link ExplicitEffect} is thread safe by default.
 * The goal of {@link ExplicitEffect} is to provide a tool in order to split up single threaded code into isolated pieces.
 * The isolated pieces use one thread a piece and are wired together in an asynchronous way,
 * without requiring explicitly asynchronous APIs.
 * This wiring mechanism allows one, to inject multithreading functionality into code,
 * that does not have explicit multithreading code via {@link AspectOrientedConstructor#withAspect(Function)}.
 * Minimizing the amount of multithreading code is an explicit goal of this interface.
 * </p>
 * <p>In single threaded code, synchronization is done implicitly the following way:
 * methods returning void are run in parallel and methods returning something are running synchronously and
 * potentially in another thread.
 * </p>
 * <p>Usage guidelines for aspects:</p>
 * <ol>
 *     <li>Use {@link #affectSynchronously(Function)}, when wrapping a method, that returns something else then this.</li>
 *     <li>Use {@link #affectSynchronously(Function)} and return the aspect,
 *     when wrapping a method, that returns this.</li>
 *     <li>Use {@link #affect(Consumer)}, when wrapping a method, that returns void.</li>
 * </ol>
 * <p>This approach has its downsides as well.
 * Currently, it seems very hard to verify,
 * if an aspect implementation of multithreading injection is correct or not.</p>
 *
 * @param <Subject>
 */
public interface ExplicitEffect<Subject> {
    /**
     * @param event The argument of the {@link Consumer} is not allowed to be pasted to code outside the
     *              {@link Consumer} scope.
     *              If this is not adhered to, the program has a race condition and is thereby incorrect.
     */
    void affect(Consumer<Subject> event);

    default <Result> Result affectSynchronously(Function<Subject, Result> synchronousEvent) {
        final var processing = Processing.<Subject, Result>processing();
        processing.withArgument(null);
        affect(i -> processing.withResult(synchronousEvent.apply(i)));
        return processing.result();
    }
}
