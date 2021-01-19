package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import net.splitcells.gel.solution.optimization.StepType;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.Sets.*;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.map.Pair.pair;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.constraint.Constraint.incomingGroupsOfConstraintPath;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;
import static net.splitcells.gel.solution.optimization.primitive.SupplySelection.supplySelection;

public class ConstraintGroupBasedRepair implements Optimization {

    public static ConstraintGroupBasedRepair constraintGroupBasedRepair
            (Function<List<List<Constraint>>, Optional<List<Constraint>>> groupSelector
                    , BiFunction<Map<GroupId, Set<Line>>, List<Line>, Optimization> repairer) {
        return new ConstraintGroupBasedRepair(groupSelector, repairer);
    }

    public static ConstraintGroupBasedRepair constraintGroupBasedRepair
            (Function<List<List<Constraint>>, Optional<List<Constraint>>> groupSelector) {
        return new ConstraintGroupBasedRepair(groupSelector, randomRepairer());
    }

    public static ConstraintGroupBasedRepair constraintGroupBasedRepair() {
        final var randomness = randomness();
        return new ConstraintGroupBasedRepair
                (allocationsGroups -> {
                    final var candidates = allocationsGroups
                            .stream()
                            .filter(allocationGroupsPath ->
                                    incomingGroupsOfConstraintPath(allocationGroupsPath)
                                            .stream()
                                            .map(group -> !allocationGroupsPath
                                                    .lastValue()
                                                    .get()
                                                    .defying(group)
                                                    .isEmpty())
                                            .reduce((a, b) -> a && b)
                                            .orElse(false)
                            )
                            .collect(toList());
                    if (candidates.isEmpty()) {
                        return Optional.empty();
                    }
                    return Optional.of(randomness.chooseOneOf(candidates));
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
            freeDemandGroups.entrySet().forEach(group -> {
                group.getValue().forEach(demand -> {
                    final var supplySelection = indexSelector
                            .apply(suppliesFree.size() - 1
                                    , freedSupplies.size() - 1);
                    if (!supplySelection.isEmpty()) {
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

    private final Function<List<List<Constraint>>, Optional<List<Constraint>>> groupSelector;
    private final BiFunction<Map<GroupId, Set<Line>>, List<Line>, Optimization> repairer;

    protected ConstraintGroupBasedRepair
            (Function<List<List<Constraint>>, Optional<List<Constraint>>> groupSelector
                    , BiFunction<Map<GroupId, Set<Line>>, List<Line>, Optimization> repairer) {
        this.groupSelector = groupSelector;
        this.repairer = repairer;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final var groupOfConstraintGroup = groupOfConstraintGroup(solution);
        final var demandGrouping = groupOfConstraintGroup
                .map(e -> e
                        .lastValue()
                        .map(f -> demandGrouping(f, solution))
                        .orElseGet(() -> map()))
                .orElseGet(() -> map());
        demandGrouping.put(null, setOfUniques(solution.demands_unused().getLines()));
        final var defyingGroupFreeing = groupOfConstraintGroup
                .map(e -> e
                        .lastValue()
                        .map(f -> freeDefyingGroupOfConstraintGroup(solution, f))
                        .orElseGet(() -> list()))
                .orElseGet(() -> list());
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            defyingGroupFreeing.forEach(e -> {
                if (!REMOVAL.equals(e.stepType())) {
                    throw new IllegalStateException();
                }
            });

        }
        return defyingGroupFreeing.withAppended(repair(solution, demandGrouping,
                defyingGroupFreeing.stream()
                        .map(e -> e.supply().interpret().get())
                        .collect(toList())));
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
                .map(processing -> pair(processing.value(Constraint.RESULTING_CONSTRAINT_GROUP)
                        , processing.value(Constraint.LINE)))
                .forEach(processing -> {
                    final Set<Line> group;
                    if (!demandGrouping.containsKey(processing.getKey())) {
                        group = Sets.setOfUniques();
                        demandGrouping.put(processing.getKey(), group);
                    } else {
                        group = demandGrouping.get(processing.getKey());
                    }
                    group.with(solution.demand_of_allocation(processing.getValue()));
                });
        return demandGrouping;
    }

    public Optional<List<Constraint>> groupOfConstraintGroup(SolutionView solution) {
        return groupSelector.apply(Constraint.allocationGroups(solution.constraint()));
    }

    public List<OptimizationEvent> freeDefyingGroupOfConstraintGroup(SolutionView solution, Constraint constraint) {
        final var incomingGroups = Sets.setOfUniques
                (constraint
                        .lineProcessing()
                        .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                        .values());
        return incomingGroups
                .stream()
                .filter(group -> !constraint.defying(group).isEmpty())
                .map(group -> constraint.lineProcessing().columnView(Constraint.LINE).values())
                .flatMap(streamOfLineList -> streamOfLineList.stream())
                .distinct()
                .map(allocation -> optimizationEvent
                        (REMOVAL
                                , solution.demand_of_allocation(allocation).toLinePointer()
                                , solution.supply_of_allocation(allocation).toLinePointer()))
                .collect(toList());
    }
}
