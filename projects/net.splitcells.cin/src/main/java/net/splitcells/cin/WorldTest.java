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
package net.splitcells.cin;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.cin.World.WORLD_HISTORY;
import static net.splitcells.cin.World.worldHistory;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireIllegalDefaultConstructor;
import static net.splitcells.gel.proposal.ProposalProcessor.propose;

public class WorldTest {

    @UnitTest
    public void testIllegalConstructor() {
        requireIllegalDefaultConstructor(World.class);
    }

    @UnitTest
    public void testProposalByCommitment() {
        final var testSubject = worldHistory(WORLD_HISTORY, list(), list());
        testSubject.demands().addTranslated(list(0, 0, 0));
        testSubject.demands().addTranslated(list(1, 0, 0));
        testSubject.supplies().addTranslated(list(0));
        testSubject.supplies().addTranslated(list(0));
        testSubject.assign(testSubject.demands().orderedLine(0)
                , testSubject.supplies().orderedLine(0));
        testSubject.init();
        final var testProposal = propose(testSubject
                , list(testSubject.constraint(), testSubject.constraint().child(0))
                , testSubject.demands().unorderedLines());
        testProposal.proposedAllocations().demands().unorderedLines().requireSizeOf(1);
        requireEquals(testProposal.proposedAllocations().demands().orderedLine(0).values()
                , testSubject.demands().orderedLine(1).values());
    }
}
