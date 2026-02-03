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

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.util.concurrent.Semaphore;

import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * A thread safe object, where one stores the values to be processed by another retriever of {@link #argument}.
 * The retriever is usually another thread.
 *
 * @param <Argument>
 * @param <Result>
 */
@JavaLegacy
public class Processing<Argument, Result> {


    public static <A, R> Processing<A, R> processing() {
        return new Processing<>();
    }

    private final Semaphore resultWaiter = new Semaphore(0, true);
    private final Semaphore argumentWaiter = new Semaphore(0, true);
    private Argument argument;
    private Result result;

    private Processing() {
    }

    /**
     * Waits until another Thread sets the {@link #argument}.
     *
     * @return Returns the arguments for the processing.
     */
    public Argument argument() {
        final Argument resultArgument;
        try {
            argumentWaiter.acquire();
            resultArgument = argument;
            return resultArgument;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw execException(e);
        }
    }

    /**
     * Informs the next thread, that calls to retrieve the {@link #result}, that the {@link #result} is available.
     *
     * @param argResult
     * @return
     */
    public Processing<Argument, Result> withResult(Result argResult) {
        result = argResult;
        resultWaiter.release();
        return this;
    }

    /**
     * Sets the {@link #argument} and waits for the {@link #result} to be made available by another thread.
     *
     * @param argArgument
     * @return
     */
    public synchronized Result processResult(Argument argArgument) {
        try {
            argument = argArgument;
            argumentWaiter.release();
            resultWaiter.acquire();
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw execException(e);
        }
    }

    public synchronized Processing<Argument, Result> withArgument(Argument argArgument) {
        argument = argArgument;
        argumentWaiter.release();
        return this;
    }

    /**
     * Waits for the {@link #result} to be made available by another thread.
     * The {@link #argument} needs to be set beforehand.
     *
     * @return
     */
    @ReturnsThis
    public synchronized Result result() {
        try {
            resultWaiter.acquire();
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw execException(e);
        }
    }
}
