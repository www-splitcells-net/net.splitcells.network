/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.data.order;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Comparator.ASCENDING_BOOLEANS;
import static net.splitcells.dem.data.order.Comparator.ASCENDING_DOUBLES;
import static net.splitcells.dem.data.order.Comparator.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.list;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ComparatorTest {
    @Test
    public void testAscendingIntegers() {
        final var object = list(3, 1, 2);
        object.sort(ASCENDING_INTEGERS);
        assertThat(object).isEqualTo(list(1, 2, 3));
    }

    @Test
    public void testLegacyAscendingIntegers() {
        final var object = list(3, 1, 2);
        object.sort(Comparators.comparator(Integer::compare));
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
