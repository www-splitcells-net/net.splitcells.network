/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.annotations.UnitTest;

import static java.lang.Integer.valueOf;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.optimization.meta.Deescalation.deescalation;

public class DeescalationTest {
    @UnitTest
    public void test() {
        final var s = attribute(Integer.class, "s");
        final var d = attribute(Integer.class, "d");
        final var testData = defineProblem("test")
                .withDemandAttributes(d)
                .withDemands(rangeClosed(1, 10).mapToObj(i -> list((Object) 1)).collect(toList()))
                .withSupplyAttributes(s)
                .withSupplies(rangeClosed(1, 10).mapToObj(i -> list((Object) 1)).collect(toList()))
                .withConstraint(c -> {
                    c.then(lineValueRater(l -> l.value(s) == -1));
                    return c;
                })
                .toProblem()
                .asSolution();
        testData.constraint().rating().equalz(noCost());
        final List<Integer> results = list();
        final var testSubject = deescalation(i -> solution -> {
                    solution.assign(solution.demandsFree().unorderedLinesStream().findFirst().orElseThrow()
                            , solution.suppliesFree().unorderedLinesStream().findFirst().orElseThrow());
                    results.add(i);
                }
                , 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        rangeClosed(1, 10).forEach(j -> testSubject.optimize(testData));
        results.requireEqualityTo(rangeClosed(0, 9).mapToObj(i -> valueOf(-i)).collect(toList()));
    }
}
