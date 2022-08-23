/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;

public class FreeSupplySwitcher implements OfflineOptimization {
    public static FreeSupplySwitcher freeSupplySwitcher() {
        return new FreeSupplySwitcher();
    }

    public static FreeSupplySwitcher freeSupplySwitcher(Randomness randomness, int soluSkaitlis) {
        return new FreeSupplySwitcher(randomness, soluSkaitlis);
    }

    public static FreeSupplySwitcher freeSupplySwitcher(Randomness randomness) {
        return new FreeSupplySwitcher(randomness);
    }

    private final Randomness randomness;
    private final int stepCounter;

    public FreeSupplySwitcher(Randomness randomness, int stepCounter) {
        this.randomness = randomness;
        this.stepCounter = stepCounter;
    }

    public FreeSupplySwitcher(Randomness randomness) {
        this.randomness = randomness;
        this.stepCounter = 1;
    }

    public FreeSupplySwitcher() {
        randomness = randomness();
        this.stepCounter = 1;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final List<OptimizationEvent> optimization = list();
        final var processedDemands = Sets.<LinePointer>setOfUniques();
        final var processedSupplies = Sets.<LinePointer>setOfUniques();
        rangeClosed(1, stepCounter)
                .forEach(i -> optimization.addAll
                        (optimizationStep(solution, processedDemands, processedSupplies)));
        return optimization;
    }

    public List<OptimizationEvent> optimizationStep
            (SolutionView solution
                    , Set<LinePointer> processedDemands
                    , Set<LinePointer> processedSupplies) {
        if (solution.demandsUsed().hasContent() && solution.suppliesFree().hasContent()) {
            final int usedDemandIndex = randomness.integer(0, solution.demandsUsed().size() - 1);
            final var usedDemand = solution.demandsUsed().line(usedDemandIndex);
            final var usedDemandPointer = usedDemand.toLinePointer();
            if (processedDemands.contains(usedDemandPointer)) {
                return list();
            }
            final var allocation = solution.allocationsOfDemand(usedDemand).iterator().next();
            final var usedSupply = solution.supplyOfAllocation(allocation);
            final var usedSupplyPointer = usedSupply.toLinePointer();
            if (processedSupplies.contains(usedSupplyPointer)) {
                return list();
            }
            processedDemands.add(usedDemandPointer);
            processedSupplies.add(usedSupplyPointer);
            return
                    list(
                            optimizationEvent(REMOVAL, usedDemandPointer, usedSupplyPointer)
                            , optimizationEvent(
                                    ADDITION
                                    , solution.demands()
                                            .rawLine(usedDemand.index())
                                            .toLinePointer()
                                    , solution
                                            .supplies()
                                            .rawLine
                                                    (randomness.integer(0, solution.suppliesFree().size()))
                                            .toLinePointer()
                            ));
        }
        return list();
    }
}
