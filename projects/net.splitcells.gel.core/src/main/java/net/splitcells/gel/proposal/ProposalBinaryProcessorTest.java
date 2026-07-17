/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.proposal;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.Gel;
import net.splitcells.gel.constraint.Constraint;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.proposal.Proposals.propose;
import static net.splitcells.gel.rating.rater.lib.AllSame.allSame;

public class ProposalBinaryProcessorTest {

    @UnitTest
    public void testProposeViaConstraintPath() {
        final var demandingAttribute = attribute(Integer.class, "demandingAttribute");
        final var firstSuppliedAttribute = attribute(Integer.class, "firstSuppliedAttribute");
        final var secondSuppliedAttribute = attribute(Integer.class, "secondSuppliedAttribute");
        final var constraintPath = Lists.<Constraint>list();
        final var testSubject = Gel.defineProblem("testProposeViaConstraintPath")
                .withDemandAttributes(demandingAttribute)
                .withDemands(list(list(1), list(1)))
                .withSupplyAttributes(firstSuppliedAttribute, secondSuppliedAttribute)
                .withSupplies(list(list(2, 3), list(2, 4), list(2, 3), list(2, 4)))
                .withConstraint(q -> {
                    constraintPath.addAll(
                            q.forAll(demandingAttribute)
                                    .forAll(firstSuppliedAttribute)
                                    .then(allSame(secondSuppliedAttribute)).constraintPath());
                    return q;
                })
                .toProblem()
                .asSolution();
        testSubject.assign(testSubject.demands().orderedLine(0)
                , testSubject.supplies().orderedLine(0));
        final var testProposal = propose(testSubject, constraintPath, testSubject.demandsFree().unorderedLines());
        testProposal.proposedAllocations().unorderedLines().requireSizeOf(1);
        final var proposedAllocation = testProposal.proposedAllocations().unorderedLinesStream().findFirst().orElseThrow();
        proposedAllocation.values().requireEqualityTo(list(1, 2, 3));
    }
}
