package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import net.splitcells.gel.solution.optimization.primitive.SupplySelection;

import java.util.Optional;
import java.util.function.BiFunction;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;
import static net.splitcells.gel.solution.optimization.primitive.SupplySelection.supplySelection;
import static org.assertj.core.api.Assertions.assertThat;

public class SupplySelectors {
    private SupplySelectors() {
        throw constructorIllegal();
    }

    public static SupplySelector indexBasedRepairer
            (BiFunction<Integer, Integer, Optional<SupplySelection>> indexSelector) {
        return (freeDemandGroups, freedSupplies) -> solution -> {
            final Set<OptimizationEvent> repairs = setOfUniques();
            final var suppliesFree = solution.suppliesFree().getLines();
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

    public static SupplySelector supplySelector() {
        final var randomness = randomness();
        return indexBasedRepairer((freeSupplyCount, supplyFreedCount) -> {
            if (freeSupplyCount.floatValue() + supplyFreedCount.floatValue() <= 0) {
                return Optional.empty();
            }
            if (randomness.truthValue(freeSupplyCount.floatValue()
                    / (freeSupplyCount.floatValue()
                    + supplyFreedCount.floatValue()))
                    && freeSupplyCount > 0) {
                return Optional.of(supplySelection(randomness.integer(0, freeSupplyCount - 1), true));
            } else {
                return Optional.of(supplySelection(randomness.integer(0, supplyFreedCount - 1), false));
            }
        });
    }
}
