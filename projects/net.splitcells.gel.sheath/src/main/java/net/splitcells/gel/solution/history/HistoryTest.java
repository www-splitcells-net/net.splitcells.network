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
package net.splitcells.gel.solution.history;

import net.splitcells.dem.testing.Assertions;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.solution.history.meta.type.AllocationRating;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.TestTypes.BENCHMARK_RUNTIME;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.history.History.ALLOCATION_EVENT;
import static net.splitcells.gel.solution.history.History.META_DATA;
import static net.splitcells.gel.solution.history.event.AllocationChangeType.ADDITION;
import static net.splitcells.gel.solution.history.event.AllocationChangeType.REMOVAL;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;

public class HistoryTest {

    @Tag(BENCHMARK_RUNTIME)
    @Test
    public void test_rest_to_beginning_runtime() {
        final var testSubject = defineProblem("test_rest_to_beginning_runtime")
                .withDemandAttributes()
                .withDemands(rangeClosed(1, 10000).mapToObj(i -> list()).collect(toList()))
                .withSupplyAttributes()
                .withSupplies(rangeClosed(1, 10000).mapToObj(i -> list()).collect(toList()))
                .withConstraint(Then.then(constantRater(cost(7))))
                .toProblem()
                .asSolution();
        testSubject.history().processWithHistory(() -> {
            testSubject.optimize(onlineLinearInitialization());
            testSubject.history().resetTo(0);
        });
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_reset_to_beginning() {
        final var testSubject = defineProblem("test_reset_to_beginning")
                .withDemandAttributes()
                .withDemands(list(list()))
                .withSupplyAttributes()
                .withSupplies(list(list()))
                .withConstraint(Then.then(constantRater(cost(7))))
                .toProblem()
                .asSolution();
        testSubject.history().processWithHistory(() -> {
            testSubject.allocate(
                    testSubject.demands().rawLine(0)
                    , testSubject.supplies().rawLine(0));
            testSubject.history().resetTo(-1);
            testSubject.history().lines().requireSizeOf(0);
        });
    }

    @Tag(INTEGRATION_TEST)
    @Test
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
                .withConstraint(Then.then(constantRater(cost(7))))
                .toProblem()
                .asSolution();
        solution.history().processWithHistory(() -> {
            rangeClosed(0, 3).forEach(i -> solution.allocate
                    (solution.demands().rawLine(i)
                            , solution.supplies().rawLine(i)));
            solution.history().lines().requireSizeOf(4);
            solution.history().resetTo(2);
            solution.history().lines().requireSizeOf(3);
        });
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_subscription_of_history_to_solution() {
        final var solution = defineProblem("test_subscription_of_history_to_solution")
                .withDemandAttributes()
                .withDemands(list(list()))
                .withSupplyAttributes()
                .withSupplies(list(list()))
                .withConstraint(Then.then(constantRater(cost(7))))
                .toProblem()
                .asSolution();
        solution.history().processWithHistory(() -> {
            solution.history().lines().requireSizeOf(0);
            {
                solution.allocate
                        (solution.demands().rawLine(0)
                                , solution.supplies().rawLine(0));
                solution.history().lines().requireSizeOf(1);
                final var additionEvent = solution.history().rawLine(0);
                final var additionOperation = additionEvent.value(ALLOCATION_EVENT);
                requireEquals(additionOperation.type(), ADDITION);
                requireEquals(additionOperation.demand(), solution.demands().rawLine(0));
                requireEquals(additionOperation.supply(), solution.supplies().rawLine(0));
            }
            {
                solution.history().lines().requireSizeOf(1);
                solution.remove(0);
                solution.history().lines().requireSizeOf(2);
                final var removalEvent = solution.history().rawLine(1);
                final var removalOperation = removalEvent.value(ALLOCATION_EVENT);
                requireEquals(removalOperation.type(), REMOVAL);
                requireEquals(removalOperation.demand(), solution.demands().rawLine(0));
                requireEquals(removalOperation.supply(), solution.supplies().rawLine(0));
            }
        });
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_history_allocation_rating() {
        final var solution = defineProblem("test_history_allocation_rating")
                .withDemandAttributes()
                .withDemands(list(list()))
                .withSupplyAttributes()
                .withSupplies(list(list()))
                .withConstraint(Then.then(constantRater(cost(7))))
                .toProblem()
                .asSolution();
        solution.history().processWithHistory(() -> {
            final var demandValue = solution.allocate
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
