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
package net.splitcells.cin.raters;

import static net.splitcells.cin.raters.Loneliness.loneliness;
import static net.splitcells.cin.raters.TimeSteps.timeSteps;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

public class LonelinessTest {

    public void testLoneliness() {
        final var time = attribute(Integer.class, "time");
        final var playerValue = attribute(Integer.class, "playerValue");
        final var positionX = attribute(Integer.class, "positionX");
        final var positionY = attribute(Integer.class, "positionY");
        final var testSubject = defineProblem("testTimeEvenSteps")
                .withDemandAttributes(time, playerValue, positionX, positionY)
                .withDemands(list(
                        list(0, 1, 1, 1)
                        , list(0, 2, 1, 2)
                        , list(0, 2, 2, 2)
                        , list(0, 1, 1, 2)
                        , list(0, 1, 2, 2)
                ))
                .withSupplyAttributes()
                .withSupplies(list(
                        list()
                        , list()
                        , list()
                        , list()
                        , list()
                ))
                .withConstraint(c -> {
                    c.forAll(timeSteps(time)).forAll(loneliness(1, playerValue, time, positionX, positionY)).forAll();
                    return c;
                })
                .toProblem()
                .asSolution();
        testSubject.allocate(testSubject.demandsFree().line(0)
                , testSubject.suppliesFree().line(0));
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().lines().requireEmpty();
    }
}
