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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.map.Maps;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.proposal.Proposals.proposal;

public class ProposalProcessor {
    private ProposalProcessor() {
        throw constructorIllegal();
    }

    /**
     * TODO This is a simplified implementation, that does not working on many corner cases.
     * For instance, {@link GroupId} are not considered at all.
     *
     * @param subject
     * @param constraintPath
     * @param relevantDemands
     * @return
     */
    @Deprecated
    public static Proposal propose(Solution subject, List<Constraint> constraintPath, List<Line> relevantDemands) {
        final var proposal = proposal(subject);
        subject.allocations().unorderedLinesStream()
                .forEach(a -> {
                    final var origDemand = subject.allocations().demandOfAssignment(a);
                    final var origSupply = subject.allocations().supplyOfAssignment(a);
                    final var demand = proposal.contextAllocationsOld().demands().add(origDemand);
                    final var supply = proposal.contextAllocationsOld().supplies().add(origSupply);
                    proposal.contextAllocationsOld().assign(demand, supply);
                });
        relevantDemands.forEach(d -> proposal.proposedAllocations().demands().add(d));
        constraintPath.forEach(constraint -> constraint.propose(proposal));
        return proposal;
    }

    public static List<Proposal> proposalsForGroups(Solution subject, List<Constraint> constraintPath) {
        return proposalsForGroups(subject, constraintPath, subject.demands().unorderedLines());
    }

    /**
     * The {@link Proposal} are created by submitting a {@link Proposal} to every {@link Constraint},
     * where each submitted {@link Proposal} contains all {@link Line} of one {@link GroupId},
     * located in the respective {@link Constraint}.
     *
     * @param subject         This is the {@link Solution} for which suggestions for improvement are created.
     * @param constraintPath  These suggestions are only create for this path of {@link Constraint#childrenView()}.
     * @param relevantDemands These demands are submitted for improvement.
     * @return Creates a list of {@link Proposal}, where one is used for one {@link GroupId}.
     * The {@link GroupId} does not have to be of the root {@link Constraint} node.
     */
    public static List<Proposal> proposalsForGroups(Solution subject, List<Constraint> constraintPath, List<Line> relevantDemands) {
        final var rootProposal = proposal(subject);
        subject.allocations().unorderedLinesStream()
                .forEach(allocation -> {
                    final var origDemand = subject.allocations().demandOfAssignment(allocation);
                    final var origSupply = subject.allocations().supplyOfAssignment(allocation);
                    final var demand = rootProposal.contextAllocationsOld().demands().add(origDemand);
                    final var supply = rootProposal.contextAllocationsOld().supplies().add(origSupply);
                    rootProposal.contextAllocationsOld().assign(demand, supply);
                    rootProposal.contextAssignments().addTranslated(list(allocation));
                });
        relevantDemands.forEach(d -> rootProposal.proposedAllocations().demands().add(d));
        constraintPath.get(0).propose(rootProposal);
        Map<GroupId, Proposal> currentProposal = Maps.<GroupId, Proposal>map().with(constraintPath.get(0).injectionGroup(), rootProposal);
        final List<Map<GroupId, Proposal>> proposals = list(currentProposal);
        for (int i = 1; i < constraintPath.size(); ++i) {
            final var previousProposal = proposals.get(i - 1);
            currentProposal = map();
            final var currentConstraint = constraintPath.get(i);
            previousProposal.entrySet().forEach(p -> {
                // TODO
            });
            proposals.add(currentProposal);
            currentProposal.values().forEach(currentConstraint::propose);
        }
        throw notImplementedYet();
    }
}
