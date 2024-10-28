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

import static net.splitcells.cin.deprecated.raters.deprecated.PlayerValuePersistenceClassifier.playerValuePersistenceClassifier;
import static net.splitcells.cin.deprecated.raters.deprecated.PositionClusters.positionClusters;
import static net.splitcells.cin.raters.TimeSteps.timeSteps;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireIllegalDefaultConstructor;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.constraint.Constraint.RATING;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;

public class PlayerValuePersistenceClassifierTest {
    @UnitTest
    public void testIllegalDefaultConstructor() {
        requireIllegalDefaultConstructor(PlayerValuePersistenceClassifier.class);
    }

    @UnitTest
    public void testCostOfDefianceNeighbour() {
        final var time = attribute(Integer.class, "time");
        final var playerValue = attribute(Integer.class, "playerValue");
        final var xCoordinate = attribute(Integer.class, "xCoordinate");
        final var yCoordinate = attribute(Integer.class, "yCoordinate");
        final var testSubject = defineProblem("testTimeEvenSteps")
                .withDemandAttributes(time, playerValue, xCoordinate, yCoordinate)
                .withDemands(list(list(0, 1, 1, 1)
                        , list(1, 0, 1, 1)
                        , list(1, 0, 2, 2)
                ))
                .withSupplyAttributes()
                .withSupplies(list(list()
                        , list()
                        , list()))
                .withConstraint(c -> {
                    c.forAll(timeSteps(time))
                            .forAll(positionClusters(xCoordinate, yCoordinate))
                            .then(playerValuePersistenceClassifier(1, playerValue, time, xCoordinate, yCoordinate,
                                    (startPlayerValues, endPlayerValues)
                                            -> startPlayerValues.anyMatch(s -> s.value(playerValue) == 1)
                                            && endPlayerValues.anyMatch(e -> e.value(playerValue) == 1)
                                    , "survives"));
                    return c;
                })
                .toProblem()
                .asSolution();
        onlineLinearInitialization().optimize(testSubject);
        testSubject.constraint().rating().requireEqualsTo(cost(2));
        testSubject.constraint()
                .child(0)
                .child(0)
                .child(0)
                .lineProcessing()
                .columnView(RATING)
                .values()
                .requireContainsOneOf(cost(1)
                        , cost(1)
                        , noCost());
    }
}
