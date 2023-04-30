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
package net.splitcells.gel.proposal;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.Gel;
import net.splitcells.gel.constraint.Constraint;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireIllegalDefaultConstructor;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.proposal.ProposalProcessor.propose;
import static net.splitcells.gel.rating.rater.lib.AllSame.allSame;

public class ProposalProcessorTest {
    @UnitTest
    public void testIllegalConstructor() {
        requireIllegalDefaultConstructor(ProposalProcessor.class);
    }

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
                .withSupplies(list(list(2, 3), list(2, 3), list(2, 4)))
                .withConstraint(q -> {
                    constraintPath.addAll(
                            q.forAll(demandingAttribute)
                                    .forAll(firstSuppliedAttribute)
                                    .then(allSame(secondSuppliedAttribute)).constraintPath());
                    return q;
                })
                .toProblem()
                .asSolution();
        testSubject.allocate(testSubject.demands().orderedLine(0)
                , testSubject.supplies().orderedLine(0));
        final var testProposal = propose(testSubject, constraintPath, testSubject.demandsFree().unorderedLines());
        testProposal.proposedAllocations().unorderedLines().requireSizeOf(1);
        final var proposedAllocation = testProposal.proposedAllocations().unorderedLinesStream().findFirst().orElseThrow();
        proposedAllocation.values().requireEqualityTo(list(1, 2, 3));
    }
}
