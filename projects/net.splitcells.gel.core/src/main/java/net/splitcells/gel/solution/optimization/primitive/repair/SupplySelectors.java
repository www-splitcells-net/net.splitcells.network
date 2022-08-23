package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;

import java.util.function.Function;
import java.util.stream.IntStream;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;

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
                    final var freeSupplies = solution.suppliesFree().lines();
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
            final var distinctFreeSupplies = solution.suppliesFree().distinctLines();
            for (final var demandGroup : freeDemandGroups.values()) {
                for (final var freeDemand : demandGroup) {
                    if (distinctFreeSupplies.isEmpty()) {
                        return;
                    }
                    if (null == solution.demandsUsed().rawLine(freeDemand.index())) {
                        // TODO HACK
                        final List<Line> bestSupply = list();
                        final var bestRating = list(solution.constraint().rating());
                        final List<Integer> distinctIndex = list(-1);
                        solution.history().processWithoutHistory(() -> {
                            IntStream.range(0, distinctFreeSupplies.size()).forEach(i -> {
                                final var freeSupply = distinctFreeSupplies.get(i);
                                final var allocation = solution.allocate(freeDemand, freeSupply);
                                final var nextRating = solution.constraint().rating();
                                solution.remove(allocation);
                                if (bestSupply.isEmpty()) {
                                    bestSupply.add(freeSupply);
                                    bestRating.set(0, nextRating);
                                    distinctIndex.set(0, i);
                                } else if (nextRating.betterThan(bestRating.get(0))) {
                                    bestSupply.set(0, freeSupply);
                                    bestRating.set(0, nextRating);
                                    distinctIndex.set(0, i);
                                }
                            });
                        });
                        if (!bestSupply.isEmpty() && bestSupply.get(0) != null) {
                            final var freeSupplyValues = bestSupply.get(0).values();
                            solution.allocate(freeDemand, bestSupply.get(0));
                            distinctFreeSupplies.removeAt(distinctIndex.get(0));
                            solution.suppliesFree()
                                    .lookupEquals(freeSupplyValues)
                                    .findFirst()
                                    .ifPresent(distinctFreeSupplies::add);
                        }
                    }
                }
            }
        };
    }

    public static SupplySelector supplySelector() {
        final var randomness = randomness();
        return indexBasedRepairer((i) -> randomness.integer(0, i));
    }
}
