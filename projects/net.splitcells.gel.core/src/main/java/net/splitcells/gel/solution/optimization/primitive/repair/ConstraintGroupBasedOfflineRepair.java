/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.map.Pair.pair;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.constraint.Constraint.*;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;
import static net.splitcells.gel.solution.optimization.primitive.repair.GroupSelectors.groupSelector;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>This class is not actively developed anymore.
 * See {@link ConstraintGroupBasedRepair}.
 * The main reason for deprecation is the fact, that the {@link #supplyOfflineSelector} requires complex calculations in order to determine the current state of {@link Solution} after applying the demand freeing via the {@link #groupSelector} in this complex {@link OfflineOptimization}.
 * </p>
 * <p>
 * Optimize a problem by find faulty groups and reallocating them.
 * </p>
 * <p>
 * “When the Japanese mend broken objects, they aggrandize the damage by filling the cracks with gold.
 * They believe that when something's suffered damage and has a history it becomes more beautiful.”
 * ― Barbara Bloom
 * </p>
 * <p>This {@link OfflineOptimization} consists of three phases.</p>
 * <ol>
 *     <li>Select a set of {@link Constraint} in the {@link net.splitcells.gel.solution.Solution#constraint} tree
 *     and therefore decide, which constraints of the problems are repaired during the optimization.
 *     </li>
 *     <li>Determine all {@link GroupId}s in the selected {@link Constraint}s, that
 *     have a {@link Cost} bigger than zero. Free all demands of these groups.
 *     This deletes the values of all variables, which are part of some constraint defying group.</li>
 *     <li>Set the values to all free demands and thereby perform actual repair process.</li>
 * </ol>
 */
@Deprecated
public class ConstraintGroupBasedOfflineRepair implements OfflineOptimization {

    public static ConstraintGroupBasedOfflineRepair simpleConstraintGroupBasedOfflineRepair
            (GroupSelector groupSelector, SupplyOfflineSelector repairer) {
        return new ConstraintGroupBasedOfflineRepair(groupSelector, repairer);
    }

    public static ConstraintGroupBasedOfflineRepair simpleConstraintGroupBasedOfflineRepair
            (GroupSelector groupSelector, SupplyOfflineSelector repairer, boolean repairCompliants) {
        return new ConstraintGroupBasedOfflineRepair(groupSelector, repairer, repairCompliants);
    }

    public static ConstraintGroupBasedOfflineRepair simpleConstraintGroupBasedOfflineRepair
            (GroupSelector groupSelector) {
        return new ConstraintGroupBasedOfflineRepair(groupSelector, SupplyOfflineSelectors.supplySelector());
    }

    public static ConstraintGroupBasedOfflineRepair simpleConstraintGroupBasedOfflineRepair(int minimumConstraintGroupPath) {
        return simpleConstraintGroupBasedOfflineRepair(minimumConstraintGroupPath, 1);
    }

    public static ConstraintGroupBasedOfflineRepair simpleConstraintGroupBasedOfflineRepair
            (int minimum_constraint_group_path, int numberOfGroupsSelectedPerDefiance) {
        final var randomness = randomness();
        return new ConstraintGroupBasedOfflineRepair(groupSelector(randomness, minimum_constraint_group_path
                , numberOfGroupsSelectedPerDefiance)
                , SupplyOfflineSelectors.supplySelector());
    }

    private final GroupSelector groupSelector;
    private final SupplyOfflineSelector supplyOfflineSelector;
    private final boolean repairCompliants;

    protected ConstraintGroupBasedOfflineRepair(GroupSelector groupSelector, SupplyOfflineSelector repairer) {
        this(groupSelector, repairer, true);
    }

    protected ConstraintGroupBasedOfflineRepair(GroupSelector groupSelector, SupplyOfflineSelector repairer, boolean repairCompliants) {
        this.groupSelector = groupSelector;
        this.supplyOfflineSelector = repairer;
        this.repairCompliants = repairCompliants;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final var groupsOfConstraintGroup = groupOfConstraintGroup(solution);
        final var demandGroupings = groupsOfConstraintGroup
                .stream()
                .map(e -> e
                        .lastValue()
                        .map(f -> demandGrouping(f, solution))
                        .orElseGet(() -> map()))
                .collect(toList());
        final var demandGrouping = demandGroupings
                .stream()
                .reduce(map(), (a, b) -> a.withMerged(b, Set::with));
        final var demandFreeing = groupsOfConstraintGroup
                .stream()
                .map(e -> e
                        .lastValue()
                        .map(f -> freeDefyingGroupOfConstraintGroup(solution, f))
                        .orElseGet(() -> list()))
                .flatMap(e -> e.stream())
                .distinct()
                .collect(toList());
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            demandFreeing.forEach(e -> {
                if (!REMOVAL.equals(e.stepType())) {
                    throw new IllegalStateException();
                }
            });
        }
        final var optimization = demandFreeing;
        optimization.addAll(repair(solution, demandGrouping,
                demandFreeing.stream()
                        .map(e -> e.supply().interpret().get())
                        .collect(toList())));
        return optimization;
    }

    public List<OptimizationEvent> repair(SolutionView solution
            , Map<GroupId, Set<Line>> freeDemandGroups
            , List<Line> freedSupplies) {
        return supplyOfflineSelector.apply(freeDemandGroups, freedSupplies).optimize(solution);
    }

    public Map<GroupId, Set<Line>> demandGrouping(Constraint constraintGrouping, SolutionView solution) {
        final Map<GroupId, Set<Line>> demandGrouping = map();
        constraintGrouping
                .lineProcessing()
                .lines()
                .stream()
                /**
                 * TODO HACK This is code duplication.
                 * It reimplements part of {@link ConstraintGroupBasedOfflineRepair#freeDefyingGroupOfConstraintGroup}.
                 */
                .filter(processing -> !constraintGrouping
                        .defying(processing.value(INCOMING_CONSTRAINT_GROUP))
                        .isEmpty())
                /**
                 * TODO HACK This is code duplication.
                 * It reimplements part of {@link ConstraintGroupBasedOfflineRepair#freeDefyingGroupOfConstraintGroup}.
                 */
                .filter(processing -> {
                    if (!repairCompliants) {
                        return !processing.value(RATING).equalz(noCost());
                    }
                    return true;
                })
                .map(processing -> pair(processing.value(Constraint.RESULTING_CONSTRAINT_GROUP)
                        , processing.value(LINE)))
                .forEach(processing -> {
                    final Set<Line> group;
                    if (!demandGrouping.containsKey(processing.getKey())) {
                        group = Sets.setOfUniques();
                        demandGrouping.put(processing.getKey(), group);
                    } else {
                        group = demandGrouping.get(processing.getKey());
                    }
                    group.with(solution.demandOfAllocation(processing.getValue()));
                });
        return demandGrouping;
    }

    public List<List<Constraint>> groupOfConstraintGroup(SolutionView solution) {
        return groupSelector.apply(Constraint.allocationGroups(solution.constraint()));
    }

    public List<OptimizationEvent> freeDefyingGroupOfConstraintGroup(SolutionView solution, Constraint constraint) {
        final var incomingGroups = Sets.setOfUniques
                (constraint
                        .lineProcessing()
                        .columnView(INCOMING_CONSTRAINT_GROUP)
                        .values());
        final var defyingGroup = incomingGroups
                .stream()
                .filter(group -> !constraint.defying(group).isEmpty())
                .count();
        return incomingGroups
                .stream()
                .filter(group -> !constraint.defying(group).isEmpty())
                .map(group -> constraint
                        .lineProcessing()
                        .columnView(INCOMING_CONSTRAINT_GROUP)
                        .lookup(group)
                        .columnView(LINE)
                        .values())
                .flatMap(streamOfLineList -> streamOfLineList.stream())
                .distinct()
                .filter(allocation -> {
                    if (!repairCompliants) {
                        return !constraint
                                .lineProcessing()
                                .columnView(LINE)
                                .lookup(allocation)
                                .lines()
                                .get(0)
                                .value(RATING)
                                .equalz(noCost());
                    }
                    return true;
                })
                .map(allocation -> optimizationEvent
                        (REMOVAL
                                , solution.demandOfAllocation(allocation).toLinePointer()
                                , solution.supplyOfAllocation(allocation).toLinePointer()))
                .collect(toList());
    }
}
