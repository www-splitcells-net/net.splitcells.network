/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */package net.splitcells.gel.rating.rater.lib.classification;

import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.rating.rater.lib.classification.ThenAtLeastFastRater.thenAtLeastFastRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class ThenAtLeastFastRaterTest {
    @Test public void testRating() {
        val i = integerAttribute("i");
        final var testData = defineProblem("testRating")
                .withDemandAttributes(i)
                .withDemands(list(list(2)
                        , list(3) // 2nd
                        , list(1) // 3rd
                        , list(3) // 4th
                        , list(2) // 5th
                        , list(1)))
                .withSupplyAttributes()
                .withSupplies(list(list()
                        , list() // 2nd
                        , list() // 3rd
                        , list() // 4th
                        , list() // 5th
                        , list()))
                .withConstraint(r -> {
                    r.then(thenAtLeastFastRater(2, lineValueRater(line -> line.value(i).equals(1))));
                    return r;
                }).toProblem()
                .asSolution();
        testData.constraint().rating().requireEqualsTo(noCost());
        val a1 = testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        cost(2).requireVerySimilar(testData.constraint().rating());
        val a2 = testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        cost(2).requireVerySimilar(testData.constraint().rating());
        val a3 = testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        cost(1).requireVerySimilar(testData.constraint().rating());
        val a4 = testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        cost(1).requireVerySimilar(testData.constraint().rating());
        val a5 = testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        cost(1).requireVerySimilar(testData.constraint().rating());
        val a6 = testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        testData.constraint().rating().requireEqualsTo(noCost());
        testData.remove(a6);
        cost(1).requireVerySimilar(testData.constraint().rating());
        testData.remove(a5);
        cost(1).requireVerySimilar(testData.constraint().rating());
        testData.remove(a4);
        cost(1).requireVerySimilar(testData.constraint().rating());
        testData.remove(a3);
        cost(2).requireVerySimilar(testData.constraint().rating());
        testData.remove(a2);
        cost(2).requireVerySimilar(testData.constraint().rating());
        testData.remove(a1);
        testData.constraint().rating().requireEqualsTo(noCost());
    }
}
