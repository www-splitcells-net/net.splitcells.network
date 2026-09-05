/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.execution;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.resource.Semaphore;

import static net.splitcells.dem.resource.Semaphore.semaphore;
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

    private final Semaphore resultWaiter = semaphore();
    private final Semaphore argumentWaiter = semaphore();
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
        argumentWaiter.acquirePermit();
        return argument;
    }

    /**
     * Informs the next thread, that calls to retrieve the {@link #result}, that the {@link #result} is available.
     *
     * @param argResult
     * @return
     */
    public Processing<Argument, Result> withResult(Result argResult) {
        result = argResult;
        resultWaiter.releasePermit();
        return this;
    }

    /**
     * Sets the {@link #argument} and waits for the {@link #result} to be made available by another thread.
     *
     * @param argArgument
     * @return
     */
    public synchronized Result processResult(Argument argArgument) {
        argument = argArgument;
        argumentWaiter.releasePermit();
        resultWaiter.acquirePermit();
        return result;
    }

    public synchronized Processing<Argument, Result> withArgument(Argument argArgument) {
        argument = argArgument;
        argumentWaiter.releasePermit();
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
        resultWaiter.acquirePermit();
        return result;
    }
}
