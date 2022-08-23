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
import net.splitcells.dem.data.set.map.Maps;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.data.set.map.Pair.pair;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.constraint.Constraint.RATING;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.optimization.primitive.repair.GroupSelectors.groupSelector;

/**
 * <p>
 * Optimize a problem by find faulty groups and reallocating them.
 * </p>
 * <p>
 * “When the Japanese mend broken objects, they aggrandize the damage by filling the cracks with gold.
 * They believe that when something's suffered damage and has a history it becomes more beautiful.”
 * ― Barbara Bloom
 * </p>
 * <p>This {@link OnlineOptimization} consists of three phases.</p>
 * <ol>
 *     <li>Select a set of {@link Constraint} paths in the {@link net.splitcells.gel.solution.Solution#constraint} tree
 *     via a {@link #groupSelector}
 *     and therefore decide, which constraints of the problems are repaired during the optimization.
 *     </li>
 *     <li>Determine all {@link GroupId}s in the selected {@link Constraint}s via a {@link #supplySelector}, that
 *     have a {@link Cost} bigger than zero. Free all demands of these groups.
 *     This deletes the values of all variables, which are part of some constraint defying group.</li>
 *     <li>Set the values to all free demands and thereby perform actual repair process.</li>
 * </ol>
 */
public class ConstraintGroupBasedRepair implements OnlineOptimization {

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair
            (FluentGroupSelector groupSelector, SupplySelector repairer, boolean repairCompliants) {
        return new ConstraintGroupBasedRepair(groupSelector, repairer, repairCompliants);
    }

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair
            (FluentGroupSelector groupSelector, SupplySelector repairer) {
        return new ConstraintGroupBasedRepair(groupSelector, repairer);
    }

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair
            (FluentGroupSelector groupSelector) {
        return new ConstraintGroupBasedRepair(groupSelector, SupplySelectors.supplySelector());
    }

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair(int minimumConstraintGroupPath) {
        return simpleConstraintGroupBasedRepair(minimumConstraintGroupPath, 1);
    }

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair
            (int minimum_constraint_group_path, int numberOfGroupsSelectedPerDefiance) {
        final var randomness = randomness();
        return new ConstraintGroupBasedRepair(groupSelector(randomness, minimum_constraint_group_path
                , numberOfGroupsSelectedPerDefiance)
                , SupplySelectors.supplySelector());
    }

    private final FluentGroupSelector groupSelector;
    private final SupplySelector supplySelector;
    private final boolean repairCompliants;

    protected ConstraintGroupBasedRepair(FluentGroupSelector groupSelector, SupplySelector repairer) {
        this(groupSelector, repairer, true);
    }

    protected ConstraintGroupBasedRepair(FluentGroupSelector groupSelector, SupplySelector repairer, boolean repairCompliants) {
        this.groupSelector = groupSelector;
        this.supplySelector = repairer;
        this.repairCompliants = repairCompliants;
    }

    @Override
    public void optimize(Solution solution) {
        final var groupsOfConstraintGroup = groupOfConstraintGroup(solution);
        final var demandGroupings = groupsOfConstraintGroup
                .stream()
                .map(e -> e
                        .lastValue()
                        .map(f -> demandGrouping(f, solution))
                        .orElseGet(() -> map()))
                .collect(toList());
        groupsOfConstraintGroup
                .forEach(e -> e
                        .lastValue()
                        .ifPresent(f -> freeDefyingGroupOfConstraintGroup(solution, f)));
        final var demandGrouping = demandGroupings
                .stream()
                .reduce(map(), (a, b) -> a.withMerged(b, Set::with));
        repair(solution, demandGrouping);
    }

    public void repair(Solution solution
            , Map<GroupId, Set<Line>> freeDemandGroups) {
        supplySelector.apply(freeDemandGroups).optimize(solution);
    }

    public Map<GroupId, Set<Line>> demandGrouping(Constraint constraintGrouping, Solution solution) {
        final Map<GroupId, Set<Line>> demandGrouping = map();
        final Map<GroupId, Set<Line>> defianceCache = Maps.map();
        constraintGrouping
                .lineProcessing()
                .lines()
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

    public List<List<Constraint>> groupOfConstraintGroup(Solution solution) {
        return groupSelector.apply(solution.constraint());
    }

    public void freeDefyingGroupOfConstraintGroup(Solution solution, Constraint constraint) {
        final var incomingGroups = Sets.setOfUniques
                (constraint
                        .lineProcessing()
                        .columnView(INCOMING_CONSTRAINT_GROUP)
                        .values());
        incomingGroups
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
                .forEach(solution::remove);
    }
}
