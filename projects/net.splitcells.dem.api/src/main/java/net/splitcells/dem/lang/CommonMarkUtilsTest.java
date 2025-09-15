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
package net.splitcells.dem.lang;

import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.StringUtils;

import static net.splitcells.dem.data.atom.Integers.requireEqualInts;
import static net.splitcells.dem.lang.CommonMarkUtils.*;
import static net.splitcells.dem.testing.Assertions.requireEquals;

public class CommonMarkUtilsTest {
    @UnitTest
    public void testNewLinesAtEnd() {
        requireEqualInts(newLinesAtEnd(""), 0);
        requireEqualInts(newLinesAtEnd("1"), 0);
        requireEqualInts(newLinesAtEnd("\n2"), 0);
        requireEqualInts(newLinesAtEnd("3\n"), 1);
        requireEqualInts(newLinesAtEnd("\n4\n"), 1);
        requireEqualInts(newLinesAtEnd("5\n\n"), 2);
        requireEqualInts(newLinesAtEnd("\n6\n\n"), 2);
    }

    @UnitTest
    public void testNewLinesAtStart() {
        requireEqualInts(newLinesAtStart(""), 0);
        requireEqualInts(newLinesAtStart("1"), 0);
        requireEqualInts(newLinesAtStart("2\n"), 0);
        requireEqualInts(newLinesAtStart("\n3"), 1);
        requireEqualInts(newLinesAtStart("\n4\n"), 1);
        requireEqualInts(newLinesAtStart("\n\n5"), 2);
        requireEqualInts(newLinesAtStart("\n\n6\n"), 2);
    }

    @UnitTest
    public void tesJoinDocuments() {
        requireEquals(joinDocuments("1","2"), "1\n\n2");
        requireEquals(joinDocuments("1\n","2"), "1\n\n2");
        requireEquals(joinDocuments("1","\n2"), "1\n\n2");
        requireEquals(joinDocuments("1\n\n","\n2"), "1\n\n\n2");
        requireEquals(joinDocuments("1\n","\n\n2"), "1\n\n\n2");
        requireEquals(joinDocuments("1\n","\n2"), "1\n\n2");
    }
}
