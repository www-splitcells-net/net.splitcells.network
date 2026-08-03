/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.primitive.repair;

import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.map.Maps;
import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.random.RandomnessViaList.randomnessViaList;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.solution.optimization.primitive.repair.SupplySelectors.hillClimber;

public class SupplySelectorsTest {
    @UnitTest public void testHillClimber() {
        val demand = attribute(Integer.class, "demand");
        val supply = attribute(Integer.class, "supply");
        val testProblem = defineProblem("testHillClimber")
                .withDemandAttributes(demand)
                .withDemands(list(list(1)))
                .withSupplyAttributes(supply)
                .withSupplies(rangeClosed(1, 10).mapToObj(i -> list((Object) i)).collect(toList()))
                .withConstraint(c -> {
                    c.then(lineValueRater(l -> l.value(supply).equals(5)));
                    return c;
                })
                .toProblem()
                .asSolution();
        val testSubjectForWorst = hillClimber(8, randomnessViaList(list(0d, 1d, 2d, 3d, 5d, 6d, 7d, 8d)));
        testSubjectForWorst.apply(Maps.<GroupId, Set<Line>>map()
                        .with(testProblem.constraint().injectionGroup()
                                , setOfUniques(testProblem.demandsFree().unorderedLines())))
                .optimize(testProblem);
        testProblem.unorderedLines().requireSizeOf(1);
        val worstAllocation = testProblem.unorderedLines().get(0);
        requireEquals(worstAllocation.values(), list(1, 1));
        testProblem.remove(worstAllocation);
        testProblem.unorderedLines().requireEmpty();
        val testSubjectForBest = hillClimber(10, randomnessViaList(list(0d, 1d, 2d, 3d, 5d, 6d, 7d, 8d, 9d, 4d)));
        testSubjectForBest.apply(Maps.<GroupId, Set<Line>>map()
                        .with(testProblem.constraint().injectionGroup()
                                , setOfUniques(testProblem.demandsFree().unorderedLines())))
                .optimize(testProblem);
        testProblem.unorderedLines().requireSizeOf(1);
        requireEquals(testProblem.unorderedLines().get(0).values(), list(1, 5));
    }
}
