/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
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
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
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
        if (solution.demandsUsed().size() >= 2) {
            final int selectionA = randomness.integer(0, solution.demandsUsed().size() - 1);
            final int selectionB = randomness.integer(0, solution.demandsUsed().size() - 1);
            if (selectionA == selectionB) {
                return list();
            }
            final var usedDemandA = solution.demands().getLines(selectionA);
            final var oldAllocationA = solution.allocationsOfDemand(usedDemandA).iterator().next();
            final var usedSupplyA = solution.supplyOfAllocation(oldAllocationA);

            final var usedDemandB = solution.demands().getLines(selectionB);
            final var oldAllocationB = solution.allocationsOfDemand(usedDemandB).iterator().next();
            final var usedSupplyB = solution.supplyOfAllocation(oldAllocationB);

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
