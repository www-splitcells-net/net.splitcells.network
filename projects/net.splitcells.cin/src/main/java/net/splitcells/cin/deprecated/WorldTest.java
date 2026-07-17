/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.cin.deprecated;

import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.cin.deprecated.World.WORLD_HISTORY;
import static net.splitcells.cin.deprecated.World.worldHistory;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireIllegalDefaultConstructor;
import static net.splitcells.gel.proposal.Proposals.propose;

public class WorldTest {

    @DisabledTest
    @UnitTest
    public void testIllegalConstructor() {
        requireIllegalDefaultConstructor(World.class);
    }

    @DisabledTest
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
