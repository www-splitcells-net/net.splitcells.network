/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.primitive.repair;

import lombok.val;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.framework.Variable;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.rating.framework.Rating;

import java.util.function.Function;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.environment.config.framework.Variable.variable;
import static net.splitcells.dem.environment.config.framework.Variable.variable;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;

public class SupplySelectors {
    private SupplySelectors() {
        throw constructorIllegal();
    }

    public static SupplySelector indexBasedRepairer(Function<Integer, Integer> indexSelector) {
        return freeDemandGroups -> solution -> {
            final var demandsUsed = Sets.<Line>setOfUniques();
            freeDemandGroups.entrySet().forEach(group -> {
                group.getValue().forEach(demand -> {
                    if (demandsUsed.contains(demand)) {
                        return;
                    }
                    final var freeSupplies = solution.suppliesFree().unorderedLines();
                    if (freeSupplies.hasElements()) {
                        final var supplySelection = freeSupplies.get(indexSelector.apply(freeSupplies.size() - 1));
                        demandsUsed.add(demand);
                        solution.assign(demand, supplySelection);
                    }
                });
            });
        };
    }

    public static SupplySelector hillClimber() {
        return hillClimber(Integer.MAX_VALUE);
    }

    /**
     * @param tries
     * @return Checks the rating of {@code tries} many random allocations and returns the one with the best {@link Rating}.
     */
    public static SupplySelector hillClimber(int tries) {
        val rnd = randomness();
        return freeDemandGroups -> solution -> {
            val freeSupplies = solution.suppliesFree().unorderedLines();
            for (val demandGroup : freeDemandGroups.values()) {
                for (val freeDemand : demandGroup) {
                    if (freeSupplies.isEmpty()) {
                        return;
                    }
                    if (null == solution.demandsUsed().rawLine(freeDemand.index())) {
                        val bestSupply = Variable.<Line>variable();
                        val bestRating = variable(solution.constraint().rating());
                        solution.history().processWithoutHistory(() -> {
                            range(0, tries).filter(i -> i < freeSupplies.size()).forEach(i -> {
                                if (freeSupplies.isEmpty()) {
                                    return;
                                }
                                val nextSupply = freeSupplies.removeAt(rnd.integer(0, freeSupplies.size() - 1));
                                val allocation = solution.assign(freeDemand, nextSupply);
                                val nextRating = solution.constraint().rating();
                                solution.remove(allocation);
                                if (bestSupply.isNull() || nextRating.betterThan(bestRating.val())) {
                                    if (bestSupply.hasValue()) {
                                        freeSupplies.add(bestSupply.val());
                                    }
                                    bestSupply.withValue(nextSupply);
                                    bestRating.withValue(nextRating);
                                } else {
                                    freeSupplies.add(nextSupply);
                                }
                            });
                        });
                        if (bestSupply.hasValue()) {
                            solution.assign(freeDemand, bestSupply.val());
                        }
                    }
                }
            }
        };
    }

    public static SupplySelector supplySelector() {
        final var randomness = randomness();
        return indexBasedRepairer(i -> randomness.integer(0, i));
    }
}
