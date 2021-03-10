package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.splitcells.dem.data.set.map.Maps.map;
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
        return new ConstraintGroupBasedRepair(groupSelector, randomRepairer());
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
                    return randomness.choose_at_most_multiple_of(numberOfGroupsSelectedPerDefiance, candidates);
                }, randomRepairer());
    }

    private static final BiFunction<Map<GroupId, Set<Line>>, List<Line>, Optimization> randomRepairer() {
        final var randomness = randomness();
        return indexBasedRepairer((suppliesFree, freedSupplies) -> {
            if (suppliesFree.floatValue() + freedSupplies.floatValue() <= 0) {
                return Optional.empty();
            }
            if (randomness.truthValue(suppliesFree.floatValue() / (suppliesFree.floatValue() + freedSupplies.floatValue()))) {
                return Optional.of(supplySelection(randomness.integer(0, suppliesFree - 1), true));
            } else {
                return Optional.of(supplySelection(randomness.integer(0, freedSupplies - 1), false));
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
                            .apply(suppliesFree.size() - 1
                                    , freedSupplies.size() - 1);
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
        final var optimizationWithDuplicateAdditions = demandGroupings
                .stream()
                .map(demandGrouping -> {
                    demandGrouping.put(null, setOfUniques(solution.demands_unused().getLines()));
                    final var defyingGroupFreeing = groupsOfConstraintGroup
                            .stream()
                            .map(e -> e
                                    .lastValue()
                                    .map(f -> freeDefyingGroupOfConstraintGroup(solution, f))
                                    .orElseGet(() -> list()))
                            .flatMap(e -> e.stream())
                            .distinct()
                            .collect(toList());
                    if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
                        defyingGroupFreeing.forEach(e -> {
                            if (!REMOVAL.equals(e.stepType())) {
                                throw new IllegalStateException();
                            }
                        });
                    }
                    defyingGroupFreeing.addAll(repair(solution, demandGrouping,
                            defyingGroupFreeing.stream()
                                    .map(e -> e.supply().interpret().get())
                                    .collect(toList())));
                    return defyingGroupFreeing;
                })
                .flatMap(e -> e.stream())
                .distinct()
                .collect(toList());
        final var optimization = optimizationWithDuplicateAdditions
                .stream()
                .filter(event -> REMOVAL.equals(event.stepType()))
                .collect(toList());
        final var demandToProposedAddition = optimizationWithDuplicateAdditions
                .stream()
                .filter(event -> ADDITION.equals(event.stepType()))
                .collect(Collectors.groupingBy(event -> event.demand()));
        final var chosenDemandAllocations = demandToProposedAddition
                .values()
                .stream()
                .map(proposals -> Lists.listWithValuesOf(randomness.chooseOneOf(proposals)))
                .flatMap(e -> e.stream())
                .collect(toList());
        optimization.addAll(chosenDemandAllocations);
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
