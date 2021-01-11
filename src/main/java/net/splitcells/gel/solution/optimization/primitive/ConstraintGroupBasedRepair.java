package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.Sets.*;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.map.Pair.pair;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;

public class ConstraintGroupBasedRepair implements Optimization {

    public static ConstraintGroupBasedRepair constraintGroupBasedRepair
            (Function<List<List<Constraint>>, Optional<List<Constraint>>> groupSelector
                    , Function<Map<GroupId, Set<Line>>, Optimization> repairer) {
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
                            .filter(allocationGroupsPath -> !allocationGroupsPath
                                    .lastValue()
                                    .get()
                                    .defying()
                                    .isEmpty())
                            .collect(toList());
                    if (candidates.isEmpty()) {
                        return Optional.empty();
                    }
                    return Optional.of(randomness.chooseOneOf(candidates));
                }, randomRepairer());
    }

    private static final Function<Map<GroupId, Set<Line>>, Optimization> randomRepairer() {
        final var randomness = randomness();
        return indexBasedRepairer(i -> randomness.integer(0, i));
    }

    public static final Function<Map<GroupId, Set<Line>>, Optimization> indexBasedRepairer
            (Function<Integer, Integer> indexSelector) {
        return freeDemandGroups -> solution -> {
            final Set<OptimizationEvent> repairs = setOfUniques();
            final var supplyFree = solution.supplies_free().getLines();
            freeDemandGroups.entrySet().forEach(grup -> {
                grup.getValue().forEach(demand -> {
                    if (supplyFree.isEmpty()) {
                        return;
                    }
                    repairs.ensureContains
                            (optimizationEvent
                                    (ADDITION
                                            , demand.toLinePointer()
                                            , supplyFree
                                                    .remove((int) indexSelector.apply(supplyFree.size() - 1))
                                                    .toLinePointer()));
                });
            });
            return listWithValuesOf(repairs);
        };
    }

    private final Function<List<List<Constraint>>, Optional<List<Constraint>>> groupSelector;
    private final Function<Map<GroupId, Set<Line>>, Optimization> repairer;

    protected ConstraintGroupBasedRepair
            (Function<List<List<Constraint>>, Optional<List<Constraint>>> groupSelector
                    , Function<Map<GroupId, Set<Line>>, Optimization> repairer) {
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
        final var optimization = groupOfConstraintGroup
                .map(e -> e
                        .lastValue()
                        .map(f -> freeDefyingGroupOfConstraintGroup(solution, f))
                        .orElseGet(() -> list()))
                .orElseGet(() -> list());
        optimization.withAppended(repair(solution, demandGrouping));
        return optimization;
    }

    public List<OptimizationEvent> repair(SolutionView solution, Map<GroupId, Set<Line>> freeDemandGroups) {
        return repairer.apply(freeDemandGroups).optimize(solution);
    }

    public Map<GroupId, Set<Line>> demandGrouping(Constraint constraintGrouping, SolutionView solution) {
        final Map<GroupId, Set<Line>> demandGrouping = map();
        constraintGrouping
                .lineProcessing()
                .getLines()
                .stream()
                .map(processing -> pair(processing.value(Constraint.RESULTING_CONSTRAINT_GROUP), processing.value(Constraint.LINE)))
                .forEach(processing -> {
                    final Set<Line> grup;
                    if (!demandGrouping.containsKey(processing.getKey())) {
                        grup = Sets.setOfUniques();
                        demandGrouping.put(processing.getKey(), grup);
                    } else {
                        grup = demandGrouping.get(processing.getKey());
                    }
                    grup.with(processing.getValue());
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
