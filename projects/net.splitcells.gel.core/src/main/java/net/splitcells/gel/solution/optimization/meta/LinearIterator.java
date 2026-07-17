/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.data.set.list.Lists.list;

public class LinearIterator implements OfflineOptimization {

    private final List<OfflineOptimization> optimizations;
    private int counter = -1;

    public static LinearIterator linearIterator(OfflineOptimization... optimization) {
        return new LinearIterator(list(optimization));
    }

    public static LinearIterator linearIterator(List<OfflineOptimization> optimization) {
        return new LinearIterator(optimization);
    }

    private LinearIterator(List<OfflineOptimization> optimizations) {
        this.optimizations = optimizations;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        List<OptimizationEvent> optimization = list();
        int tryCounter = 0;
        while (optimization.isEmpty() && tryCounter < this.optimizations.size()) {
            optimization = selectNextOptimization().optimize(solution);
            ++tryCounter;
        }
        return optimization;
    }

    private OfflineOptimization selectNextOptimization() {
        counter += 1;
        if (counter >= optimizations.size()) {
            counter = 0;
        }
        return optimizations.get(counter);
    }
}
