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
        if (ENFORCE_PATH_IDENTITY) {
            requireThrow(() -> testSubject.createChild("third-value", "relative", "second", "path"));
        }
    }
}
