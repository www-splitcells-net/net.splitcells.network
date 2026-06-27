/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.testing.annotations.UnitTest;

import java.nio.file.Path;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Paths.removeFileSuffix;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireIllegalDefaultConstructor;

public class PathsTest {
    @UnitTest public void testIllegalConstructor() {
        requireIllegalDefaultConstructor(Paths.class);
    }

    @UnitTest public void testPath() {
        requireEquals(Paths.path("test", "a" , "b"), Path.of("test/a/b"));
        requireEquals(Paths.path(Path.of("/test"), list("a" , "b")), Path.of("/test/a/b"));
    }

    @UnitTest public void testRemoveFileSuffix() {
        requireEquals(removeFileSuffix("test.txt"), "test");
    }

    @UnitTest public void testToString() {
        requireEquals(Paths.toString(Path.of("\\a\\b")), "/a/b");
    }
}
