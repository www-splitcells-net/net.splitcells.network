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
import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.Gel;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.proposal.Proposal;

import static net.splitcells.cin.raters.CommitmentAdherence.commitmentAdherence;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.proposal.Proposal.*;
import static net.splitcells.gel.proposal.Proposals.proposalsForGroups;
import static net.splitcells.gel.proposal.Proposals.propose;

public class CommitmentAdherenceTest {

    @UnitTest
    public void test() {
        final var demandingAttribute = attribute(Integer.class, "demandingAttribute");
        final var suppliedAttribute = attribute(Integer.class, "suppliedAttribute");
        final var constraintPath = Lists.<Constraint>list();
        final var testSubject = defineProblem("testProposeViaConstraintPath")
                .withDemandAttributes(demandingAttribute)
                .withDemands(list(list(1), list(2)))
                .withSupplyAttributes(suppliedAttribute)
                .withSupplies(list(list(2), list(2)))
                .withConstraint(q -> {
                    constraintPath.addAll(
                            q.then(commitmentAdherence(demandingAttribute)).constraintPath());
                    return q;
                })
                .toProblem()
                .asSolution();
        testSubject.init();
        testSubject.assign(testSubject.demands().orderedLine(0)
                , testSubject.supplies().orderedLine(0));
        testSubject.init();
        final var testProposal1 = proposalsForGroups(testSubject, constraintPath);
        testProposal1.get(1).contextAssignments().unorderedLines().requireSizeOf(1);
        testProposal1.get(1).proposedAssignments().unorderedLines().requireSizeOf(1);
        final var proposedAssignment1 = testProposal1.get(1).proposedAssignments().unorderedLines().get(0);
        requireEquals(proposedAssignment1.value(ASSIGNMENT_PROPOSAL_TYPE), PROPOSE_UNCHANGED);
        testSubject.headerView().forEach(attribute -> {
            if (demandingAttribute.equals(attribute)) {
                requireEquals(proposedAssignment1.value(attribute), 1);
            } else {
                requireEquals(proposedAssignment1.value(attribute), null);
            }
        });
        testSubject.assign(testSubject.demands().orderedLine(1)
                , testSubject.supplies().orderedLine(1));
        final var testProposal2 = proposalsForGroups(testSubject, constraintPath);
        testProposal2.get(1).contextAssignments().unorderedLines().requireSizeOf(2);
        testProposal2.get(1).proposedAssignments().unorderedLines().requireSizeOf(1);
        final var proposedAssignment2 = testProposal2.get(1).proposedAssignments().unorderedLines().get(0);
        requireEquals(proposedAssignment2.value(ASSIGNMENT_PROPOSAL_TYPE), PROPOSE_UNCHANGED);
        testSubject.headerView().forEach(attribute -> {
            if (demandingAttribute.equals(attribute)) {
                requireEquals(proposedAssignment2.value(attribute), 1);
            } else {
                requireEquals(proposedAssignment2.value(attribute), null);
            }
        });
        testSubject.init();
        final var testProposal3 = proposalsForGroups(testSubject, constraintPath);
        testProposal3.get(1).contextAssignments().unorderedLines().requireSizeOf(2);
        testProposal3.get(1).proposedAssignments().unorderedLines().requireSizeOf(2);
        final var proposedAssignment31 = testProposal3.get(1).proposedAssignments().unorderedLines().get(0);
        final var proposedAssignment32 = testProposal3.get(1).proposedAssignments().unorderedLines().get(1);
        requireEquals(proposedAssignment31.value(ASSIGNMENT_PROPOSAL_TYPE), PROPOSE_UNCHANGED);
        requireEquals(proposedAssignment32.value(ASSIGNMENT_PROPOSAL_TYPE), PROPOSE_UNCHANGED);
        testSubject.headerView().forEach(attribute -> {
            if (demandingAttribute.equals(attribute)) {
                requireEquals(proposedAssignment31.value(attribute), 1);
            } else {
                requireEquals(proposedAssignment31.value(attribute), null);
            }
        });
        testSubject.headerView().forEach(attribute -> {
            if (demandingAttribute.equals(attribute)) {
                requireEquals(proposedAssignment32.value(attribute), 2);
            } else {
                requireEquals(proposedAssignment32.value(attribute), null);
            }
        });
    }
}
