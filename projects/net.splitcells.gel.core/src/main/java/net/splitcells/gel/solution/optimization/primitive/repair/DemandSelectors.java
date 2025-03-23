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
package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.map.Maps;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.data.set.map.Pair.pair;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.constraint.Constraint.*;
import static net.splitcells.gel.proposal.Proposal.*;
import static net.splitcells.gel.proposal.Proposals.propose;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class DemandSelectors {
    private DemandSelectors() {
        throw constructorIllegal();
    }

    public static DemandSelector commitCompliance(DemandSelector demandSelector, Proposal commitment) {
        return (constraint, solution) -> {
            final var baseGrouping = demandSelector.demandGrouping(constraint, solution);
            baseGrouping.values().forEach(group -> {
                final Set<Line> toRemove = setOfUniques();
                group.forEach(g -> {
                    if (commitment.proposedAssignments().unorderedLinesStream2()
                            .anyMatch(c -> c.value(ASSIGNMENT_PROPOSAL_TYPE).equals(PROPOSE_UNCHANGED)
                                    && Thing.equals(c.value(EXISTING_ASSIGNMENT), g)
                            )) {
                        toRemove.add(g);
                    }
                });
                toRemove.forEach(group::delete);
            });
            return baseGrouping;
        };
    }

    /**
     * Builds a demand selector for {@link Solution}s,
     * where {@link GroupId}s are considered,
     * that contain defying {@link Line}s.
     *
     * @param repairCompliance If set to true, selects all {@link Line}s of a given {@link GroupId},
     *                         if it contains at least one defying {@link Line}.
     *                         Otherwise, only defying {@link Line}s are considered.
     * @return
     */
    @Deprecated
    public static DemandSelector demandSelector(boolean repairCompliance) {
        return (Constraint constraintGrouping, Solution solution) -> {
            final Set<Line> selectedDemands = setOfUniques();
            final Map<GroupId, Set<Line>> demandGrouping = map();
            final Map<GroupId, Set<Line>> defianceCache = Maps.map();
            constraintGrouping
                    .lineProcessing()
                    .unorderedLines()
                    .stream()
                    /**
                     * TODO HACK This is code duplication.
                     * It reimplements part of {@link ConstraintGroupBasedRepair#freeDefyingGroupOfConstraintGroup}.
                     */
                    .filter(processing -> {
                        final var group = processing.value(INCOMING_CONSTRAINT_GROUP);
                        return !defianceCache.computeIfAbsent(group, g -> constraintGrouping.defying(g)).isEmpty();
                    })
                    /**
                     * TODO HACK This is code duplication.
                     * It reimplements part of {@link ConstraintGroupBasedOfflineRepair#freeDefyingGroupOfConstraintGroup}.
                     */
                    .filter(processing -> {
                        if (!repairCompliance) {
                            return !processing.value(RATING).equalz(noCost());
                        }
                        return true;
                    })
                    .map(processing -> pair(processing.value(Constraint.RESULTING_CONSTRAINT_GROUP)
                            , processing.value(LINE)))
                    .forEach(processing -> {
                        if (selectedDemands.has(processing.getValue())) {
                            return;
                        }
                        selectedDemands.add(processing.getValue());
                        final Set<Line> group;
                        if (!demandGrouping.containsKey(processing.getKey())) {
                            group = setOfUniques();
                            demandGrouping.put(processing.getKey(), group);
                        } else {
                            group = demandGrouping.get(processing.getKey());
                        }
                        group.with(solution.demandOfAssignment(processing.getValue()));
                    });
            return demandGrouping;
        };
    }

    /**
     * <p>Builds a demand selector for {@link Solution}s,
     * where {@link GroupId}s are considered,
     * that contain defying {@link Line}s.</p>
     * <p>TODO Currently, only demands are considered, which have an entry in {@link Proposal#proposedAllocations()}.
     * via {@link Constraint#propose(Proposal)}.
     * Shouldn't this be configurable?</p>
     * <p>TODO Support {@link Proposal#proposedAssignments()}}.</p>
     *
     * @param restrictingConstraintPath Uses this {@link Query#constraintPath} in order to guide the selection via
     *                                  {@link Constraint#propose(Proposal)}.
     * @return
     */
    public static DemandSelector demandSelector(DemandSelectorsConfig config, List<Constraint> restrictingConstraintPath) {
        return (Constraint constraintGrouping, Solution solution) -> {
            final Map<GroupId, Set<Line>> demandGrouping = map();
            final Map<GroupId, Set<Line>> defianceCache = Maps.map();
            constraintGrouping
                    .lineProcessing()
                    .unorderedLines()
                    .stream()
                    /**
                     * TODO HACK This is code duplication.
                     * It reimplements part of {@link ConstraintGroupBasedRepair#freeDefyingGroupOfConstraintGroup}.
                     */
                    .filter(processing -> {
                        final var group = processing.value(INCOMING_CONSTRAINT_GROUP);
                        return !defianceCache.computeIfAbsent(group, g -> constraintGrouping.defying(g)).isEmpty();
                    })
                    /**
                     * TODO HACK This is code duplication.
                     * It reimplements part of {@link ConstraintGroupBasedOfflineRepair#freeDefyingGroupOfConstraintGroup}.
                     */
                    .filter(processing -> {
                        if (!config.repairCompliants()) {
                            if (config.useCompleteRating()) {
                                return !solution.constraint().rating(processing.value(Constraint.LINE)).equalz(noCost());
                            } else {
                                return !processing.value(RATING).equalz(noCost());
                            }
                        }
                        return true;
                    })
                    .filter(processing -> {
                        final var demandProcessing = solution.demandOfAssignment(processing.value(Constraint.LINE));
                        final var proposal = propose(solution, restrictingConstraintPath, list(demandProcessing));
                        // If true the restrictingConstraintPath does not prohibit allocations for demandProcessing.
                        return proposal
                                .proposedAllocations()
                                .demands()
                                .orderedLines()
                                .stream()
                                .anyMatch(l -> l.equalContents(demandProcessing));
                    })
                    .map(processing -> pair(processing.value(Constraint.RESULTING_CONSTRAINT_GROUP)
                            , processing.value(LINE)))
                    .forEach(processing -> {
                        final Set<Line> group;
                        if (!demandGrouping.containsKey(processing.getKey())) {
                            group = setOfUniques();
                            demandGrouping.put(processing.getKey(), group);
                        } else {
                            group = demandGrouping.get(processing.getKey());
                        }
                        group.with(solution.demandOfAssignment(processing.getValue()));
                    });
            return demandGrouping;
        };
    }
}
