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
package net.splitcells.cin.deprecated.raters;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.Gel;
import net.splitcells.gel.constraint.Constraint;

import static net.splitcells.cin.deprecated.raters.CommitmentAdherence.commitmentAdherence;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.proposal.ProposalProcessor.propose;

public class CommitmentAdherenceTest {

    @DisabledTest
    @UnitTest
    public void test() {
        final var demandingAttribute = attribute(Integer.class, "demandingAttribute");
        final var suppliedAttribute = attribute(Integer.class, "suppliedAttribute");
        final var constraintPath = Lists.<Constraint>list();
        final var testSubject = Gel.defineProblem("testProposeViaConstraintPath")
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
        testSubject.assign(testSubject.demands().orderedLine(0)
                , testSubject.supplies().orderedLine(0));
        testSubject.init();
        final var testProposal = propose(testSubject, constraintPath, testSubject.demands().unorderedLines());
        testProposal.proposedAllocations().demands().unorderedLines().requireSizeOf(1);
        requireEquals(testProposal.proposedAllocations().demands().unorderedLines().get(0).values()
                , testSubject.demands().orderedLine(1).values());

    }
}
