/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.order;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Comparators.ASCENDING_BOOLEANS;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_DOUBLES;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.list;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JavaLegacy
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
