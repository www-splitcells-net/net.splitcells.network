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
package net.splitcells.cin.raters;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.cin.raters.TimeSteps.NO_TIME_STEP_GROUP;
import static net.splitcells.cin.raters.TimeSteps.timeStepId;
import static net.splitcells.cin.raters.TimeSteps.timeSteps;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.TestTypes.EXPERIMENTAL_TEST;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.common.Language.FOR_ALL;
import static net.splitcells.gel.constraint.Constraint.RESULTING_CONSTRAINT_GROUP;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearDeinitializer.onlineLinearDeinitializer;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;

public class TimeStepsTest {

    @Test
    public void testTimeEvenSteps() {
        final var time = attribute(Integer.class, "time");
        final var testSubject = defineProblem("testTimeEvenSteps")
                .withDemandAttributes(time)
                .withDemands(list(
                        list(-1)
                        , list(0)
                        , list(1)
                        , list(2)
                        , list(3)
                        , list(4)
                ))
                .withSupplyAttributes()
                .withSupplies(list(
                        list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                ))
                .withConstraint(c -> {
                    c.forAll(timeSteps(time));
                    return c;
                })
                .toProblem()
                .asSolution();
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .forEach(g -> requireEquals(g.name().get(), FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .forEach(g -> requireEquals(g.name().get(), FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP
                        , FOR_ALL.value() + " " + timeStepId(0, 1)
                        , FOR_ALL.value() + " " + timeStepId(0, 1)));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP
                        , FOR_ALL.value() + " " + timeStepId(0, 1)
                        , FOR_ALL.value() + " " + timeStepId(0, 1)
                        , FOR_ALL.value() + " " + timeStepId(2, 3)
                        , FOR_ALL.value() + " " + timeStepId(2, 3)));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP
                        , FOR_ALL.value() + " " + timeStepId(0, 1)
                        , FOR_ALL.value() + " " + timeStepId(0, 1)
                        , FOR_ALL.value() + " " + timeStepId(2, 3)
                        , FOR_ALL.value() + " " + timeStepId(2, 3)
                        , FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0),
                testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + timeStepId(0, 1)
                        , FOR_ALL.value() + " " + timeStepId(0, 1)
                        , FOR_ALL.value() + " " + timeStepId(2, 3)
                        , FOR_ALL.value() + " " + timeStepId(2, 3)
                        , FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0),
                testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP
                        , FOR_ALL.value() + " " + timeStepId(2, 3)
                        , FOR_ALL.value() + " " + timeStepId(2, 3)
                        , FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0),
                testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + timeStepId(2, 3)
                        , FOR_ALL.value() + " " + timeStepId(2, 3)
                        , FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0),
                testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP
                        , FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0),
                testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0),
                testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list());
    }

    @Test
    public void testTimeOddSteps() {
        final var time = attribute(Integer.class, "time");
        final var testSubject = defineProblem("testTimeOddSteps")
                .withDemandAttributes(time)
                .withDemands(list(
                        list(0)
                        , list(1)
                        , list(2)
                        , list(3)
                        , list(4)
                        , list(5)
                ))
                .withSupplyAttributes()
                .withSupplies(list(
                        list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()
                ))
                .withConstraint(c -> {
                    c.forAll(timeSteps(time, false));
                    return c;
                })
                .toProblem()
                .asSolution();
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .forEach(g -> requireEquals(g.name().get(), FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .forEach(g -> requireEquals(g.name().get(), FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP
                        , FOR_ALL.value() + " " + timeStepId(1, 2)
                        , FOR_ALL.value() + " " + timeStepId(1, 2)));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP
                        , FOR_ALL.value() + " " + timeStepId(1, 2)
                        , FOR_ALL.value() + " " + timeStepId(1, 2)
                        , FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP
                        , FOR_ALL.value() + " " + timeStepId(1, 2)
                        , FOR_ALL.value() + " " + timeStepId(1, 2)
                        , FOR_ALL.value() + " " + timeStepId(3, 4)
                        , FOR_ALL.value() + " " + timeStepId(3, 4)));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP
                        , FOR_ALL.value() + " " + timeStepId(1, 2)
                        , FOR_ALL.value() + " " + timeStepId(1, 2)
                        , FOR_ALL.value() + " " + timeStepId(3, 4)
                        , FOR_ALL.value() + " " + timeStepId(3, 4)
                        , FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0)
                , testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + timeStepId(1, 2)
                        , FOR_ALL.value() + " " + timeStepId(1, 2)
                        , FOR_ALL.value() + " " + timeStepId(3, 4)
                        , FOR_ALL.value() + " " + timeStepId(3, 4)
                        , FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0)
                , testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP
                        , FOR_ALL.value() + " " + timeStepId(3, 4)
                        , FOR_ALL.value() + " " + timeStepId(3, 4)
                        , FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0)
                , testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + timeStepId(3, 4)
                        , FOR_ALL.value() + " " + timeStepId(3, 4)
                        , FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0)
                , testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP
                        , FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0)
                , testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(FOR_ALL.value() + " " + NO_TIME_STEP_GROUP));
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0)
                , testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list());
    }

    @Tag(EXPERIMENTAL_TEST)
    @Test
    public void testTimeSteps() {
        final var time = attribute(Integer.class, "time");
        final var value = attribute(Integer.class, "value");
        final var testSubject = defineProblem("testTimeSteps")
                .withDemandAttributes(time)
                .withDemands(range(0, 100).mapToObj(i -> {
                    if (i <= 33) {
                        return list((Object) 1);
                    } else if (i <= 66) {
                        return list((Object) 2);
                    } else {
                        return list((Object) 3);
                    }
                }).collect(toList()))
                .withSupplyAttributes(value)
                .withSupplies(range(0, 100).mapToObj(i -> list((Object) i)).collect(toList()))
                .withConstraint(c -> {
                    c.forAll(timeSteps(time));
                    return c;
                })
                .toProblem()
                .asSolution();
        rangeClosed(1, 3).forEach(i ->
                testSubject.history().processWithHistory(() -> {
                    testSubject.optimize(onlineLinearInitialization());
                    testSubject.constraint().childrenView().get(0).lineProcessing()
                            .columnView(RESULTING_CONSTRAINT_GROUP)
                            .stream()
                            .distinct()
                            .collect(toList())
                            .requireSizeOf(3);
                    final var oneToTwo = testSubject.constraint().childrenView().get(0).lineProcessing()
                            .columnView(RESULTING_CONSTRAINT_GROUP)
                            .stream()
                            .filter(s -> s.name().orElseThrow().equals(timeStepId(1, 2)))
                            .findFirst()
                            .orElseThrow();
                    final var noTimeStepGroup = testSubject.constraint().childrenView().get(0).lineProcessing()
                            .columnView(RESULTING_CONSTRAINT_GROUP)
                            .stream()
                            .filter(s -> s.name().orElseThrow().equals(NO_TIME_STEP_GROUP))
                            .findFirst()
                            .orElseThrow();
                    testSubject.constraint().childrenView().get(0).lineProcessing()
                            .columnView(RESULTING_CONSTRAINT_GROUP)
                            .lookup(noTimeStepGroup)
                            .unorderedLines()
                            .requireSizeOf(0);
                    testSubject.constraint().childrenView().get(0).lineProcessing()
                            .columnView(RESULTING_CONSTRAINT_GROUP)
                            .lookup(oneToTwo)
                            .unorderedLines()
                            .requireSizeOf(34);
                    onlineLinearDeinitializer().optimize(testSubject);
                    testSubject.allocations().unorderedLines().requireSizeOf(0);
                    testSubject.constraint().childrenView().get(0)
                            .lineProcessing()
                            .unorderedLines()
                            .requireSizeOf(0);
                })
        );
    }
}
