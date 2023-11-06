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
package net.splitcells.dem.utils;

import net.splitcells.dem.testing.Assertions;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.atom.Integers.requireEqualInts;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.StringUtils.countChar;
import static net.splitcells.dem.utils.StringUtils.multiple;

public class StringUtilsTest {
    @Test
    public void testCountChar() {
        requireEqualInts(1, countChar("abcdefg", 'a'));
        requireEqualInts(2, countChar("abcdefga", 'a'));
        requireEqualInts(2, countChar("aabcdefg", 'a'));
        requireEqualInts(0, countChar("bcdefg", 'a'));
    }

    @Test
    public void testMultiple() {
        requireEquals(multiple("23", 3), "232323");
    }
}
