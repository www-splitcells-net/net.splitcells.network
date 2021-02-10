package net.splitcells.gel.solution.history;

import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.solution.history.meta.type.AllocationRating;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.define_problem;
import static net.splitcells.gel.solution.history.History.ALLOCATION_EVENT;
import static net.splitcells.gel.solution.history.History.META_DATA;
import static net.splitcells.gel.solution.history.event.AllocationChangeType.ADDITION;
import static net.splitcells.gel.solution.history.event.AllocationChangeType.REMOVAL;
import static org.assertj.core.api.Assertions.assertThat;

public class HistoryTest {

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_reset_to_beginning() {
        final var testSubject = define_problem()
                .withDemandAttributes()
                .withDemands(list(list()))
                .withSupplyAttributes()
                .withSupplies(list(list()))
                .withConstraint(Then.then(constantRater(cost(7))))
                .toProblem()
                .asSolution();
        testSubject.allocate(
                testSubject.demands().getRawLine(0)
                , testSubject.supplies().getRawLine(0));
        testSubject.history().resetTo(-1);
        assertThat(testSubject.history().size()).isEqualTo(0);
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_reset_to_middle() {
        final var solution = define_problem()
                .withDemandAttributes()
                .withDemands
                        (list(list(), list()))
                .withSupplyAttributes()
                .withSupplies
                        (list(list(), list()))
                .withConstraint(Then.then(constantRater(cost(7))))
                .toProblem().asSolution();
        solution.allocate
                (solution.demands().getRawLine(0)
                        , solution.supplies().getRawLine(0));
        solution.allocate
                (solution.demands().getRawLine(1)
                        , solution.supplies().getRawLine(1));
        assertThat(solution.history().size()).isEqualTo(2);
        solution.history().resetTo(0);
        assertThat(solution.history().size()).isEqualTo(1);
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_subscription_of_history_to_solution() {
        final var solution = define_problem()
                .withDemandAttributes()
                .withDemands(list(list()))
                .withSupplyAttributes()
                .withSupplies(list(list()))
                .withConstraint(Then.then(constantRater(cost(7))))
                .toProblem().asSolution();
        assertThat(solution.history().size()).isEqualTo(0);
        {
            solution.allocate
                    (solution.demands().getRawLine(0)
                            , solution.supplies().getRawLine(0));
            assertThat(solution.history().size()).isEqualTo(1);
            final var additionEvent = solution.history().getRawLine(0);
            final var additionOperation = additionEvent.value(ALLOCATION_EVENT);
            assertThat(additionOperation.tips()).isEqualTo(ADDITION);
            assertThat(additionOperation.demand()).isEqualTo(solution.demands().getRawLine(0));
            assertThat(additionOperation.supply()).isEqualTo(solution.supplies().getRawLine(0));
        }
        {
            assertThat(solution.history().size()).isEqualTo(1);
            solution.remove(0);
            assertThat(solution.history().size()).isEqualTo(2);
            final var removalEvent = solution.history().getRawLine(1);
            final var removalOperation = removalEvent.value(ALLOCATION_EVENT);
            assertThat(removalOperation.tips()).isEqualTo(REMOVAL);
            assertThat(removalOperation.demand()).isEqualTo(solution.demands().getRawLine(0));
            assertThat(removalOperation.supply()).isEqualTo(solution.supplies().getRawLine(0));
        }
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void pārbaudiIeraksītuPieškiršanasNovērtejumu() {
        final var solution = define_problem()
                .withDemandAttributes()
                .withDemands(list(list()))
                .withSupplyAttributes()
                .withSupplies(list(list()))
                .withConstraint(Then.then(constantRater(cost(7))))
                .toProblem().asSolution();
        final var demandValue = solution.allocate
                (solution.demands().getRawLine(0)
                        , solution.supplies().getRawLine(0));
        assertThat
                (solution
                        .history()
                        .getRawLine(0)
                        .value(META_DATA)
                        .value(AllocationRating.class)
                        .get()
                        .value())
                .isEqualTo(cost(7));
        {
            solution.remove(demandValue);
            assertThat
                    (solution
                            .history()
                            .getRawLine(1)
                            .value(META_DATA)
                            .value(AllocationRating.class)
                            .get()
                            .value()
                    ).isEqualTo(noCost());
        }
    }
}
