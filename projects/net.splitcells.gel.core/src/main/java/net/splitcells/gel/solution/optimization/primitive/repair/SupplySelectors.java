package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import net.splitcells.gel.solution.optimization.primitive.SupplySelection;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;
import static net.splitcells.gel.solution.optimization.primitive.SupplySelection.supplySelection;

public class SupplySelectors {
    private SupplySelectors() {
        throw constructorIllegal();
    }

    public static SupplySelector indexBasedRepairer(Function<Integer, Integer> indexSelector) {
        return (freeDemandGroups) -> solution -> {
            final var demandsUsed = Sets.<Line>setOfUniques();
            freeDemandGroups.entrySet().forEach(group -> {
                group.getValue().forEach(demand -> {
                    if (demandsUsed.contains(demand)) {
                        return;
                    }
                    final var freeSupplies = solution.suppliesFree().getLines();
                    if (freeSupplies.hasElements()) {
                        final var supplySelection = freeSupplies.get(indexSelector.apply(freeSupplies.size() - 1));
                        demandsUsed.add(demand);
                        solution.allocate(demand, supplySelection);
                    } else {
                        throw new RuntimeException();
                    }
                });
            });
        };
    }

    public static SupplySelector hillClimber() {
        final var randomness = randomness();
        return freeDemandGroups -> solution -> {
            for (final var demandGroup : freeDemandGroups.values()) {
                for (final var freeDemand : demandGroup) {
                    if (null == solution.demandsUsed().getRawLine(freeDemand.index())) {
                        // TODO HACK
                        final List<Line> bestSupply = list();
                        final var bestRating = list(solution.constraint().rating());
                        solution.history().processWithoutHistory(() -> {
                            for (final var freeSupply : solution.suppliesFree().getLines().shuffle(randomness)) {
                                final var allocation = solution.allocate(freeDemand, freeSupply);
                                final var nextRating = solution.constraint().rating();
                                solution.remove(allocation);
                                if (bestSupply.isEmpty()) {
                                    bestSupply.add(freeSupply);
                                    bestRating.set(0, nextRating);
                                } else if (nextRating.betterThan(bestRating.get(0))) {
                                    bestSupply.set(0, freeSupply);
                                    bestRating.set(0, nextRating);
                                }
                            }
                        });
                        if (bestSupply.get(0) != null) {
                            solution.allocate(freeDemand, bestSupply.get(0));
                        }
                    }
                }
            }
            return;
        };
    }

    public static SupplySelector supplySelector() {
        final var randomness = randomness();
        return indexBasedRepairer((i) -> randomness.integer(0, i));
    }
}
