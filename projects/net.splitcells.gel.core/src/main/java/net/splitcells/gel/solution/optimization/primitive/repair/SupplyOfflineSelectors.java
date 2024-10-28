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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.gel.data.view.Line;
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

public class SupplyOfflineSelectors {
    private SupplyOfflineSelectors() {
        throw constructorIllegal();
    }

    public static SupplyOfflineSelector indexBasedRepairer
            (BiFunction<Integer, Integer, Optional<SupplySelection>> indexSelector) {
        return (freeDemandGroups, freedSupplies) -> solution -> {
            final Set<OptimizationEvent> repairs = setOfUniques();
            final var suppliesFree = solution.suppliesFree().unorderedLines();
            final var demandsUsed = Sets.<Line>setOfUniques();
            freeDemandGroups.entrySet().forEach(group -> {
                group.getValue().forEach(demand -> {
                    if (demandsUsed.contains(demand)) {
                        return;
                    }
                    final var supplySelection = indexSelector
                            .apply(suppliesFree.size()
                                    , freedSupplies.size());
                    if (supplySelection.isPresent()) {
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

    public static SupplyOfflineSelector supplySelector() {
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
