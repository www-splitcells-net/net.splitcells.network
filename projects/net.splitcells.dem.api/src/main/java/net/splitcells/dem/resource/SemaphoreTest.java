/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.Dem;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.resource.Semaphore.semaphore;

public class SemaphoreTest {
    @UnitTest public void testNoPermits() {
        val testSubject = semaphore(0);
        Dem.executeThread(getClass().getName(), () -> {
            Dem.sleepAtLeast(1000L);
            testSubject.release();
        });
        testSubject.acquire(a -> {
            // Nothing needs to be done, we just check if this test finishes at all.
        });
    }
}
