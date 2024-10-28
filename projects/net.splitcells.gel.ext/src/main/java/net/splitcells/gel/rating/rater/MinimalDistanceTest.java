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

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.solution.optimization.StepType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.has_minimal_distance_of;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.minimalDistance;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.primitive.OfflineLinearInitialization.offlineLinearInitialization;
import static org.assertj.core.api.Assertions.assertThat;

public class MinimalDistanceTest {

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_multiple_line_addition_and_removal() {
        final var integer = integerAttribute("integer");
        final var testSubject = defineProblem()
                .withDemandAttributes()
                .withEmptyDemands(3)
                .withSupplyAttributes(integer)
                .withSupplies(list(
                        list(2)
                        , list(2)
                        , list(9)
                ))
                .withConstraint
                        (then(has_minimal_distance_of(integer, 3.0)))
                .toProblem()
                .asSolution();
        testSubject.optimize(offlineLinearInitialization());
        testSubject.optimize
                (optimizationEvent
                        (StepType.REMOVAL
                                , testSubject.demands().orderedLine(1).toLinePointer()
                                , testSubject.supplies().orderedLine(1).toLinePointer()));
        testSubject.optimize
                (optimizationEvent
                        (StepType.REMOVAL
                                , testSubject.demands().orderedLine(2).toLinePointer()
                                , testSubject.supplies().orderedLine(2).toLinePointer()));
        testSubject.optimize
                (optimizationEvent
                        (StepType.ADDITION
                                , testSubject.demands().orderedLine(1).toLinePointer()
                                , testSubject.supplies().orderedLine(2).toLinePointer()));
        testSubject.optimize
                (optimizationEvent
                        (StepType.REMOVAL
                                , testSubject.demands().orderedLine(1).toLinePointer()
                                , testSubject.supplies().orderedLine(2).toLinePointer()));

    }

    @Test
    public void testRating() {
        final var attribute = attribute(Double.class);
        final var lineSupplier = Tables.table(attribute);
        final var testSubject = then(minimalDistance(attribute, 2));
        {
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        final var testValues = Lists.list
                (lineSupplier.addTranslated(list(1.0))
                        , lineSupplier.addTranslated(list(3.0))
                        , lineSupplier.addTranslated(list(4.0))
                        , lineSupplier.addTranslated(list(1.0))
                );
        {
            testSubject.register(testValues.get(0));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.register(testValues.get(1));
            assertThat(testSubject.lines().size()).isEqualTo(2);
            assertThat(testSubject.lineProcessing().size()).isEqualTo(2);
            assertThat(testSubject.complying()).hasSize(2);
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.register(testValues.get(2));
            assertThat(testSubject.defying()).hasSize(2);
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.rating()).isEqualTo(cost(1));
        }
        {
            testSubject.register(testValues.get(3));
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.defying()).hasSize(4);
            assertThat(testSubject.rating()).isEqualTo(cost(3));
        }
        {
            testSubject.registerBeforeRemoval(testValues.get(0));
            assertThat(testSubject.defying()).hasSize(2);
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.rating()).isEqualTo(cost(1));
        }
        {
            testSubject.registerBeforeRemoval(testValues.get(1));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(2);
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.registerBeforeRemoval(testValues.get(2));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.registerBeforeRemoval(testValues.get(3));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
    }

    @Test
    public void test_simple_neighbour_defiance() {
        final var attribute = attribute(Double.class);
        final var lineProducer = Tables.table(attribute);
        final var testSubject = then(minimalDistance(attribute, 2));
        {
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        final var testValues = list
                (lineProducer.addTranslated(list(1.0))
                        , lineProducer.addTranslated(list(1.0))
                        , lineProducer.addTranslated(list(1.0))
                );
        {
            testSubject.register(testValues.get(0));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.register(testValues.get(1));
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.defying()).hasSize(2);
            assertThat(testSubject.rating()).isEqualTo(cost(2));
        }
        {
            testSubject.register(testValues.get(2));
            assertThat(testSubject.defying()).hasSize(3);
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(cost(6));
        }
        {
            testSubject.registerBeforeRemoval(testValues.get(0));
            assertThat(testSubject.defying()).hasSize(2);
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(cost(2));
        }
        {
            testSubject.registerBeforeRemoval(testValues.get(1));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.registerBeforeRemoval(testValues.get(2));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
    }
}
