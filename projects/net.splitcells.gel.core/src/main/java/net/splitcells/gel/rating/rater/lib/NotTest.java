/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.lib;

import lombok.val;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.Not.not;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class NotTest {
    @UnitTest
    public void test() {
        final var testData = defineProblem("test")
                .withDemandAttributes()
                .withDemands(list(list(), list(), list()))
                .withSupplyAttributes()
                .withSupplies(list(list(), list(), list()))
                .withConstraint(r -> {
                    r.then(not(hasSize(1))).then(hasSize(10));
                    return r;
                }).toProblem()
                .asSolution();
        testData.constraint().rating().requireEqualsTo(noCost());
        val first = testData.assign(testData.demandsFree().anyLine(), testData.suppliesFree().anyLine());
        testData.constraint().rating().requireEqualsTo(cost(1));
        val second = testData.assign(testData.demandsFree().anyLine(), testData.suppliesFree().anyLine());
        testData.constraint().rating().requireEqualsTo(cost(8));
        testData.remove(first);
        testData.constraint().rating().requireEqualsTo(cost(1));
        testData.remove(second);
        testData.constraint().rating().requireEqualsTo(noCost());
    }
}
