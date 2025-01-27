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

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.SetT;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.solution.Solution;

import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.constraint.Constraint.*;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.table.Tables.table2;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;

public class Proposals implements Proposal {

    public static Proposal proposalsForConstraintTree(Solution subject) {
        return proposalsForConstraintTree(subject, subject.constraint(), subject.unorderedLinesStream2());
    }

    public static Proposal proposalsForConstraintTree(Solution subject, Constraint constraint, Flow<Line> lineProcessing) {
        final List<Map<GroupId, Proposal>> proposals = list();
        final var proposal = proposal(subject);
        lineProcessing.forEach(lp ->
                proposal.contextAssignments().addTranslated(list(lp.value(LINE)))
        );
        // TODO
        return proposal;
    }

    public static List<Proposal> proposalsForGroups(Solution subject, List<Constraint> constraintPath) {
        return proposalsForGroups(subject, constraintPath, subject.unorderedLines());
    }

    /**
     * <p>The {@link Proposal} are created by submitting a {@link Proposal} to every {@link Constraint},
     * where each submitted {@link Proposal} contains all {@link Line} of one {@link GroupId},
     * located in the respective {@link Constraint}.</p>
     * <p>Note on the implementation: there is no check,
     * if a {@link Line} is present in a {@link Constraint#childrenView()} explicitly.
     * It this is implicitly done by checking {@link Constraint#INCOMING_CONSTRAINT_GROUP} and
     * {@link Constraint#RESULTING_CONSTRAINT_GROUP},
     * as in this case {@link Line} are not present in {@link Constraint#childrenView()}.</p>
     *
     * @param subject             This is the {@link Solution} for which suggestions for improvement are created.
     * @param constraintPath      These suggestions are only create for this path of {@link Constraint#childrenView()}.
     * @param relevantAllocations These {@link Solution#allocations()} are submitted for improvement.
     * @return Creates a list of {@link Proposal}, where one is used for one {@link GroupId}.
     * The {@link GroupId} does not have to be of the root {@link Constraint} node.
     * There is no attempt being made to resolve conflicts between the returned {@link Proposal}.
     */
    public static List<Proposal> proposalsForGroups(Solution subject, List<Constraint> constraintPath, SetT<Line> relevantAllocations) {
        final List<Map<GroupId, Proposal>> proposals = list();
        // The list is used, in order to ensure, that the proposals of one constraint are never after the proposals of later constraints.
        final List<Proposal> proposalsForGroups = list();
        final var rootConstraint = constraintPath.get(0);
        proposals.add(proposalForGroup(subject
                , Sets.setOfUniques(rootConstraint.injectionGroup())
                , rootConstraint.lineProcessing().unorderedLinesStream().filter(lp -> relevantAllocations.contains(lp.value(LINE)))
                , rootConstraint));
        proposalsForGroups.addAll(proposals.get(0).values());
        for (int i = 1; i < constraintPath.size(); ++i) {
            proposals.add(proposalForGroup(subject
                    , proposals.get(i - 1).keySet2()
                    , constraintPath.get(i).lineProcessing().unorderedLinesStream()
                    , constraintPath.get(i)));
            proposalsForGroups.addAll(proposals.get(i).values());
        }
        return proposalsForGroups;
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
            proposal.contextAssignments().addTranslated(list(l.value(LINE)));
        });
        currentProposals.values().forEach(currentConstraint::propose);
        return currentProposals;
    }

    public static Proposal proposal(Solution subject) {
        return new Proposals(subject);
    }

    private final Solution subject;
    private final Assignments proposedAllocations;
    private final Assignments contextAllocations;
    private final Table proposedAssignments;
    private final Table contextAssignments;
    private final Table proposedAllocationsWithNewSupplies;
    private final Table proposedDisallocations;

    private Proposals(Solution subject) {
        this.subject = subject;
        this.proposedAllocations = assignments("proposed-allocations-old"
                , table("proposed-demands", subject.demands(), subject.demands().headerView2())
                , table("proposed-supplies", subject.supplies(), subject.supplies().headerView2()));
        this.contextAllocations = assignments("old-context-allocations"
                , table("proposed-demands", subject.demands(), subject.demands().headerView2())
                , table("proposed-supplies", subject.supplies(), subject.supplies().headerView2()));
        contextAssignments = table("context-assignments", subject.demands(), list(CONTEXT_ASSIGNMENT));
        proposedDisallocations = table("proposed-disallocations", subject.demands(), list(EXISTING_ASSIGNMENT));
        proposedAllocationsWithNewSupplies = table("proposed-allocations-with-new-supplies"
                , subject.demands()
                , Lists.<Attribute<? extends Object>>list().withAppended(EXISTING_DEMAND, NEW_SUPPLY_BASE)
                        .withAppended(subject.supplies().headerView2()));
        proposedAssignments = table("proposed-assignments"
                , subject.demands()
                , Lists.<Attribute<? extends Object>>list()
                        .withAppended(ASSIGNMENT_PROPOSAL_TYPE, EXISTING_ASSIGNMENT, EXISTING_DEMAND, EXISTING_SUPPLY)
                        .withAppended(subject.demands().headerView2())
                        .withAppended(subject.supplies().headerView2()));
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

    /**
     * @return These {@link Assignments} are proposed in order to get a better {@link Solution}.
     */
    @Override
    public Assignments proposedAllocations() {
        return proposedAllocations;
    }

    @Override
    public Table proposedAllocationsWithSupplies() {
        return proposedAllocationsWithNewSupplies;
    }

    @Override
    public Table proposedAssignments() {
        return proposedAssignments;
    }

    @Override
    public Table proposedDisallocations() {
        return proposedDisallocations;
    }

    @Override
    public Assignments contextAllocationsOld() {
        return contextAllocations;
    }

    @Override
    public Table contextAssignments() {
        return contextAssignments;
    }

    /**
     * @return The {@link Solution} for which this is generated for.
     */
    @Override
    public Solution subject() {
        return subject;
    }
}
