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
import net.splitcells.gel.Gel;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.range;
import static net.splitcells.cin.TimeSteps.timeSteps;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.testing.TestTypes.EXPERIMENTAL_TEST;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.constraint.Constraint.RESULTING_CONSTRAINT_GROUP;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;

public class TimeStepsTest {
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
        testSubject.history().processWithHistory(() -> {
            testSubject.optimize(onlineLinearInitialization());
            testSubject.constraint().childrenView().get(0).lineProcessing()
                    .columnView(RESULTING_CONSTRAINT_GROUP)
                    .stream()
                    .distinct()
                    .collect(toList())
                    .requireSizeOf(2);
            final var oneToTwo = testSubject.constraint().childrenView().get(0).lineProcessing()
                    .columnView(RESULTING_CONSTRAINT_GROUP)
                    .stream()
                    .distinct()
                    .collect(toList())
                    .get(0);
            testSubject.constraint().childrenView().get(0).lineProcessing()
                    .columnView(RESULTING_CONSTRAINT_GROUP)
                    .lookup(oneToTwo)
                    .lines()
                    .requireSizeOf(34);
        });
    }
}
