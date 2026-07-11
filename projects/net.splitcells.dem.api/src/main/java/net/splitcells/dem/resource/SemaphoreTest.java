/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.Dem;
import net.splitcells.dem.testing.annotations.IntegrationTest;
import net.splitcells.dem.testing.annotations.UnitTest;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.Dem.executeThread;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.environment.config.framework.Variable.variable;
import static net.splitcells.dem.resource.Semaphore.semaphore;

public class SemaphoreTest {
    @UnitTest public void testNoPermits() {
        val testSubject = semaphore(0);
        executeThread(getClass().getName(), () -> {
            Dem.sleepAtLeast(1000L);
            testSubject.release();
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
        val check = variable(false);
        Dem.sleepAtLeast(1_000L);
        rangeClosed(1, 6).forEach(i -> executeThread(getClass().getName(), () -> testSubject.acquire(a -> {
            Dem.sleepAtLeast(10_000L);
        })));
        executeThread(getClass().getName(), () -> testSubject.acquire(a -> {
            Dem.sleepAtLeast(10_000L);
            check.withValue(true);
        }));
        Dem.sleepAtLeast(2_000L);
        testSubject.acquire(a -> {
            // We wait, until the check is set to true.
        });
        require(check.val());
    }
}
