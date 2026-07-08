/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.testing.annotations.UnitTest;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueRater;

public class SupplySelectorsTest {
    @UnitTest public void testHillClimber() {
        final var demand = attribute(Integer.class, "demand");
        final var supply = attribute(Integer.class, "supply");
        final var testProblem = defineProblem("testHillClimber")
                .withDemandAttributes(demand)
                .withDemands(list(list(1)))
                .withSupplyAttributes(supply)
                .withSupplies(rangeClosed(1, 10).mapToObj(i -> list((Object) i)).collect(toList()))
                .withConstraint(c -> {
                    c.forAll(supply).then(lineValueRater(l -> l.value(demand).equals(5)));
                    return c;
                })
                .toProblem()
                .asSolution();
    }
}
