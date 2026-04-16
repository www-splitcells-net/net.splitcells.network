/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@JavaLegacy
public class Lock {

    public static Lock lock() {
        return new Lock();
    }

    private final ReentrantLock reentrantLock = new ReentrantLock(true);

    private Lock() {

    }

    public void run(Runnable run) {
        reentrantLock.lock();
        try {
            run.run();
        } finally {
            reentrantLock.unlock();
        }
    }

    public <T> T supply(Supplier<T> supplier) {
        reentrantLock.lock();
        try {
            return supplier.get();
        } finally {
            reentrantLock.unlock();
        }
    }
}
