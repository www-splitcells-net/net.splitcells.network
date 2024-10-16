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
package net.splitcells.cin.deprecated.raters.deprecated;

import net.splitcells.dem.testing.annotations.UnitTest;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.cin.deprecated.World.isAlive;
import static net.splitcells.cin.deprecated.raters.deprecated.PositionClusters.positionClusters;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.constraint.Constraint.RESULTING_CONSTRAINT_GROUP;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

public class IsAliveTest {

    private static final String PLAYER = "player";
    private static final String TIME = "time";
    private static final String X_COORDINATE = "x-coordinate";
    private static final String Y_COORDINATE = "y-coordinate";

    @UnitTest
    public void testAlive() {
        final var player = attribute(Integer.class, PLAYER);
        final var time = attribute(Integer.class, TIME);
        final var xCoord = attribute(Integer.class, X_COORDINATE);
        final var yCoord = attribute(Integer.class, Y_COORDINATE);
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
        testSubject.assign(testSubject.demandsFree().orderedLine(0), testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.assign(testSubject.demandsFree().orderedLine(0), testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.constraint().childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.undoAssignment(testSubject.demandsUsed().orderedLine(0), testSubject.suppliesUsed().orderedLine(0));
        testSubject.constraint().lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
    }

    @UnitTest
    public void testAliveWithMultipleCenterStartPositions() {
        final var player = attribute(Integer.class, PLAYER);
        final var time = attribute(Integer.class, TIME);
        final var xCoord = attribute(Integer.class, X_COORDINATE);
        final var yCoord = attribute(Integer.class, Y_COORDINATE);
        final var testSubject = defineProblem("testAliveWithMultipleCenterStartPositions")
                .withDemandAttributes(player, time, xCoord, yCoord)
                .withDemands(list(list(0, 0, 1, 1)
                        , list(0, 0, 1, 1)
                        , list(1, 0, 1, 1)
                        , list(0, 1, 1, 1)
                        , list(1, 1, 1, 1)))
                .withSupplyAttributes()
                .withSupplies(list(list()
                        , list()
                        , list()
                        , list()
                        , list()))
                .withConstraint(c -> {
                    c.forAll(positionClusters(xCoord, yCoord))
                            .forAll(isAlive(1, player, time, xCoord, yCoord))
                            .forAll();
                    return c;
                })
                .toProblem()
                .asSolution();
        rangeClosed(1, 5).forEach(i ->
                testSubject.assign(testSubject.demandsFree()
                        .orderedLine(0), testSubject.suppliesFree().orderedLine(0)));
        testSubject.constraint().lineProcessing().unorderedLines().requireSizeOf(5);
        testSubject.constraint().child(0).lineProcessing().unorderedLines().requireSizeOf(5);
        testSubject.constraint().child(0).lineProcessing().columnView(RESULTING_CONSTRAINT_GROUP)
                .stream()
                .distinct()
                .collect(toList())
                .requireSetSizeOf(1);
        testSubject.constraint().child(0).child(0).lineProcessing().unorderedLines().requireSizeOf(5);
        testSubject.constraint().child(0).child(0).child(0);
        testSubject.constraint()
                .child(0)
                .child(0)
                .child(0)
                .lineProcessing()
                .unorderedLines()
                .requireSizeOf(5);
    }

    @UnitTest
    public void testDeadWithMultipleCenterStartPositions() {
        final var player = attribute(Integer.class, PLAYER);
        final var time = attribute(Integer.class, TIME);
        final var xCoord = attribute(Integer.class, X_COORDINATE);
        final var yCoord = attribute(Integer.class, Y_COORDINATE);
        final var testSubject = defineProblem("testDeadWithMultipleCenterStartPositions")
                .withDemandAttributes(player, time, xCoord, yCoord)
                .withDemands(list(list(0, 0, 1, 1)
                        , list(0, 0, 1, 1)
                        , list(0, 0, 1, 1)
                        , list(0, 1, 1, 1)
                        , list(0, 1, 1, 1)))
                .withSupplyAttributes()
                .withSupplies(list(list()
                        , list()
                        , list()
                        , list()
                        , list()))
                .withConstraint(c -> {
                    c.forAll(positionClusters(xCoord, yCoord))
                            .forAll(isAlive(1, player, time, xCoord, yCoord))
                            .forAll();
                    return c;
                })
                .toProblem()
                .asSolution();
        rangeClosed(1, 5).forEach(i ->
                testSubject.assign(testSubject.demandsFree()
                        .orderedLine(0), testSubject.suppliesFree().orderedLine(0)));
        testSubject.constraint().lineProcessing().unorderedLines().requireSizeOf(5);
        testSubject.constraint().child(0).lineProcessing().unorderedLines().requireSizeOf(5);
        testSubject.constraint().child(0).lineProcessing().columnView(RESULTING_CONSTRAINT_GROUP)
                .stream()
                .distinct()
                .collect(toList())
                .requireSetSizeOf(1);
        testSubject.constraint().child(0).child(0).lineProcessing().unorderedLines().requireSizeOf(5);
        testSubject.constraint().child(0).child(0).child(0);
        testSubject.constraint()
                .child(0)
                .child(0)
                .child(0)
                .lineProcessing()
                .unorderedLines()
                .requireSizeOf(0);
    }

    @UnitTest
    public void testDead() {
        final var player = attribute(Integer.class, PLAYER);
        final var time = attribute(Integer.class, TIME);
        final var xCoord = attribute(Integer.class, X_COORDINATE);
        final var yCoord = attribute(Integer.class, Y_COORDINATE);
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
        testSubject.assign(testSubject.demandsFree().orderedLine(0), testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(1);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireEmpty();
        testSubject.assign(testSubject.demandsFree().orderedLine(0), testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.constraint().childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireSizeOf(2);
        testSubject.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireEmpty();
    }
}
