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
package net.splitcells.dem.data.order;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Comparators.ASCENDING_BOOLEANS;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_DOUBLES;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.list;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ComparisonTest {
    @Test
    public void testAscendingIntegers() {
        final var object = list(3, 1, 2);
        object.sort(ASCENDING_INTEGERS);
        assertThat(object).isEqualTo(list(1, 2, 3));
    }

    @Test
    public void testLegacyAscendingIntegers() {
        final var object = list(3, 1, 2);
        object.sort(Comparators.legacyComparator(Integer::compare));
        assertThat(object).isEqualTo(list(1, 2, 3));
    }

    @Test
    public void testAscendingDoubles() {
        final var object = list(3d, 1d, 2d);
        object.sort(ASCENDING_DOUBLES);
        assertThat(object).isEqualTo(list(1d, 2d, 3d));
    }

    @Test
    public void testAscendingBooleans() {
        final var object = list(false, true, false);
        object.sort(ASCENDING_BOOLEANS);
        assertThat(object).isEqualTo(list(false, false, true));
    }
}
