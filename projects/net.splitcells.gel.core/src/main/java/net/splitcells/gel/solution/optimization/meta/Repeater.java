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
