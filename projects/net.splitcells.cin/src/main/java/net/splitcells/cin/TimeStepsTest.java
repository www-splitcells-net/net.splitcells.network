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
package net.splitcells.cin;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.gel.Gel;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.cin.TimeSteps.NO_TIME_STEP_GROUP;
import static net.splitcells.cin.TimeSteps.timeStepId;
import static net.splitcells.cin.TimeSteps.timeSteps;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.TestTypes.EXPERIMENTAL_TEST;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.constraint.Constraint.RESULTING_CONSTRAINT_GROUP;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearDeinitializer.onlineLinearDeinitializer;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;

public class TimeStepsTest {

    @Test
    public void testTimeEvenSteps() {
        final var time = attribute(Integer.class, "time");
        final var testSubject = defineProblem("testTimeSteps")
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
        testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .forEach(g -> requireEquals(g.name().get(), NO_TIME_STEP_GROUP));
        testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .forEach(g -> requireEquals(g.name().get(), NO_TIME_STEP_GROUP));
        testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(NO_TIME_STEP_GROUP
                        , timeStepId(0, 1)
                        , timeStepId(0, 1)));
        testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0));
        testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(NO_TIME_STEP_GROUP
                        , timeStepId(0, 1)
                        , timeStepId(0, 1)
                        , timeStepId(2, 3)
                        , timeStepId(2, 3)));
        testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(NO_TIME_STEP_GROUP
                        , timeStepId(0, 1)
                        , timeStepId(0, 1)
                        , timeStepId(2, 3)
                        , timeStepId(2, 3)
                        , NO_TIME_STEP_GROUP));
    }

    @Test
    public void testTimeOddSteps() {
        final var time = attribute(Integer.class, "time");
        final var testSubject = defineProblem("testTimeSteps")
                .withDemandAttributes(time)
                .withDemands(list(
                        list(0)
                        , list(1)
                        , list(2)
                        , list(3)
                ))
                .withSupplyAttributes()
                .withSupplies(list(
                        list()
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
        testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .forEach(g -> requireEquals(g.name().get(), NO_TIME_STEP_GROUP));
        testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .forEach(g -> requireEquals(g.name().get(), NO_TIME_STEP_GROUP));
        testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(NO_TIME_STEP_GROUP
                        , timeStepId(1, 2)
                        , timeStepId(1, 2)));
        testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0));
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .mapped(g -> g.name().orElseThrow())
                .assertEquals(list(NO_TIME_STEP_GROUP
                        , timeStepId(1, 2)
                        , timeStepId(1, 2)
                        , NO_TIME_STEP_GROUP));
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
                            .lines()
                            .requireSizeOf(0);
                    testSubject.constraint().childrenView().get(0).lineProcessing()
                            .columnView(RESULTING_CONSTRAINT_GROUP)
                            .lookup(oneToTwo)
                            .lines()
                            .requireSizeOf(34);
                    onlineLinearDeinitializer().optimize(testSubject);
                    testSubject.allocations().lines().requireSizeOf(0);
                    testSubject.constraint().childrenView().get(0)
                            .lineProcessing()
                            .lines()
                            .requireSizeOf(0);
                })
        );
    }
}
