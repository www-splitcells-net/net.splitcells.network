/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.list;

public class Repeater implements OfflineOptimization {
    public static Repeater repeater(OfflineOptimization optimization, int maximalRepetitionCount) {
        return new Repeater(optimization, maximalRepetitionCount);
    }

    private final OfflineOptimization optimization;
    private final int maximalRepetitionCount;
    private int repetitionCount = 0;

    private Repeater(OfflineOptimization optimization, int maximalRepetitionCount) {
        this.optimization = optimization;
        this.maximalRepetitionCount = maximalRepetitionCount;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        if (repetitionCount >= maximalRepetitionCount) {
            return list();
        }
        repetitionCount += 1;
        return optimize(solution);
    }
}
