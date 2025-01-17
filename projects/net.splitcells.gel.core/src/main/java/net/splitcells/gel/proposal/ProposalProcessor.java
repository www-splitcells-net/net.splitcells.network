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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.solution.Solution;

import java.util.stream.Stream;

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
     * @param subject             This is the {@link Solution} for which suggestions for improvement are created.
     * @param constraintPath      These suggestions are only create for this path of {@link Constraint#childrenView()}.
     * @param relevantAllocations These {@link Solution#allocations()} are submitted for improvement.
     * @return Creates a list of {@link Proposal}, where one is used for one {@link GroupId}.
     * The {@link GroupId} does not have to be of the root {@link Constraint} node.
     */
    public static List<Proposal> proposalsForGroups(Solution subject, List<Constraint> constraintPath, List<Line> relevantAllocations) {
        final List<Map<GroupId, Proposal>> proposals = list();
        final var rootConstraint = constraintPath.get(0);
        proposals.add(proposalForGroup(subject
                , Sets.setOfUniques(rootConstraint.injectionGroup())
                , rootConstraint.lineProcessing().unorderedLinesStream()
                , rootConstraint));
        for (int i = 1; i < constraintPath.size(); ++i) {
            proposals.add(proposalForGroup(subject
                    , proposals.get(i - 1).keySet2()
                    , constraintPath.get(i).lineProcessing().unorderedLinesStream()
                    , constraintPath.get(i)));
        }
        return proposals.flow().map(Map::valueList).reduce(List::withAppended).orElseThrow();
    }

    private static Map<GroupId, Proposal> proposalForGroup(Solution subject, Set<GroupId> incomingGroups, Stream<Line> lineProcessing, Constraint currentConstraint) {
        return proposalForGroup(subject, lineProcessing.filter(l -> incomingGroups.has(l.value(INCOMING_CONSTRAINT_GROUP)))
                , currentConstraint);
    }

    private static Map<GroupId, Proposal> proposalForGroup(Solution subject, Stream<Line> lineProcessing, Constraint currentConstraint) {
        final Map<GroupId, Proposal> currentProposals = map();
        lineProcessing.forEach(l -> {
            final var resultingGroup = l.value(RESULTING_CONSTRAINT_GROUP);
            final Proposal proposal;
            if (!currentProposals.containsKey(resultingGroup)) {
                currentProposals.put(resultingGroup, proposal(subject));
            }
            proposal = currentProposals.get(resultingGroup);
            proposal.contextAssignments().addTranslated(list(l));
        });
        currentProposals.values().forEach(currentConstraint::propose);
        return currentProposals;
    }
}
