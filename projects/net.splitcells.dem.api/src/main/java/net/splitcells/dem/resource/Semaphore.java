/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.environment.config.framework.Variable;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.function.Consumer;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public class Semaphore {

    public static Semaphore semaphore() {
        return new Semaphore(0);
    }

    public static Semaphore semaphore(int permits) {
        return new Semaphore(permits);
    }

    private final java.util.concurrent.Semaphore impl;

    private Semaphore(int permits) {
        impl = new java.util.concurrent.Semaphore(permits, true);
    }

    public void release() {
        impl.release();
    }

    public void acquire(Consumer<AutoCloseable> requestor) {
        val lockClosed = Variable.<Boolean>create().withValue(FALSE);
        AutoCloseable lock = new AutoCloseable() {
            @Override public void close() throws Exception {
                if (lockClosed.value().orElseThrow()) {
                    throw execException(tree("Requestor requested more than one lock release.")
                            .withProperty("requestor", requestor.toString())
                            .withProperty("lock", this.toString()));
                }
                impl.release();
            }
        };
        try {
            impl.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw execException(e);
        }
        requestor.accept(lock);
        if (!lockClosed.value().orElseThrow()) {
            lockClosed.withValue(TRUE);
            impl.release();
        }
    }
}
