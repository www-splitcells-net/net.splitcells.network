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
package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.data.view.attribute.Attribute;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.dem.utils.random.Randomness.listBasedRandomness;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.ConstantRater.constantRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.OfflineLinearInitialization.offlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.UsedSupplySwitcher.usedSupplySwitcher;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UsedSupplySwitcherTest {
    public static final Attribute<Integer> A = attribute(Integer.class, "A");
    public static final Attribute<Integer> B = attribute(Integer.class, "B");

    @Tag(INTEGRATION_TEST)
    @Test
    public void testSwitchWithNoUnused() {
        final var testSolution = defineProblem()
                .withDemandAttributes(A)
                .withDemands(
                        list(
                                list(1)
                                , list(2)))
                .withSupplyAttributes(B)
                .withSupplies(
                        list(
                                list(1)
                                , list(2)))
                .withConstraint(Then.then(constantRater(cost(1))))
                .toProblem()
                .asSolution();
        {
            testSolution.optimize(offlineLinearInitialization());
            assertThat(testSolution.allocations().orderedLine(0).value(A)).isEqualTo(1);
            assertThat(testSolution.allocations().orderedLine(0).value(B)).isEqualTo(1);
            assertThat(testSolution.allocations().orderedLine(1).value(A)).isEqualTo(2);
            assertThat(testSolution.allocations().orderedLine(1).value(B)).isEqualTo(2);
            assertThat(testSolution.demandsFree().unorderedLines()).isEmpty();
            assertThat(testSolution.suppliesFree().unorderedLines()).isEmpty();
        }
        final var randomness = mock(Randomness.class);
        doReturn(0, 1)
                .when(randomness)
                .integer(any(), any());
        testSolution.optimizeOnce(UsedSupplySwitcher.usedSupplySwitcher(randomness));
        {
            assertThat(testSolution.allocations().orderedLine(0).value(A)).isEqualTo(1);
            assertThat(testSolution.allocations().orderedLine(0).value(B)).isEqualTo(2);
            assertThat(testSolution.allocations().orderedLine(1).value(A)).isEqualTo(2);
            assertThat(testSolution.allocations().orderedLine(1).value(B)).isEqualTo(1);
        }
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void testSwitchWithUnusedSupplyPresent() {
        final var testSolution = defineProblem()
                .withDemandAttributes(A)
                .withDemands(
                        list(
                                list(1)
                                , list(2)))
                .withSupplyAttributes(B)
                .withSupplies(
                        list(
                                list(1)
                                , list(2)
                                , list(3)))
                .withConstraint(Then.then(constantRater(cost(1))))
                .toProblem()
                .asSolution();
        {
            testSolution.optimize(offlineLinearInitialization());
            assertThat(testSolution.allocations().orderedLine(0).value(A)).isEqualTo(1);
            assertThat(testSolution.allocations().orderedLine(0).value(B)).isEqualTo(1);
            assertThat(testSolution.allocations().orderedLine(1).value(A)).isEqualTo(2);
            assertThat(testSolution.allocations().orderedLine(1).value(B)).isEqualTo(2);
            assertThat(testSolution.demandsFree().unorderedLines()).isEmpty();
            assertThat(testSolution.suppliesFree().unorderedLines()).hasSize(1);
        }
        final var randomness = mock(Randomness.class);
        doReturn(0, 1)
                .when(randomness)
                .integer(any(), any());
        testSolution.optimizeOnce(UsedSupplySwitcher.usedSupplySwitcher(randomness));
        {
            assertThat(testSolution.allocations().orderedLine(0).value(A)).isEqualTo(1);
            assertThat(testSolution.allocations().orderedLine(0).value(B)).isEqualTo(2);
            assertThat(testSolution.allocations().orderedLine(1).value(A)).isEqualTo(2);
            assertThat(testSolution.allocations().orderedLine(1).value(B)).isEqualTo(1);
        }
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void testSwitchWithUnusedDemandPresent() {
        final var testSolution = defineProblem()
                .withDemandAttributes(A)
                .withDemands(
                        list(
                                list(1)
                                , list(2)
                                , list(3)))
                .withSupplyAttributes(B)
                .withSupplies(
                        list(
                                list(1)
                                , list(2)))
                .withConstraint(Then.then(constantRater(cost(1))))
                .toProblem()
                .asSolution();
        {
            testSolution.optimize(offlineLinearInitialization());
            assertThat(testSolution.allocations().orderedLine(0).value(A)).isEqualTo(1);
            assertThat(testSolution.allocations().orderedLine(0).value(B)).isEqualTo(1);
            assertThat(testSolution.allocations().orderedLine(1).value(A)).isEqualTo(2);
            assertThat(testSolution.allocations().orderedLine(1).value(B)).isEqualTo(2);
            assertThat(testSolution.demandsFree().unorderedLines()).hasSize(1);
            assertThat(testSolution.suppliesFree().unorderedLines()).isEmpty();
        }
        final var randomness = mock(Randomness.class);
        doReturn(0, 1)
                .when(randomness)
                .integer(any(), any());
        testSolution.optimizeOnce(UsedSupplySwitcher.usedSupplySwitcher(randomness));
        {
            assertThat(testSolution.allocations().orderedLine(0).value(A)).isEqualTo(1);
            assertThat(testSolution.allocations().orderedLine(0).value(B)).isEqualTo(2);
            assertThat(testSolution.allocations().orderedLine(1).value(A)).isEqualTo(2);
            assertThat(testSolution.allocations().orderedLine(1).value(B)).isEqualTo(1);
        }
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void testSwitchMultipleStepsInOptimization() {
        final var stepCount = 2;
        final var variables = 4;
        final var testSolution = defineProblem("testSwitchMultipleStepsInOptimization")
                .withDemandAttributes(A)
                .withDemands(
                        range(0, variables).boxed().map(i -> Lists.<Object>list(i)).collect(toList()))
                .withSupplyAttributes(B)
                .withSupplies(
                        range(0, variables).boxed().map(i -> Lists.<Object>list(i)).collect(toList()))
                .withConstraint(Then.then(constantRater(cost(1))))
                .toProblem()
                .asSolution();
        testSolution.history().processWithHistory(() -> {
            {
                testSolution.optimize(offlineLinearInitialization());
                assertThat(testSolution.allocations().size()).isEqualTo(4);
                range(0, variables).forEach(i -> assertThat(testSolution.allocations().orderedLine(i).value(A)).isEqualTo(i));
                assertThat(testSolution.demandsFree().unorderedLines()).isEmpty();
                assertThat(testSolution.suppliesFree().unorderedLines()).isEmpty();
            }
            testSolution.optimizeOnce(usedSupplySwitcher(listBasedRandomness(range(0, variables).boxed().iterator()), stepCount));
            assertThat(testSolution.history().size()).isEqualTo(variables + stepCount * 4);
        });
    }
}