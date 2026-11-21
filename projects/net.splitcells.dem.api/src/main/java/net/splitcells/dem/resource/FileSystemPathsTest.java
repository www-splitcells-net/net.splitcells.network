/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.resource.FileSystemPaths.isContainedPath;
import static net.splitcells.dem.resource.FileSystemPaths.requireContainedPath;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.testing.Assertions.requireThrow;

public class FileSystemPathsTest {
    @UnitTest public void testIsContainedPath() {
        require(isContainedPath(""));
        require(isContainedPath("."));
        require(isContainedPath("./"));
        require(isContainedPath("a/b"));
        require(isContainedPath("a/../b"));
        require(isContainedPath("a/.."));
        require(isContainedPath("..a/b"));
        requireNot(isContainedPath(".."));
        requireNot(isContainedPath("../a"));
        requireNot(isContainedPath("a/../../b"));
        requireNot(isContainedPath("./../a"));
        requireNot(isContainedPath("/a"));
    }

    @UnitTest public void testRequireContainedPath() {
        requireEquals(requireContainedPath("a/../b"), "a/../b");
        requireThrow(() -> requireContainedPath("../a"));
        requireThrow(() -> requireContainedPath("/etc/passwd"));
    }
}
