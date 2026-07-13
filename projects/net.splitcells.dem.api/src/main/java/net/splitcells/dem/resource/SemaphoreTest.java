/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.Dem;
import net.splitcells.dem.testing.annotations.IntegrationTest;

import java.time.Instant;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.Dem.executeThread;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.environment.config.framework.Variable.variable;
import static net.splitcells.dem.execution.AtomicEffect.atomicEffect;
import static net.splitcells.dem.resource.Semaphore.semaphore;
import static net.splitcells.dem.testing.Assertions.requireEquals;

/**
 * TODO Test execution time of each test. Every one of these should take a minimum amount of time,
 * as otherwise this means the test did not work correctly.
 */
public class SemaphoreTest {
    @IntegrationTest public void testNoPermits() {
        val testSubject = semaphore(0);
        executeThread(getClass().getName(), () -> {
            Dem.sleepAtLeast(10_000L);
            testSubject.releasePermit();
        });
        testSubject.acquire(a -> {
            // Nothing needs to be done, we just check if this test finishes at all.
        });
    }

    @IntegrationTest public void testOnePermit() {
        val testSubject = semaphore(1);
        val check = variable(false);
        Dem.sleepAtLeast(1_000L);
        executeThread(getClass().getName(), () -> testSubject.acquire(a -> {
            Dem.sleepAtLeast(10_000L);
            check.withValue(true);
        }));
        Dem.sleepAtLeast(1_000L);
        testSubject.acquire(a -> {
            // We wait, until the check is set to true.
        });
        require(check.val());
    }

    @IntegrationTest public void testPermits() {
        val testSubject = semaphore(7);
        val check = atomicEffect(0);
        Dem.sleepAtLeast(1_000L);
        rangeClosed(1, 7).forEach(i -> executeThread(getClass().getName(), () -> testSubject.acquire(a -> {
            Dem.sleepAtLeast(10_000L);
            check.update(v -> ++v);
        })));
        // We wait 1 second, so all threads have aquired 1 permit.
        Dem.sleepAtLeast(1_000L);
        testSubject.acquire(a -> {
            // We check wether at least one thread is finished.
            require(check.value() > 0);
            // We assume, that the time between the first and last release is smaller than 2 seconds.
            Dem.sleepAtLeast(2_000L);
            // We wait, until the check is set to true by the last thread.
        });
        requireEquals(check.value(), 7);
    }
}
