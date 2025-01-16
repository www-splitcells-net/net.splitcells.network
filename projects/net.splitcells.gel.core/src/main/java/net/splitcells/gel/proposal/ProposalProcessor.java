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
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.RESULTING_CONSTRAINT_GROUP;
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
        return proposalsForGroups(subject, constraintPath, subject.constraint().lineProcessing().unorderedLines());
    }

    /**
     * The {@link Proposal} are created by submitting a {@link Proposal} to every {@link Constraint},
     * where each submitted {@link Proposal} contains all {@link Line} of one {@link GroupId},
     * located in the respective {@link Constraint}.
     *
     * @param subject         This is the {@link Solution} for which suggestions for improvement are created.
     * @param constraintPath  These suggestions are only create for this path of {@link Constraint#childrenView()}.
     * @param relevantAllocations These {@link Solution#allocations()} are submitted for improvement.
     * @return Creates a list of {@link Proposal}, where one is used for one {@link GroupId}.
     * The {@link GroupId} does not have to be of the root {@link Constraint} node.
     */
    public static List<Proposal> proposalsForGroups(Solution subject, List<Constraint> constraintPath, List<Line> relevantAllocations) {
        final var rootProposal = proposal(subject);
        relevantAllocations.forEach(allocation -> {
            rootProposal.contextAssignments().addTranslated(list(allocation));
        });
        constraintPath.get(0).propose(rootProposal);
        // TODO Initialize proposals correctly.
        final List<Map<GroupId, Proposal>> proposals = list(
                Maps.<GroupId, Proposal>map().with(constraintPath.get(0).injectionGroup(), rootProposal));
        for (int i = 1; i < constraintPath.size(); ++i) {
            final var previousProposal = proposals.get(i - 1);
            final Map<GroupId, Proposal> currentProposals = map();
            final var currentConstraint = constraintPath.get(i);
            previousProposal.entrySet().forEach(p -> {
                currentConstraint.lineProcessing()
                        .columnView(INCOMING_CONSTRAINT_GROUP).lookup(p.getKey())
                        .columnView(RESULTING_CONSTRAINT_GROUP).lookup(a -> true)
                        .unorderedLinesStream()
                        .forEach(l -> {
                            final var resultingGroup = l.value(RESULTING_CONSTRAINT_GROUP);
                            final Proposal proposal;
                            if (!currentProposals.containsKey(resultingGroup)) {
                                currentProposals.put(resultingGroup, proposal(subject));
                            }
                            proposal = currentProposals.get(resultingGroup);
                            proposal.contextAssignments().addTranslated(list(l));
                        });
            });
            proposals.add(currentProposals);
            currentProposals.values().forEach(currentConstraint::propose);
        }
        throw notImplementedYet();
    }
}
