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

import net.splitcells.dem.Dem;
import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.cin.deprecated.raters.deprecated.Loneliness.loneliness;
import static net.splitcells.cin.deprecated.raters.deprecated.PositionClusters.positionClusters;
import static net.splitcells.cin.deprecated.raters.deprecated.TimeSteps.timeSteps;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearDeinitializer.onlineLinearDeinitializer;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;

public class LonelinessTest {
    private static final String TIME = "time";
    private static final String PLAYER_VALUE = "playerValue";
    private static final String POSITION_X = "positionX";
    private static final String POSITION_Y = "positionY";
    @UnitTest
    public void testLoneliness() {
        final var time = attribute(Integer.class, TIME);
        final var playerValue = attribute(Integer.class, PLAYER_VALUE);
        final var positionX = attribute(Integer.class, POSITION_X);
        final var positionY = attribute(Integer.class, POSITION_Y);
        final var testSubject = defineProblem("testLoneliness")
                .withDemandAttributes(time, playerValue, positionX, positionY)
                .withDemands(list(list(0, 1, 1, 1)
                        , list(1, 1, 1, 1)
                        , list(0, 2, 1, 2)
                        , list(0, 2, 2, 2)
                        , list(0, 1, 1, 1)
                        , list(0, 1, 2, 2)))
                .withSupplyAttributes()
                .withSupplies(list(list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()))
                .withConstraint(c -> {
                    c.forAll(timeSteps(time))
                            .forAll(positionClusters(positionX, positionY))
                            .forAll(loneliness(1, playerValue, time, positionX, positionY))
                            .forAll();
                    return c;
                })
                .toProblem()
                .asSolution();
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0).childrenView().get(0).lineProcessing().unorderedLines().requireEmpty();
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .lineProcessing().unorderedLines().requireSizeOf(4);
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.assign(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .lineProcessing().unorderedLines().requireSizeOf(6);
        testSubject.constraint().childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .lineProcessing().unorderedLines().requireSizeOf(0);
    }

    @UnitTest
    public void testLonelinessWithoutCenterStart() {
        final var time = attribute(Integer.class, TIME);
        final var playerValue = attribute(Integer.class, PLAYER_VALUE);
        final var positionX = attribute(Integer.class, POSITION_X);
        final var positionY = attribute(Integer.class, POSITION_Y);
        final var testSubject = defineProblem("testLonelinessWithoutCenterStart")
                .withDemandAttributes(time, playerValue, positionX, positionY)
                .withDemands(list(list(0, 1, 2, 2)
                        , list(1, 1, 1, 1)
                        , list(0, 2, 1, 2)
                        , list(0, 2, 2, 2)
                        , list(0, 1, 2, 1)
                        , list(0, 1, 1, 2)))
                .withSupplyAttributes()
                .withSupplies(list(list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()))
                .withConstraint(c -> {
                    c.forAll(timeSteps(time))
                            .forAll(positionClusters(positionX, positionY))
                            .forAll(loneliness(1, playerValue, time, positionX, positionY))
                            .forAll();
                    return c;
                })
                .toProblem()
                .asSolution();
        onlineLinearInitialization().optimize(testSubject);
        testSubject.constraint().childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .lineProcessing().unorderedLines().requireSizeOf(6);
        testSubject.constraint().childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .lineProcessing().unorderedLines().requireSizeOf(0);
    }

    /**
     * TODO It seems, that the removing assignments do not work in a multithreaded context.
     * Maybe it is a issue with the multithreading injection or
     * there is a real bug in the {@link Loneliness} implementation.
     * {@link TimeSteps} seems to have the same problem.
     */
    @UnitTest
    public void testLonelinessRemoval() {
        final var time = attribute(Integer.class, TIME);
        final var playerValue = attribute(Integer.class, PLAYER_VALUE);
        final var positionX = attribute(Integer.class, POSITION_X);
        final var positionY = attribute(Integer.class, POSITION_Y);
        final var testSubject = defineProblem("testLonelinessRemoval")
                .withDemandAttributes(time, playerValue, positionX, positionY)
                .withDemands(list(list(0, 1, 2, 2)
                        , list(1, 1, 1, 1)
                        , list(0, 2, 1, 2)
                        , list(0, 2, 2, 2)
                        , list(0, 1, 2, 1)
                        , list(0, 1, 1, 2)))
                .withSupplyAttributes()
                .withSupplies(list(list()
                        , list()
                        , list()
                        , list()
                        , list()
                        , list()))
                .withConstraint(c -> {
                    c.forAll(timeSteps(time))
                            .forAll(positionClusters(positionX, positionY))
                            .forAll(loneliness(1, playerValue, time, positionX, positionY))
                            .forAll();
                    return c;
                })
                .toProblem()
                .asSolution();
        onlineLinearInitialization().optimize(testSubject);
        testSubject.constraint().childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .lineProcessing().unorderedLines().requireSizeOf(6);
        testSubject.constraint().childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .lineProcessing().unorderedLines().requireSizeOf(0);
        onlineLinearDeinitializer().optimize(testSubject);
        onlineLinearInitialization().optimize(testSubject);
        testSubject.constraint().childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .lineProcessing().unorderedLines().requireSizeOf(6);
        testSubject.constraint().childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .childrenView().get(0)
                .lineProcessing().unorderedLines().requireSizeOf(0);
    }
}
