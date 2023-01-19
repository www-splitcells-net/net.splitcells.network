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
package net.splitcells.cin.raters;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.cin.raters.Alive.alive;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

public class AliveTest {
    @UnitTest
    public void testDeadOfLoneliness() {
        final var player = attribute(Integer.class, "player");
        final var time = attribute(Integer.class, "time");
        final var xCoord = attribute(Integer.class, "x-coordinate");
        final var yCoord = attribute(Integer.class, "y-coordinate");
        final var testSubject = defineProblem("testAlive")
                .withDemandAttributes(player, time, xCoord, yCoord)
                .withDemands(list
                        (list(1, 1, 1, 1)))
                .withSupplyAttributes()
                .withSupplies(list
                        (list()))
                .withConstraint(c -> {
                    c.forAll(alive(1, player, time, xCoord, yCoord));
                    return c;
                })
                .toProblem()
                .asSolution();
    }
}
