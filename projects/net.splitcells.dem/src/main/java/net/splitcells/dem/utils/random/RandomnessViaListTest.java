/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils.random;

import lombok.val;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.MathUtils.requireAcceptable;
import static net.splitcells.dem.utils.random.RandomnessViaList.randomnessViaList;

public class RandomnessViaListTest {
    @UnitTest public void testInteger() {
        val testSubject = randomnessViaList(list(1d, 1.7d, 3.2d, 6d, 100d));
        requireEquals(testSubject.integer(), 1);
        requireEquals(testSubject.integer(), 2);
        requireEquals(testSubject.integer(), 3);
        requireEquals(testSubject.integer(1, 10), 6);
        requireEquals(testSubject.integer(1, 10), 1);
    }

    @UnitTest public void testFloat() {
        val testSubject = randomnessViaList(list(1d, 1.7d, 3.2d, 5d));
        requireAcceptable(testSubject.floating(1, 2), 1);
        requireAcceptable(testSubject.floating(1, 3), 1.7);
        requireAcceptable(testSubject.floating(1, 3), 1);
        requireAcceptable(testSubject.floating(1, 3), 2);
    }
}
