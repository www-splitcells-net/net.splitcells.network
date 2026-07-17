/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history;

import net.splitcells.dem.testing.annotations.BenchmarkTest;
import net.splitcells.dem.testing.annotations.IntegrationTest;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.data.table.history.TableEventType;
import net.splitcells.gel.solution.history.meta.type.AllocationRating;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.tree.XmlConfig.xmlConfig;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.TestTypes.BENCHMARK_RUNTIME;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.rating.rater.lib.ConstantRater.constantRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.history.History.*;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;

public class HistoryTest {


    @UnitTest
    public void testToAnalysisFods() {
        final var testSubject = defineProblem("testToAnalysisFods")
                .withDemandAttributes()
                .withDemands(rangeClosed(1, 10).mapToObj(i -> list()).collect(toList()))
                .withSupplyAttributes()
                .withSupplies(rangeClosed(1, 10).mapToObj(i -> list()).collect(toList()))
                .withConstraint(then(constantRater(cost(7))))
                .toProblem()
                .asSolution();
        testSubject.history().processWithHistory(() -> {
            testSubject.optimize(onlineLinearInitialization());
        });
        testSubject.history().toAnalysisFods().toXmlString(xmlConfig());
    }

    @BenchmarkTest
    public void test_rest_to_beginning_runtime() {
        final var testSubject = defineProblem("test_rest_to_beginning_runtime")
                .withDemandAttributes()
                .withDemands(rangeClosed(1, 1000).mapToObj(i -> list()).collect(toList()))
                .withSupplyAttributes()
                .withSupplies(rangeClosed(1, 1000).mapToObj(i -> list()).collect(toList()))
                .withConstraint(then(constantRater(cost(7))))
                .toProblem()
                .asSolution();
        testSubject.history().processWithHistory(() -> {
            testSubject.optimize(onlineLinearInitialization());
            testSubject.history().resetTo(0);
        });
    }

    @IntegrationTest
    public void test_reset_to_beginning() {
        final var testSubject = defineProblem("test_reset_to_beginning")
                .withDemandAttributes()
                .withDemands(list(list()))
                .withSupplyAttributes()
                .withSupplies(list(list()))
                .withConstraint(then(constantRater(cost(7))))
                .toProblem()
                .asSolution();
        testSubject.history().processWithHistory(() -> {
            testSubject.assign(
                    testSubject.demands().rawLine(0)
                    , testSubject.supplies().rawLine(0));
            testSubject.history().resetTo(-1);
            testSubject.history().unorderedLines().requireSizeOf(0);
        });
    }

    @IntegrationTest
    public void test_reset_to_middle() {
        final var solution = defineProblem("test_reset_to_middle")
                .withDemandAttributes()
                .withDemands(list(list()
                        , list()
                        , list()
                        , list()))
                .withSupplyAttributes()
                .withSupplies(list(list()
                        , list()
                        , list()
                        , list()))
                .withConstraint(then(constantRater(cost(7))))
                .toProblem()
                .asSolution();
        solution.history().processWithHistory(() -> {
            rangeClosed(0, 3).forEach(i -> solution.assign
                    (solution.demands().rawLine(i)
                            , solution.supplies().rawLine(i)));
            solution.history().unorderedLines().requireSizeOf(4);
            solution.history().resetTo(2);
            solution.history().unorderedLines().requireSizeOf(3);
        });
    }

    @IntegrationTest
    public void test_subscription_of_history_to_solution() {
        final var solution = defineProblem("test_subscription_of_history_to_solution")
                .withDemandAttributes()
                .withDemands(list(list()))
                .withSupplyAttributes()
                .withSupplies(list(list()))
                .withConstraint(then(constantRater(cost(7))))
                .toProblem()
                .asSolution();
        solution.history().processWithHistory(() -> {
            solution.history().unorderedLines().requireSizeOf(0);
            {
                solution.assign
                        (solution.demands().rawLine(0)
                                , solution.supplies().rawLine(0));
                solution.history().unorderedLines().requireSizeOf(1);
                final var additionEvent = solution.history().rawLine(0);
                requireEquals(additionEvent.value(EVENT_TYPE), TableEventType.ADDITION);
                requireEquals(additionEvent.value(DEMAND), solution.demands().rawLine(0));
                requireEquals(additionEvent.value(SUPPLY), solution.supplies().rawLine(0));
            }
            {
                solution.history().unorderedLines().requireSizeOf(1);
                solution.remove(0);
                solution.history().unorderedLines().requireSizeOf(2);
                final var removalEvent = solution.history().rawLine(1);
                requireEquals(removalEvent.value(EVENT_TYPE), TableEventType.REMOVAL);
                requireEquals(removalEvent.value(DEMAND), solution.demands().rawLine(0));
                requireEquals(removalEvent.value(SUPPLY), solution.supplies().rawLine(0));
            }
        });
    }

    @IntegrationTest
    public void test_history_allocation_rating() {
        final var solution = defineProblem("test_history_allocation_rating")
                .withDemandAttributes()
                .withDemands(list(list()))
                .withSupplyAttributes()
                .withSupplies(list(list()))
                .withConstraint(then(constantRater(cost(7))))
                .toProblem()
                .asSolution();
        solution.history().processWithHistory(() -> {
            final var demandValue = solution.assign
                    (solution.demands().rawLine(0)
                            , solution.supplies().rawLine(0));
            requireEquals(solution
                            .history()
                            .rawLine(0)
                            .value(META_DATA)
                            .value(AllocationRating.class)
                            .get()
                            .value()
                    , cost(7));
            {
                solution.remove(demandValue);
                requireEquals(solution
                                .history()
                                .rawLine(1)
                                .value(META_DATA)
                                .value(AllocationRating.class)
                                .get()
                                .value()
                        , noCost());
            }
        });
    }
}
