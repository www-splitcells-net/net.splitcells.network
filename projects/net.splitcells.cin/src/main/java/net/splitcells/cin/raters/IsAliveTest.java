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

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.cin.raters.IsAlive.isAlive;
import static net.splitcells.cin.raters.PositionClusters.positionClusters;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireIllegalDefaultConstructor;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

public class IsAliveTest {

    @UnitTest
    public void testAlive() {
        final var player = attribute(Integer.class, "player");
        final var time = attribute(Integer.class, "time");
        final var xCoord = attribute(Integer.class, "x-coordinate");
        final var yCoord = attribute(Integer.class, "y-coordinate");
        final var testSubject = defineProblem("testAlive")
                .withDemandAttributes(player, time, xCoord, yCoord)
                .withDemands(list(list(1, 0, 1, 1), list(1, 1, 1, 1)))
                .withSupplyAttributes()
                .withSupplies(list(list(), list()))
                .withConstraint(c -> {
                    c.forAll(positionClusters(xCoord, yCoord))
                            .then(isAlive(1, player, time, xCoord, yCoord))
                            .forAll();
                    return c;
                })
                .toProblem()
                .asSolution();
        testSubject.allocate(testSubject.demandsFree().orderedLine(0), testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.allocate(testSubject.demandsFree().orderedLine(0), testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.constraint().childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.deallocate(testSubject.demandsUsed().orderedLine(0), testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
    }

    @UnitTest
    public void testDead() {
        final var player = attribute(Integer.class, "player");
        final var time = attribute(Integer.class, "time");
        final var xCoord = attribute(Integer.class, "x-coordinate");
        final var yCoord = attribute(Integer.class, "y-coordinate");
        final var testSubject = defineProblem("testDead")
                .withDemandAttributes(player, time, xCoord, yCoord)
                .withDemands(list(list(0, 0, 1, 1), list(1, 1, 1, 1)))
                .withSupplyAttributes()
                .withSupplies(list(list(), list()))
                .withConstraint(c -> {
                    c.forAll(positionClusters(xCoord, yCoord))
                            .then(isAlive(1, player, time, xCoord, yCoord))
                            .forAll();
                    return c;
                })
                .toProblem()
                .asSolution();
        testSubject.allocate(testSubject.demandsFree().orderedLine(0), testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireEmpty();
        testSubject.allocate(testSubject.demandsFree().orderedLine(0), testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.constraint().childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireEmpty();
    }
}
