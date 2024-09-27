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
package net.splitcells.dem.resource;

import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.ExecutionException;

import static net.splitcells.dem.resource.Trail.elementCount;
import static net.splitcells.dem.resource.Trail.parentCount;
import static net.splitcells.dem.resource.Trail.withoutPrefixElements;
import static net.splitcells.dem.resource.Trail.withoutSuffixElements;
import static net.splitcells.dem.testing.Assertions.assertThrows;
import static net.splitcells.dem.testing.Assertions.requireEquals;

public class TrailTest {
    @UnitTest
    public void testParentCount() {
        requireEquals(parentCount("../../../a/../../b/../c/.."), 3);
        requireEquals(parentCount("/../../../a/../../b/../c/.."), 0);
        requireEquals(parentCount("/a/../../../a/../../b/../c/.."), 0);
    }

    @UnitTest
    public void testWithoutPrefixElements() {
        requireEquals(withoutPrefixElements("a/b/c", 0), "a/b/c");
        requireEquals(withoutPrefixElements("a/b/c", 1), "b/c");
        requireEquals(withoutPrefixElements("a/b/c", 2), "c");
        requireEquals(withoutPrefixElements("a/b/c", 3), "");
        requireEquals(withoutPrefixElements("a//////////////b//c", 0), "a/b/c");
        requireEquals(withoutPrefixElements("../../..", 0), "../../..");
        requireEquals(withoutPrefixElements("../../..", 1), "../..");
        requireEquals(withoutPrefixElements("../../..", 2), "..");
        requireEquals(withoutPrefixElements("../../..", 3), "");
    }

    @UnitTest
    public void testWithoutSuffixElements() {
        requireEquals(withoutSuffixElements("a/b/c", 0), "a/b/c");
        requireEquals(withoutSuffixElements("a/b/c", 1), "a/b");
        requireEquals(withoutSuffixElements("a/b/c", 2), "a");
        requireEquals(withoutSuffixElements("a/b/c", 3), "");
        requireEquals(withoutSuffixElements("a//////////////b//c", 0), "a/b/c");
        requireEquals(withoutSuffixElements("../../..", 0), "../../..");
        requireEquals(withoutSuffixElements("../../..", 1), "../..");
        requireEquals(withoutSuffixElements("../../..", 2), "..");
        requireEquals(withoutSuffixElements("../../..", 3), "");
    }

    @UnitTest
    public void testWithoutPrefixElementsIllegalRemoval() {
        assertThrows(ExecutionException.class, () -> withoutPrefixElements("../../..", 4));
        assertThrows(ExecutionException.class, () -> withoutPrefixElements("../../..", -1));
    }

    @UnitTest
    public void testElementCount() {
        requireEquals(elementCount("////////"), 0);
        requireEquals(elementCount("///a/////"), 1);
        requireEquals(elementCount("..///a/////"), 2);
        requireEquals(elementCount("///aaa/////"), 1);
        requireEquals(elementCount("///a//a/a//"), 3);
    }
}