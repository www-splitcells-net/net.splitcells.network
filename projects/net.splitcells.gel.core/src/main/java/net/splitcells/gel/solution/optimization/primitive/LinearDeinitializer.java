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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;

public class LinearDeinitializer implements OfflineOptimization {

    public static LinearDeinitializer linearDeinitializer() {
        return new LinearDeinitializer();
    }

    private LinearDeinitializer() {

    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        if (!solution.allocations().lines().isEmpty()) {
            final var allocation = solution.allocations().lines().get(0);
            return
                    list(
                            optimizationEvent
                                    (REMOVAL
                                            , solution.demandOfAllocation(allocation).toLinePointer()
                                            , solution.supplyOfAllocation(allocation).toLinePointer()));
        }
        return list();
    }
}
