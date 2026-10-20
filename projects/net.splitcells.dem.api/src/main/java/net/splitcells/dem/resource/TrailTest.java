/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.ExecutionException;

import java.nio.file.Path;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Trail.elementCount;
import static net.splitcells.dem.resource.Trail.parentCount;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.resource.Trail.withoutPrefixElements;
import static net.splitcells.dem.resource.Trail.withoutSuffixElements;
import static net.splitcells.dem.testing.Assertions.requireThrow;

public class TrailTest {
    @UnitTest public void testParentCount() {
        Assertions.requireEquals(parentCount("../../.."), 3);
        Assertions.requireEquals(parentCount("../../../a/../../b/../c/.."), 3);
        Assertions.requireEquals(parentCount("/../../../a/../../b/../c/.."), 0);
        Assertions.requireEquals(parentCount("/a/../../../a/../../b/../c/.."), 0);
    }

    @UnitTest public void testUnixPathString() {
        Assertions.requireEquals(trail("../abc").unixPathString(), "../abc");
        Assertions.requireEquals(trail("abc").unixPathString(), "abc");
        Assertions.requireEquals(trail("a", "b", "c").unixPathString(), "a/b/c");
        Assertions.requireEquals(trail().unixPathString(), "");
        Assertions.requireEquals(trail("", "", "").unixPathString(), "");
        Assertions.requireEquals(trail("a", "", "c").unixPathString(), "a/c");
    }

    @UnitTest public void testWithoutPrefixElements() {
        Assertions.requireEquals(withoutPrefixElements("a/b/c", 0), "a/b/c");
        Assertions.requireEquals(withoutPrefixElements("a/b/c", 1), "b/c");
        Assertions.requireEquals(withoutPrefixElements("a/b/c", 2), "c");
        Assertions.requireEquals(withoutPrefixElements("a/b/c", 3), "");
        Assertions.requireEquals(withoutPrefixElements("a//////////////b//c", 0), "a/b/c");
        Assertions.requireEquals(withoutPrefixElements("../../..", 0), "../../..");
        Assertions.requireEquals(withoutPrefixElements("../../..", 1), "../..");
        Assertions.requireEquals(withoutPrefixElements("../../..", 2), "..");
        Assertions.requireEquals(withoutPrefixElements("../../..", 3), "");
    }

    @UnitTest public void testWithoutSuffixElements() {
        Assertions.requireEquals(withoutSuffixElements("a/b/c", 0), "a/b/c");
        Assertions.requireEquals(withoutSuffixElements("a/b/c", 1), "a/b");
        Assertions.requireEquals(withoutSuffixElements("a/b/c", 2), "a");
        Assertions.requireEquals(withoutSuffixElements("a/b/c", 3), "");
        Assertions.requireEquals(withoutSuffixElements("a//////////////b//c", 0), "a/b/c");
        Assertions.requireEquals(withoutSuffixElements("../../..", 0), "../../..");
        Assertions.requireEquals(withoutSuffixElements("../../..", 1), "../..");
        Assertions.requireEquals(withoutSuffixElements("../../..", 2), "..");
        Assertions.requireEquals(withoutSuffixElements("../../..", 3), "");
    }

    @UnitTest public void testWithoutPrefixElementsIllegalRemoval() {
        requireThrow(ExecutionException.class, () -> withoutPrefixElements("/../../..", 1));
        requireThrow(ExecutionException.class, () -> withoutPrefixElements("../../..", 4));
        requireThrow(ExecutionException.class, () -> withoutPrefixElements("../../..", -1));
    }

    @UnitTest public void testWithoutSuffixElementsIllegalRemoval() {
        requireThrow(ExecutionException.class, () -> withoutSuffixElements("/../../..", 1));
        requireThrow(ExecutionException.class, () -> withoutSuffixElements("../../..", 4));
        requireThrow(ExecutionException.class, () -> withoutSuffixElements("../../..", -1));
    }

    @UnitTest public void testElementCount() {
        Assertions.requireEquals(elementCount("////////"), 0);
        Assertions.requireEquals(elementCount("///a/////"), 1);
        Assertions.requireEquals(elementCount("..///a/////"), 2);
        Assertions.requireEquals(elementCount("///aaa/////"), 1);
        Assertions.requireEquals(elementCount("///a//a/a//"), 3);
    }

    @UnitTest public void testToString() {
        val testPath = "../abc";
        Assertions.requireEquals(trail(testPath).unixPathString(), testPath);
        Assertions.requireEquals(trail(testPath).toString(), testPath);
    }

    @UnitTest public void testJavaLegacyPath() {
        val testPath = "../abc";
        Assertions.requireEquals(trail(testPath).javaLegacyPath(), Path.of(testPath));
    }

    @UnitTest public void testEquals() {
        requireThrow(() -> trail("abc").equals(new Object()));
    }
}