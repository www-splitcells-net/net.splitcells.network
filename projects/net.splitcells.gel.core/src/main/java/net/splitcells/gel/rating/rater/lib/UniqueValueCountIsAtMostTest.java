/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.lib;

import lombok.val;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.Not.not;
import static net.splitcells.gel.rating.rater.lib.UniqueValueCountIsAtMost.uniqueValueCountIsAtMost;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class UniqueValueCountIsAtMostTest {
    @UnitTest
    public void test() {
        val attribute = integerAttribute("i");
        final var testData = defineProblem("test")
                .withDemandAttributes(attribute)
                .withDemands(list(list(1)
                        , list(1) // 2nd
                        , list(2) // 3rd
                        , list(1) // 4th
                        , list(2) // 5th
                        , list(3) // 6th
                        , list(3)))
                .withSupplyAttributes()
                .withSupplies(list(list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()))
                .withConstraint(r -> {
                    r.then(uniqueValueCountIsAtMost(2, attribute));
                    return r;
                }).toProblem()
                .asSolution();
        testData.constraint().rating().requireEqualsTo(noCost());
        // first
        testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        testData.constraint().rating().requireEqualsTo(noCost());
        // second
        testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        testData.constraint().rating().requireEqualsTo(noCost());
        // third
        testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        testData.constraint().rating().requireEqualsTo(noCost());
        // fourth
        testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        testData.constraint().rating().requireEqualsTo(noCost());
        // fifth
        testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        testData.constraint().rating().requireEqualsTo(noCost());
        // sixth
        testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        testData.constraint().rating().requireVerySimilar(cost(1));
        // seventh
        testData.assign(testData.demandsFree().orderedLine(0), testData.suppliesFree().orderedLine(0));
        testData.constraint().rating().requireVerySimilar(cost(1));

    }
}
