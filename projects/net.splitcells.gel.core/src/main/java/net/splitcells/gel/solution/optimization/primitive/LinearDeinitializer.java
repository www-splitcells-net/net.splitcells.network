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
