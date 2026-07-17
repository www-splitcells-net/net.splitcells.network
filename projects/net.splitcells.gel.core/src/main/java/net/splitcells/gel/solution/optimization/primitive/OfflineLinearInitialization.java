/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.primitive;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

public class OfflineLinearInitialization implements OfflineOptimization {

    @Deprecated
    public static OfflineLinearInitialization offlineLinearInitialization() {
        return new OfflineLinearInitialization();
    }

    private OfflineLinearInitialization() {

    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        if (solution.demandsFree().hasContent() && solution.suppliesFree().hasContent()) {
            return list(
                    optimizationEvent
                            (ADDITION
                                    , solution.demandsFree().orderedLine(0).toLinePointer()
                                    , solution.suppliesFree().orderedLine(0).toLinePointer()));

        }
        return list();
    }

}
