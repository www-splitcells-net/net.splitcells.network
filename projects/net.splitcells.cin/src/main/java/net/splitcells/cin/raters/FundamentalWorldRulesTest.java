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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.cin.World.POSITION_X;
import static net.splitcells.cin.World.POSITION_Y;
import static net.splitcells.cin.World.VALUE;
import static net.splitcells.cin.World.WORLD_HISTORY;
import static net.splitcells.cin.World.WORLD_TIME;
import static net.splitcells.cin.World.worldHistory;
import static net.splitcells.cin.World.worldHistory2;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listOfShallowCopies;

public class FundamentalWorldRulesTest {
    @UnitTest
    public void testBlinker() {
        final var testSubject = worldHistory2(WORLD_HISTORY, list(), list());
        list(list(0, 0, 0)
                , list(0, 1, 0)
                , list(0, 2, 0)
                , list(0, 0, 1)
                , list(0, 1, 1)
                , list(0, 2, 1)
                , list(0, 0, 2)
                , list(0, 1, 2)
                , list(0, 2, 2)
                , list(1, 0, 0)
                , list(1, 1, 0)
                , list(1, 2, 0)
                , list(1, 0, 1)
                , list(1, 1, 1)
                , list(1, 2, 1)
                , list(1, 0, 2)
                , list(1, 1, 2)
                , list(1, 2, 2))
                .forEach(testSubject.demands()::addTranslated);
        Lists.<List<Object>>list()
                .withAppended(listOfShallowCopies(list(0), 3 * 4))
                .withAppended(listOfShallowCopies(list(1), 2 * 3))
                .forEach(testSubject.supplies()::addTranslated);
    }

    private void allocateBlinker(Solution worldHistory) {
        worldHistory.assign(worldHistory.demandsFree()
                        .lookup(WORLD_TIME, 0)
                        .lookup(POSITION_X, 1)
                        .lookup(POSITION_Y, 2)
                        .orderedLine(0)
                , worldHistory.suppliesFree()
                        .lookup(VALUE, 1)
                        .orderedLine(0));
        worldHistory.assign(worldHistory.demandsFree()
                        .lookup(WORLD_TIME, 0)
                        .lookup(POSITION_X, 2)
                        .lookup(POSITION_Y, 2)
                        .orderedLine(0)
                , worldHistory.suppliesFree()
                        .lookup(VALUE, 1)
                        .orderedLine(0));
        worldHistory.assign(worldHistory.demandsFree()
                        .lookup(WORLD_TIME, 0)
                        .lookup(POSITION_X, 3)
                        .lookup(POSITION_Y, 2)
                        .orderedLine(0)
                , worldHistory.suppliesFree()
                        .lookup(VALUE, 1)
                        .orderedLine(0));
    }
}
