/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.testing.Assertions.requireIllegalDefaultConstructor;

public class PathsTest {
    @UnitTest public void testIllegalConstructor() {
        requireIllegalDefaultConstructor(Paths.class);
    }
}
