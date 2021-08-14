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
package net.splitcells.gel.rating.rater;

import net.splitcells.gel.data.database.Databases;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;
import static net.splitcells.gel.rating.rater.MinimalDistance.minimalDistance;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.lineValueSelector;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.LinearDeinitializer.linearDeinitializer;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
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
        testSubjectContainer.allocate(testSubjectContainer.demandsUnused().getRawLine(0)
                , testSubjectContainer.suppliesFree().getRawLine(0));
        testSubjectContainer.allocate(testSubjectContainer.demandsUnused().getRawLine(1)
                , testSubjectContainer.suppliesFree().getRawLine(1));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(11));
        testSubjectContainer.remove(testSubjectContainer.getRawLine(1));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(1));
        testSubjectContainer.remove(testSubjectContainer.getRawLine(0));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(noCost());
        testSubjectContainer.allocate(testSubjectContainer.demandsUnused().getRawLine(0)
                , testSubjectContainer.suppliesFree().getRawLine(0));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(1));
        testSubjectContainer.allocate(testSubjectContainer.demandsUnused().getRawLine(1)
                , testSubjectContainer.suppliesFree().getRawLine(1));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(11));
    }
}
