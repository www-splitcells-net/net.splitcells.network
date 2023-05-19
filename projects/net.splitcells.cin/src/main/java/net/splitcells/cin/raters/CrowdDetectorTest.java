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

import static net.splitcells.cin.raters.CrowdDetector.crowdDetector;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireIllegalDefaultConstructor;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class CrowdDetectorTest {
    @UnitTest
    public void testIllegalDefaultConstructor() {
        requireIllegalDefaultConstructor(CrowdDetector.class);
    }

    @UnitTest
    public void test() {
        final var player = attribute(Integer.class, "player");
        final var time = attribute(Integer.class, "time");
        final var testSubject = Gel.defineProblem("testProposeViaConstraintPath")
                .withDemandAttributes(player)
                .withDemands(list(list(1)
                        , list(1)
                        , list(1)
                        , list(1)
                        , list(1)
                ))
                .withSupplyAttributes(time)
                .withSupplies(list(list(2)
                        , list(1)
                        , list(2)
                        , list(1)
                        , list(1)
                ))
                .withConstraint(q -> {
                    q.then(crowdDetector(1, player, time, s -> s == 2, "test"));
                    return q;
                })
                .toProblem()
                .asSolution();
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(1));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(2));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(3));
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(noCost());
        testSubject.allocate(testSubject.demandsFree().orderedLine(0)
                , testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(5));
    }
}
