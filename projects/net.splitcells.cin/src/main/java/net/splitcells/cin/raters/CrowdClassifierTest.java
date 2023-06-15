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
import net.splitcells.gel.Gel;

import static net.splitcells.cin.raters.CrowdClassifier.crowdClassifier;
import static net.splitcells.cin.raters.PositionClusters.positionClusters;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireIllegalDefaultConstructor;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class CrowdClassifierTest {
    @UnitTest
    public void testIllegalDefaultConstructor() {
        requireIllegalDefaultConstructor(CrowdClassifier.class);
    }

    @UnitTest
    public void test() {
        final var player = attribute(Integer.class, "player");
        final var time = attribute(Integer.class, "time");
        final var xCoord = attribute(Integer.class, "x-coordinate");
        final var yCoord = attribute(Integer.class, "y-coordinate");
        final var testSubject = Gel.defineProblem("testProposeViaConstraintPath")
                .withDemandAttributes(player, xCoord, yCoord)
                .withDemands(list(list(1, 0, 0)
                        , list(1, 0, 0)
                        , list(1, 0, 0)
                        , list(1, 0, 0)
                        , list(1, 0, 0)
                ))
                .withSupplyAttributes(time)
                .withSupplies(list(list(2)
                        , list(1)
                        , list(2)
                        , list(1)
                        , list(1)
                ))
                .withConstraint(q -> {
                    q.forAll(positionClusters(xCoord, yCoord))
                            .then(crowdClassifier(1, player, time, xCoord, yCoord, s -> s == 2, "test"));
                    return q;
                })
                .toProblem()
                .asSolution();
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(1));
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(2));
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(3));
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(noCost());
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(5));
    }

    @UnitTest
    public void testIgnoranceOfCenterPosition() {
        final var player = attribute(Integer.class, "player");
        final var time = attribute(Integer.class, "time");
        final var xCoord = attribute(Integer.class, "x-coordinate");
        final var yCoord = attribute(Integer.class, "y-coordinate");
        final var testSubject = Gel.defineProblem("testProposeViaConstraintPath")
                .withDemandAttributes(player, xCoord, yCoord)
                .withDemands(list(list(1, 1, 1)
                        , list(1, 1, 1)
                        , list(1, 1, 1)
                        , list(1, 1, 1)
                        , list(1, 1, 1)
                ))
                .withSupplyAttributes(time)
                .withSupplies(list(list(2)
                        , list(1)
                        , list(2)
                        , list(1)
                        , list(1)
                ))
                .withConstraint(q -> {
                    q.forAll(positionClusters(xCoord, yCoord))
                            .then(crowdClassifier(1, player, time, xCoord, yCoord, s -> s == 2, "test"));
                    return q;
                })
                .toProblem()
                .asSolution();
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(1));
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(2));
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(3));
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(4));
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(5));
    }
}
