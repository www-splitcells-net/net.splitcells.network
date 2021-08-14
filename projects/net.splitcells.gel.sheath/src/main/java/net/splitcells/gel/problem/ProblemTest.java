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
package net.splitcells.gel.problem;

import net.splitcells.gel.constraint.ConstraintTest;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.solution.Solution;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProblemTest {

    @Test
    void testEmptyProblem() {
        Solution solution = defineProblem()
                .withDemandAttributes()
                .withSupplyAttributes()
                .withConstraint(Then.then(constantRater(cost(7))))
                .toProblem().asSolution();
        assertTrue(solution.isComplete());
        assertTrue(solution.isEmpty());
        assertThat(solution.constraint().rating().compare_partially_to(cost(0)).get())
                .isEqualTo(EQUAL);
    }

    @Test
    void testUnbalancedProblem() {
        var defaultCost = 7;
        @SuppressWarnings("unchecked") //
        Solution solution = defineProblem()
                .withDemandAttributes()
                .withDemands(list(), list())
                .withSupplyAttributes()
                .withSupplies(list(), list(), list())
                .withConstraint(Then.then(constantRater(cost(defaultCost))))
                .toProblem().asSolution();
        assertThat(solution.isComplete()).isFalse();
        assertThat(solution.rawLinesView()).isEmpty();
        assertThat(solution.constraint().rating().compare_partially_to(noCost()).get())
                .isEqualTo(EQUAL);
        final Line firstAllocation;
        {
            firstAllocation = solution.allocate
                    (solution.demandsUnused().rawLinesView().get(0)
                            , solution.suppliesFree().rawLinesView().get(0));
            assertThat(solution.size()).isEqualTo(1);
            assertThat(solution.isComplete()).isFalse();
            assertThat(solution.constraint().rating().compare_partially_to(cost(defaultCost)).get())
                    .isEqualTo(EQUAL);
        }
        final Line secondAllocation;
        {
            secondAllocation = solution.allocate
                    (solution.demandsUnused().rawLinesView().get(1)
                            , solution.suppliesFree().rawLinesView().get(1));
            assertThat(solution.size()).isEqualTo(2);
            assertThat(solution.isComplete()).isTrue();
            assertThat(solution.constraint().rating().compare_partially_to(cost(2 * defaultCost)).get())
                    .isEqualTo(EQUAL);
        }
        {
            solution.remove(firstAllocation);
            {
                /** TODO Move these tests to {@link ConstraintTest}.
                 */
                assertThat(solution.size()).isEqualTo(1);
                assertThat(solution.demandsUnused().size()).isEqualTo(1);
                assertThat(solution.suppliesFree().size()).isEqualTo(2);
                assertThat(solution.demandsUsed().size()).isEqualTo(1);
                assertThat(solution.suppliesUsed().size()).isEqualTo(1);
            }
            assertThat(solution.isComplete()).isFalse();
            assertThat(solution.constraint().rating().compare_partially_to(cost(defaultCost)).get())
                    .isEqualTo(EQUAL);
        }
        {
            solution.remove(secondAllocation);
            assertThat(solution.isComplete()).isFalse();
            assertThat(solution.size()).isEqualTo(0);
            assertThat(solution.constraint().rating().compare_partially_to(noCost()).get())
                    .isEqualTo(EQUAL);
        }
    }

}
