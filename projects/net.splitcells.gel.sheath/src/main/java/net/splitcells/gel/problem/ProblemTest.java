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
                    (solution.demands_unused().rawLinesView().get(0)
                            , solution.suppliesFree().rawLinesView().get(0));
            assertThat(solution.size()).isEqualTo(1);
            assertThat(solution.isComplete()).isFalse();
            assertThat(solution.constraint().rating().compare_partially_to(cost(defaultCost)).get())
                    .isEqualTo(EQUAL);
        }
        final Line secondAllocation;
        {
            secondAllocation = solution.allocate
                    (solution.demands_unused().rawLinesView().get(1)
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
                assertThat(solution.demands_unused().size()).isEqualTo(1);
                assertThat(solution.suppliesFree().size()).isEqualTo(2);
                assertThat(solution.demands_used().size()).isEqualTo(1);
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
