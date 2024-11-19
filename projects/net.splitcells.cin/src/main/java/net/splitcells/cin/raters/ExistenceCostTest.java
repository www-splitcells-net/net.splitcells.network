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

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.cin.EntityManager.ADD_VALUE;
import static net.splitcells.cin.EntityManager.EVENT_SOURCE;
import static net.splitcells.cin.EntityManager.EVENT_TYPE;
import static net.splitcells.cin.EntityManager.EXISTENCE_COST_EVENT_SOURCE;
import static net.splitcells.cin.EntityManager.PLAYER;
import static net.splitcells.cin.EntityManager.PLAYER_ATTRIBUTE;
import static net.splitcells.cin.EntityManager.PLAYER_ENERGY;
import static net.splitcells.cin.EntityManager.PLAYER_VALUE;
import static net.splitcells.cin.EntityManager.TIME;
import static net.splitcells.cin.raters.ExistenceCost.existenceCost;
import static net.splitcells.cin.raters.TimeSteps.overlappingTimeSteps;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;

public class ExistenceCostTest {
    @UnitTest
    public void test() {
        final var testSubject = defineProblem("entity-manager")
                .withDemandAttributes(TIME)
                .withNoDemands()
                .withSupplyAttributes(PLAYER_ATTRIBUTE, PLAYER_VALUE, EVENT_TYPE, EVENT_SOURCE)
                .withSupplies()
                .withConstraint(query -> {
                    query.forAll(overlappingTimeSteps(TIME)).then(existenceCost());
                    return query;
                })
                .toProblem()
                .asSolution();
        final var demands = testSubject.demands();
        final var supplies = testSubject.supplies();
        testSubject.constraint().rating().requireEqualsTo(noCost());
        testSubject.assign(demands.addTranslated(listWithValuesOf(0)), supplies.addTranslated(listWithValuesOf(0, 0, 0, 0)));
        testSubject.constraint().rating().requireEqualsTo(noCost());
        testSubject.assign(demands.addTranslated(listWithValuesOf(1)), supplies.addTranslated(listWithValuesOf(0, 0, 0, 0)));
        testSubject.constraint().rating().requireEqualsTo(cost(2));
        testSubject.assign(demands.addTranslated(listWithValuesOf(1))
                , supplies.addTranslated(listWithValuesOf(PLAYER_ENERGY, -1, ADD_VALUE, EXISTENCE_COST_EVENT_SOURCE)));
        testSubject.constraint().rating().requireEqualsTo(noCost());
        testSubject.remove(1);
        testSubject.constraint().rating().requireEqualsTo(noCost());
        testSubject.remove(2);
        testSubject.constraint().rating().requireEqualsTo(noCost());
        testSubject.remove(0);
        testSubject.constraint().rating().requireEqualsTo(noCost());
    }
}
