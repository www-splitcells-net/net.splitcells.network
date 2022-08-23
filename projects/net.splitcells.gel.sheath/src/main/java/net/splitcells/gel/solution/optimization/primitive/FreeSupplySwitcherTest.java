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
package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.data.table.attribute.Attribute;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class FreeSupplySwitcherTest {
    public static final Attribute<Integer> A = attribute(Integer.class, "A");
    public static final Attribute<Integer> B = attribute(Integer.class, "B");

    @Tag(INTEGRATION_TEST)
    @Test
    public void testSupplySwitchOfOneDemandWithOneUnusedSupply() {
        final var demands = 1;
        final var testSolution = defineProblem()
                .withDemandAttributes(A)
                .withDemands(
                        range(0, demands).boxed().map(i -> Lists.<Object>list(i)).collect(Lists.toList()))
                .withSupplyAttributes(B)
                .withSupplies(
                        range(0, demands + 1).boxed().map(i -> Lists.<Object>list(i)).collect(Lists.toList()))
                .withConstraint(Then.then(constantRater(cost(1))))
                .toProblem()
                .asSolution();
        {
            testSolution.optimize(linearInitialization());
            range(0, demands).forEach(i -> assertThat(testSolution.allocations().line(i).value(A)).isEqualTo(i));
            assertThat(testSolution.demandsFree().lines()).isEmpty();
            assertThat(testSolution.suppliesFree().lines()).hasSize(1);
        }
        final var randomness = mock(Randomness.class);
        doReturn(0)
                .when(randomness)
                .integer(any(), any());
        testSolution.optimizeOnce(FreeSupplySwitcher.freeSupplySwitcher(randomness, demands + 1));
        {
            range(0, demands).forEach(i -> {
                assertThat(testSolution.allocations().line(i).value(A)).isEqualTo(i);
                assertThat(testSolution.allocations().line(i).value(B)).isEqualTo(i);
            });
            assertThat(testSolution.history().size()).isEqualTo(demands + 2);
        }
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void testSupplySwitchOfOneDemandWithNoUnusedSupply() {
        final var variableCount = 1;
        final var testSolution = defineProblem()
                .withDemandAttributes(A)
                .withDemands(
                        range(0, variableCount).boxed().map(i -> Lists.<Object>list(i)).collect(Lists.toList()))
                .withSupplyAttributes(B)
                .withSupplies(
                        range(0, variableCount).boxed().map(i -> Lists.<Object>list(i)).collect(Lists.toList()))
                .withConstraint(Then.then(constantRater(cost(1))))
                .toProblem()
                .asSolution();
        {
            testSolution.optimize(linearInitialization());
            range(0, variableCount).forEach(i -> assertThat(testSolution.allocations().line(i).value(A)).isEqualTo(i));
            assertThat(testSolution.demandsFree().lines()).isEmpty();
            assertThat(testSolution.suppliesFree().lines()).isEmpty();
        }
        final var randomness = mock(Randomness.class);
        doReturn(0)
                .when(randomness)
                .integer(any(), any());
        testSolution.optimizeOnce(FreeSupplySwitcher.freeSupplySwitcher(randomness, variableCount));
        {
            range(0, variableCount).forEach(i -> {
                assertThat(testSolution.allocations().line(i).value(A)).isEqualTo(i);
                assertThat(testSolution.allocations().line(i).value(B)).isEqualTo(i);
            });
            assertThat(testSolution.history().size()).isEqualTo(variableCount);
        }
    }
}
