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
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.MathUtils.isEven;
import static net.splitcells.dem.utils.MathUtils.sumsForTarget;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MathUtilsTest {

    @UnitTest
    public void testSumsForTargetWithNoResult() {
        assertThat(sumsForTarget(1, list(1), 0)).isEqualTo(list());
    }

    @UnitTest
    public void testSumsForTargetWithOneResult() {
        assertThat(sumsForTarget(1, list(1))).isEqualTo(list(list(1)));
        assertThat(sumsForTarget(1, list(1), 1)).isEqualTo(list(list(1)));
    }

    /**
     * TODO Do not guarantee result order.
     */
    @UnitTest
    public void testSumsForTargetWithMultipleResults() {
        assertThat(sumsForTarget(2, list(1))).isEqualTo(list(list(1, 1)));
        assertThat(sumsForTarget(3, list(1, 2, 3)))
                .isEqualTo(list
                        (list(1, 1, 1)
                                , list(1, 2)
                                , list(2, 1)
                                , list(3)));
        assertThat(sumsForTarget(3, list(1, 2, 3), 2)).isEqualTo(list(list(1, 2), list(2, 1)));
        assertThat(sumsForTarget(3, list(0, 1, 2, 3, 4), 2)).isEqualTo(list(list(0, 3), list(1, 2), list(2, 1), list(3, 0)));
        assertThat(sumsForTarget(3, list(1, 2, 3, 4), 2)).isEqualTo(list(list(1, 2), list(2, 1)));
    }

    @UnitTest
    public void testIsEven() {
        require(isEven(0));
        require(isEven(2));
        require(isEven(4));
        require(isEven(-2));
        require(isEven(-4));
        requireNot(isEven(1));
        requireNot(isEven(-1));
        requireNot(isEven(-3));
        requireNot(isEven(-3));
    }
}
