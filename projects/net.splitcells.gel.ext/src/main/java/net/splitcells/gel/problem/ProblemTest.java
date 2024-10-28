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
package net.splitcells.gel.problem;

import net.splitcells.gel.constraint.ConstraintTest;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.solution.Solution;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Integers.requireEqualInts;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.lib.ConstantRater.constantRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;

class ProblemTest {

    @Test
    void testEmptyProblem() {
        Solution solution = defineProblem()
                .withDemandAttributes()
                .withNoDemands()
                .withSupplyAttributes()
                .withNoSupplies()
                .withConstraint(Then.then(constantRater(cost(7))))
                .toProblem().asSolution();
        require(solution.isComplete());
        require(solution.isEmpty());
        solution.constraint()
                .rating()
                .compare_partially_to(cost(0))
                .orElseThrow()
                .requireEqualsTo(EQUAL);
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
        require(!solution.isComplete());
        solution.rawLinesView().requireEmpty();
        solution.constraint()
                .rating()
                .compare_partially_to(noCost())
                .orElseThrow()
                .requireEqualsTo(EQUAL);
        final Line firstAllocation;
        {
            firstAllocation = solution.assign
                    (solution.demandsFree().rawLinesView().get(0)
                            , solution.suppliesFree().rawLinesView().get(0));
            requireEqualInts(solution.size(), 1);
            require(!solution.isComplete());
            solution.constraint()
                    .rating()
                    .compare_partially_to(cost(defaultCost))
                    .orElseThrow()
                    .requireEqualsTo(EQUAL);
        }
        final Line secondAllocation;
        {
            secondAllocation = solution.assign
                    (solution.demandsFree().rawLinesView().get(1)
                            , solution.suppliesFree().rawLinesView().get(1));
            requireEqualInts(solution.size(), 2);
            require(solution.isComplete());
            solution.constraint()
                    .rating()
                    .compare_partially_to(cost(2d * defaultCost))
                    .orElseThrow()
                    .requireEqualsTo(EQUAL);
        }
        {
            solution.remove(firstAllocation);
            {
                /** TODO Move these tests to {@link ConstraintTest}.
                 */
                requireEqualInts(solution.size(), 1);
                requireEqualInts(solution.demandsFree().size(), 1);
                requireEqualInts(solution.suppliesFree().size(), 2);
                requireEqualInts(solution.demandsUsed().size(), 1);
                requireEqualInts(solution.suppliesUsed().size(), 1);
            }
            require(!solution.isComplete());
            solution.constraint()
                    .rating()
                    .compare_partially_to(cost(defaultCost))
                    .orElseThrow()
                    .requireEqualsTo(EQUAL);
        }
        {
            solution.remove(secondAllocation);
            require(!solution.isComplete());
            requireEqualInts(solution.size(), 0);
            solution.constraint()
                    .rating()
                    .compare_partially_to(noCost())
                    .orElseThrow()
                    .requireEqualsTo(EQUAL);
        }
    }

}
