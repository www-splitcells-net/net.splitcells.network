package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static java.util.Objects.requireNonNull;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;

public class UsedSupplySwitcher implements Optimization {
    public static UsedSupplySwitcher usedSupplySwitcher() {
        return new UsedSupplySwitcher(randomness(), 1);
    }

    public static UsedSupplySwitcher usedSupplySwitcher(int stepCount) {
        return new UsedSupplySwitcher(randomness(), stepCount);
    }

    public static UsedSupplySwitcher usedSupplySwitcher(Randomness randomness) {
        return new UsedSupplySwitcher(randomness, 1);
    }

    public static UsedSupplySwitcher usedSupplySwitcher(Randomness randomness, int stepCount) {
        return new UsedSupplySwitcher(randomness, stepCount);
    }

    private UsedSupplySwitcher(Randomness randomness, int stepCount) {
        this.randomness = requireNonNull(randomness);
        this.stepCount = stepCount;
    }

    private final Randomness randomness;
    private final int stepCount;

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final List<OptimizationEvent> optimization = list();
        final var processedDemands = Sets.<LinePointer>setOfUniques();
        final var processedSupplies = Sets.<LinePointer>setOfUniques();
        rangeClosed(1, stepCount)
                .forEach(i -> optimization.addAll
                        (optimizationStep(solution, processedDemands, processedSupplies)));
        return optimization;
    }

    private List<OptimizationEvent> optimizationStep
            (SolutionView solution
                    , Set<LinePointer> processedDemands
                    , Set<LinePointer> preocessedSupplies) {
        if (solution.demands_used().size() >= 2) {
            final int selectionA = randomness.integer(0, solution.demands_used().size() - 1);
            final int selectionB = randomness.integer(0, solution.demands_used().size() - 1);
            if (selectionA == selectionB) {
                return list();
            }
            final var usedDemandA = solution.demands().getLines(selectionA);
            final var oldAllocationA = solution.allocations_of_demand(usedDemandA).iterator().next();
            final var usedSupplyA = solution.supply_of_allocation(oldAllocationA);

            final var usedDemandB = solution.demands().getLines(selectionB);
            final var oldAllocationB = solution.allocations_of_demand(usedDemandB).iterator().next();
            final var usedSupplyB = solution.supply_of_allocation(oldAllocationB);

            final var usedDemandAPointer = usedDemandA.toLinePointer();
            final var usedDemandBPointer = usedDemandB.toLinePointer();
            if (processedDemands.containsAny(usedDemandAPointer, usedDemandBPointer)) {
                return list();
            }
            final var usedSupplyAPointer = usedSupplyA.toLinePointer();
            final var usedSupplyBPointer = usedSupplyB.toLinePointer();
            if (preocessedSupplies.containsAny(usedSupplyAPointer, usedSupplyBPointer)) {
                return list();
            }
            processedDemands.addAll(usedDemandAPointer, usedDemandBPointer);
            preocessedSupplies.addAll(usedSupplyAPointer, usedSupplyBPointer);
            return
                    list(optimizationEvent(REMOVAL, usedDemandAPointer, usedSupplyAPointer)
                            , optimizationEvent(REMOVAL, usedDemandBPointer, usedSupplyBPointer)
                            , optimizationEvent(ADDITION, usedDemandAPointer, usedSupplyBPointer)
                            , optimizationEvent(ADDITION, usedDemandBPointer, usedSupplyAPointer)
                    );
        }
        return list();
    }
}
