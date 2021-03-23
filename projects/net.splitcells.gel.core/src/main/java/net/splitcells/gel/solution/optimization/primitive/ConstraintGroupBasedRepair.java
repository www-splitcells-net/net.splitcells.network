package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.resource.host.interaction.Domsole;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.Sets.*;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.map.Pair.pair;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.constraint.Constraint.*;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;
import static net.splitcells.gel.solution.optimization.primitive.SupplySelection.supplySelection;

public class ConstraintGroupBasedRepair implements Optimization {

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair
            (Function<List<List<Constraint>>, List<List<Constraint>>> groupSelector
                    , BiFunction<Map<GroupId, Set<Line>>, List<Line>, Optimization> repairer) {
        return new ConstraintGroupBasedRepair(groupSelector, repairer);
    }

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair
            (Function<List<List<Constraint>>, List<List<Constraint>>> groupSelector) {
        return new ConstraintGroupBasedRepair(groupSelector, supplySelector());
    }

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair(int minimumConstraintGroupPath) {
        return simpleConstraintGroupBasedRepair(minimumConstraintGroupPath, 1);
    }

    public static ConstraintGroupBasedRepair simpleConstraintGroupBasedRepair
            (int minimum_constraint_group_path
                    , int numberOfGroupsSelectedPerDefiance) {
        final var randomness = randomness();
        return new ConstraintGroupBasedRepair
                (allocationsGroups -> {
                    final var candidates = allocationsGroups
                            .stream()
                            .filter(allocationGroupsPath ->
                                    {
                                        if (allocationGroupsPath.size() < minimum_constraint_group_path) {
                                            return false;
                                        }
                                        return incomingGroupsOfConstraintPath(allocationGroupsPath.shallowCopy())
                                                .stream()
                                                .map(group -> !allocationGroupsPath
                                                        .lastValue()
                                                        .get()
                                                        .defying(group)
                                                        .isEmpty())
                                                .reduce((a, b) -> a || b)
                                                .orElse(false);
                                    }
                            )
                            .collect(toList());
                    if (candidates.isEmpty()) {
                        return list();
                    }
                    return randomness.chooseAtMostMultipleOf(numberOfGroupsSelectedPerDefiance, candidates);
                }, supplySelector());
    }

    private static final BiFunction<Map<GroupId, Set<Line>>, List<Line>, Optimization> supplySelector() {
        final var randomness = randomness();
        return indexBasedRepairer((freeSupplyCount, supplyFreedCount) -> {
            if (freeSupplyCount.floatValue() + supplyFreedCount.floatValue() <= 0) {
                return Optional.empty();
            }
            if (randomness.truthValue(freeSupplyCount.floatValue() / (freeSupplyCount.floatValue() + supplyFreedCount.floatValue()))) {
                return Optional.of(supplySelection(randomness.integer(0, freeSupplyCount - 1), true));
            } else {
                return Optional.of(supplySelection(randomness.integer(0, supplyFreedCount - 1), false));
            }
        });
    }

    public static final BiFunction<Map<GroupId, Set<Line>>, List<Line>, Optimization> indexBasedRepairer
            (BiFunction<Integer, Integer, Optional<SupplySelection>> indexSelector) {
        return (freeDemandGroups, freedSupplies) -> solution -> {
            final Set<OptimizationEvent> repairs = setOfUniques();
            final var suppliesFree = solution.supplies_free().getLines();
            final var demandsUsed = Sets.<Line>setOfUniques();
            freeDemandGroups.entrySet().forEach(group -> {
                group.getValue().forEach(demand -> {
                    if (demandsUsed.contains(demand)) {
                        return;
                    }
                    final var supplySelection = indexSelector
                            .apply(suppliesFree.size()
                                    , freedSupplies.size());
                    if (!supplySelection.isEmpty()) {
                        demandsUsed.add(demand);
                        final Line selectedSupply;
                        if (supplySelection.get().isCurrentlyFree()) {
                            selectedSupply = suppliesFree.remove(supplySelection.get().selectedIndex());
                        } else {
                            selectedSupply = freedSupplies.remove(supplySelection.get().selectedIndex());
                        }
                        repairs.add
                                (optimizationEvent
                                        (ADDITION
                                                , demand.toLinePointer()
                                                , selectedSupply.toLinePointer()));
                    } else {
                        throw new RuntimeException();
                    }
                });
            });
            return listWithValuesOf(repairs);
        };
    }

    private final Function<List<List<Constraint>>, List<List<Constraint>>> groupSelector;
    private final BiFunction<Map<GroupId, Set<Line>>, List<Line>, Optimization> repairer;
    private final Randomness randomness = randomness();

    protected ConstraintGroupBasedRepair
            (Function<List<List<Constraint>>, List<List<Constraint>>> groupSelector
                    , BiFunction<Map<GroupId, Set<Line>>, List<Line>, Optimization> repairer) {
        this.groupSelector = groupSelector;
        this.repairer = repairer;
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
        demandGrouping.put(null, setOfUniques(solution.demands_unused().getLines()));
        final var demandFreeing = groupsOfConstraintGroup
                .stream()
                .map(e -> e
                        .lastValue()
                        .map(f -> freeDefyingGroupOfConstraintGroup(solution, f))
                        .orElseGet(() -> list()))
                .flatMap(e -> e.stream())
                //.distinct() // TODO Is this needed anymore?
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
        return repairer.apply(freeDemandGroups, freedSupplies).optimize(solution);
    }

    public Map<GroupId, Set<Line>> demandGrouping(Constraint constraintGrouping, SolutionView solution) {
        final Map<GroupId, Set<Line>> demandGrouping = map();
        constraintGrouping
                .lineProcessing()
                .getLines()
                .stream()
                /**
                 * TODO HACK This is code duplication.
                 * It reimplements part of {@link ConstraintGroupBasedRepair#freeDefyingGroupOfConstraintGroup}.
                 */
                .filter(processing -> !constraintGrouping
                        .defying(processing.value(INCOMING_CONSTRAINT_GROUP))
                        .isEmpty())
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
                .map(allocation -> optimizationEvent
                        (REMOVAL
                                , solution.demandOfAllocation(allocation).toLinePointer()
                                , solution.supplyOfAllocation(allocation).toLinePointer()))
                .collect(toList());
    }
}
