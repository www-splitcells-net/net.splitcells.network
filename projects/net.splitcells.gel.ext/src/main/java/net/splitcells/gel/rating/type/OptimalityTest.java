/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.type;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.order.Ordering.GREATER_THAN;
import static net.splitcells.dem.data.order.Ordering.LESSER_THAN;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.gel.rating.type.Optimality.optimality;

public class OptimalityTest {
    @Test
    public void testOrder() {
        optimality(0).compare_partially_to(optimality(.1)).orElseThrow().requireEqualsTo(LESSER_THAN);
        optimality(.1).compare_partially_to(optimality(0)).orElseThrow().requireEqualsTo(GREATER_THAN);
        optimality().compare_partially_to(optimality()).orElseThrow().requireEqualsTo(EQUAL);
    }

    @Test
    public void testMaximalValue() {
        requireThrow(AssertionError.class, () -> optimality(1.000_1));
    }

    @Test
    public void testMinimalValue() {
        requireThrow(AssertionError.class, () -> optimality(-.000_1));
    }
}
