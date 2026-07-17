/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.object;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.object.Discoveries.ENFORCE_PATH_IDENTITY;
import static net.splitcells.dem.object.Discoveries.discoveryRoot;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireThrow;

public class DiscoveryTest {
    @UnitTest
    public void testChildCreationAndRemoval() {
        final var testSubject = discoveryRoot();
        final var firstChild = testSubject.createChild("test-value", "relative", "path");
        firstChild.path().content().requireEquals(list("relative", "path"));
        requireEquals(firstChild, testSubject.childByPath("relative", "path"));
        final var secondChild = testSubject.createChild("another-value", "relative", "second", "path");
        secondChild.path().content().requireEquals(list("relative", "second", "path"));
        requireEquals(secondChild, testSubject.childByPath("relative", "second", "path"));
        testSubject.childByPath("relative", "second", "path").removeChild(secondChild);
        testSubject.childByPath("relative", "second", "path").children().requireEmpty();
    }

    @UnitTest
    public void testDuplicatePaths() {
        final var testSubject = discoveryRoot();
        if (ENFORCE_PATH_IDENTITY) {
            requireThrow(() -> {
                testSubject.createChild("first", "second", "third", "path");
                testSubject.createChild("third", "second", "third", "path");
            });
            testSubject.createChild("third", "second", "third", "another-path");
        }
    }
}
