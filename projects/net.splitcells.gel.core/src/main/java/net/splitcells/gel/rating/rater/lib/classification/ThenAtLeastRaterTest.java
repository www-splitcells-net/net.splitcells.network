/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.lib.classification;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.constraint.Constraint.*;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.rating.rater.lib.UniqueValueCountIsAtMost.uniqueValueCountIsAtMost;
import static net.splitcells.gel.rating.rater.lib.classification.ThenAtLeastRater.thenAtLeastRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class ThenAtLeastRaterTest {

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
                    r.then(thenAtLeastRater(2, lineValueRater(line -> line.value(i).equals(1))));
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
