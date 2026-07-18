/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.IdentifiedNameGenerator.identifiedNameGenerator;

public class IdentifiedNameGeneratorTest {
    @UnitTest
    public void testNextName() {
        requireEquals(identifiedNameGenerator().nextName(), "Ottfried Demmin");
    }

    @UnitTest
    public void testName() {
        requireEquals(identifiedNameGenerator().name(0), "Ottfried Demmin");
        requireEquals(identifiedNameGenerator().name(36), "Joshua Soller");
    }
}
