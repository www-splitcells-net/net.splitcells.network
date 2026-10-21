/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.primitive.repair;

import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.constraint.Constraint.*;
import static net.splitcells.gel.data.table.TableEvent.tableEvent;
import static net.splitcells.gel.data.table.history.TableEventType.REMOVAL;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintPathSelectors.constraintPathSelector;
import static net.splitcells.gel.solution.optimization.primitive.repair.RepairConfig.repairConfig;

/**
 * <p>Optimize a problem by find faulty groups and reallocating them.</p>
 * <p>This {@link OnlineOptimization} consists of three phases.</p>
 * <ol>
 *     <li>Select a set of {@link Constraint} paths in the {@link net.splitcells.gel.solution.Solution#constraint} tree
 *     via a {@link RepairConfig#groupSelector()} and therefore decide,
 *     which constraints of the problems are repaired during the optimization.</li>
 *     <li>Determine all {@link GroupId}s in the selected {@link Constraint}s via a {@link RepairConfig#demandSelector()}, that
 *     have a {@link Cost} bigger than zero. Free all demands of these groups.
 *     This deletes the values of all variables, which are part of some constraint defying group.</li>
 *     <li>Set the values to all free demands and
 *     thereby perform the actual repair process via the {@link RepairConfig#supplySelector()}</li>
 * </ol>
 * <p>TODO Create and use {@link Proposal} only for dissatisfying {@link GroupId}.</p>
 * <p>TODO Create and use {@link Proposal} only for certain {@link Constraint} nodes.
 * In other words, search for any kind of relations between {@link Constraint} nodes.</p>
 */
public class ConstraintGroupBasedRepair implements OnlineOptimization {

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair
            (FluentConstraintPathSelector groupSelector, SupplySelector supplySelector, boolean repairCompliants) {
        return new ConstraintGroupBasedRepair(repairConfig()
                .withGroupSelector(groupSelector)
                .withSupplySelector(supplySelector)
                .withRepairCompliance(repairCompliants));
    }

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair
            (FluentConstraintPathSelector groupSelector, SupplySelector supplySelector) {
        return new ConstraintGroupBasedRepair(repairConfig()
                .withGroupSelector(groupSelector)
                .withSupplySelector(supplySelector));
    }

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair
            (FluentConstraintPathSelector groupSelector) {
        return new ConstraintGroupBasedRepair(repairConfig().withGroupSelector(groupSelector));
    }

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair(int minimumConstraintGroupPath) {
        return simpleConstraintGroupBasedRepair(minimumConstraintGroupPath, 1);
    }

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair
            (int minimumConstraintGroupPath, int numberOfGroupsSelectedPerDefiance) {
        val randomness = randomness();
        return new ConstraintGroupBasedRepair
                (repairConfig().withGroupSelector(ConstraintPathSelectors.constraintPathSelector(randomness, minimumConstraintGroupPath
                        , numberOfGroupsSelectedPerDefiance)));
    }

    public static ConstraintGroupBasedRepair constraintGroupBasedRepair(RepairConfig repairConfig) {
        return new ConstraintGroupBasedRepair(repairConfig);
    }

    private final RepairConfig config;


    private ConstraintGroupBasedRepair(RepairConfig argConfig) {
        config = argConfig;
    }

    @Override
    public void optimize(Solution solution) {
        val constraintPaths = constraintPaths(solution);
        final Map<GroupId, Set<Line>> demandGroupings = map();
        constraintPaths.forEach(e -> e
                .lastValue()
                .map(f -> config.demandSelector().demandGrouping(f, solution))
                .ifPresent(cg -> demandGroupings.withMerged(cg, Set::with)));
        constraintPaths
                .forEach(e -> e
                        .lastValue()
                        .ifPresent(f -> freeDefyingGroupOfConstraintGroup(solution, f, demandGroupings)));
        repair(solution, demandGroupings);
    }

    public void repair(Solution solution
            , Map<GroupId, Set<Line>> freeDemandGroups) {
        config.supplySelector().apply(freeDemandGroups).optimize(solution);
    }

    public List<List<Constraint>> constraintPaths(Solution solution) {
        return config.groupSelector().apply(solution.constraint());
    }

    /**
     * TODO Process {@link RepairConfig#freeDefyingGroupOfConstraintGroup()}.
     *
     * @param solution
     * @param constraint
     * @param demandGroupings Its format corresponds to {@link DemandSelector#demandGrouping(Constraint, Solution)}.
     *                        Freed {@link Line}s are add to this.
     */
    public void freeDefyingGroupOfConstraintGroup(Solution solution, Constraint constraint, Map<GroupId, Set<Line>> demandGroupings) {
        val incomingGroups = setOfUniques(constraint
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
                    if (!config.repairCompliance()) {
                        return !constraint
                                .lineProcessing()
                                .columnView(LINE)
                                .lookup(allocation)
                                .unorderedLines()
                                .get(0)
                                .value(RATING)
                                .equalz(noCost());
                    }
                    return true;
                })
                .forEach(alloc -> {
                    val resultingGroup = constraint
                            .lineProcessing()
                            .columnView(LINE)
                            .lookup(alloc)
                            .unorderedLines()
                            .get(0)
                            .value(RESULTING_CONSTRAINT_GROUP);
                    demandGroupings.addIfAbsent(resultingGroup, () -> setOfUniques())
                            .add(solution.demandOfAssignment(alloc));
                    solution.process(tableEvent(alloc, REMOVAL).setReason(tree("Delete defying allocations.")));
                });
    }
}
