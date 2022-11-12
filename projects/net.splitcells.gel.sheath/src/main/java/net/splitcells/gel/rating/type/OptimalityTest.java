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
package net.splitcells.gel.rating.type;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Ordering.*;
import static net.splitcells.dem.testing.Assertions.assertThrows;
import static net.splitcells.gel.rating.type.Optimality.optimality;

public class OptimalityTest {
    @Test
    public void testOrder() {
        optimality(0).compare_partially_to(optimality(.1)).get().requireEqualsTo(LESSER_THAN);
        optimality(.1).compare_partially_to(optimality(0)).get().requireEqualsTo(GREATER_THAN);
        optimality().compare_partially_to(optimality()).get().requireEqualsTo(EQUAL);
    }

    @Test
    public void testMaximalValue() {
        assertThrows(AssertionError.class, () -> optimality(1.000_1));
    }

    @Test
    public void testMinimalValue() {
        assertThrows(AssertionError.class, () -> optimality(-.000_1));
    }
}
