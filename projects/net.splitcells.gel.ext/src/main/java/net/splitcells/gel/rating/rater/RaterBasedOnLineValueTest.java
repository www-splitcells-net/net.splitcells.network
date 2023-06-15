/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.ConstantRater.constantRater;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.minimalDistance;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueSelector;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static org.assertj.core.api.Assertions.assertThat;

public class RaterBasedOnLineValueTest {
    @Tag(INTEGRATION_TEST)
    @Test
    public void testLineValueSelector() {
        final var attribute = attribute(Integer.class);
        final var testSubjectContainer = defineProblem()
                .withDemandAttributes(attribute)
                .withDemands(list
                        (list(1)
                                , list(2)))
                .withSupplyAttributes()
                .withSupplies(list
                        (list()
                                , list()))
                .withConstraint(r -> {
                    r.forAll(lineValueSelector(line -> 1 == line.value(attribute))).then(constantRater(cost(1)));
                    r.forAll(lineValueSelector(line -> 2 == line.value(attribute))).then(constantRater(cost(10)));
                    return r;
                })
                .toProblem()
                .asSolution();
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(noCost());
        testSubjectContainer.assign(testSubjectContainer.demandsFree().rawLine(0)
                , testSubjectContainer.suppliesFree().rawLine(0));
        testSubjectContainer.assign(testSubjectContainer.demandsFree().rawLine(1)
                , testSubjectContainer.suppliesFree().rawLine(1));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(11));
        testSubjectContainer.remove(testSubjectContainer.rawLine(1));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(1));
        testSubjectContainer.remove(testSubjectContainer.rawLine(0));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(noCost());
        testSubjectContainer.assign(testSubjectContainer.demandsFree().rawLine(0)
                , testSubjectContainer.suppliesFree().rawLine(0));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(1));
        testSubjectContainer.assign(testSubjectContainer.demandsFree().rawLine(1)
                , testSubjectContainer.suppliesFree().rawLine(1));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(11));
    }
}
