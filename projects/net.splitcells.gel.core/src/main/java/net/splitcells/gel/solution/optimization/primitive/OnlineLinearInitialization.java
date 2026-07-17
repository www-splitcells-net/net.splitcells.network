/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * Allocates {@link Solution#demandsFree()} and {@link Solution#suppliesFree()} in their respective order.
 */
public class OnlineLinearInitialization implements OnlineOptimization {
    private static final boolean IMPROVE_RUNTIME_GET_NEXT_LINE_BY_STREAM = true;
    private static final boolean IMPROVE_RUNTIME_GET_NEXT_LINE_BY_CACHE = false;

    public static OnlineLinearInitialization onlineLinearInitialization() {
        return new OnlineLinearInitialization();
    }

    private OnlineLinearInitialization() {

    }

    @Override
    public void optimize(Solution solution) {
        if (IMPROVE_RUNTIME_GET_NEXT_LINE_BY_CACHE) {
            // This is fast, when free demand and supply set are of equal length.
            final var freeDemands = solution.demandsFree().orderedLines();
            final var freeSupplies = solution.suppliesFree().orderedLines();
            int i = 0;
            while (freeDemands.hasElements() && freeSupplies.hasElements()) {
                solution.assign(freeDemands.get(i++), freeSupplies.get(i++));
            }
        } else {
            while (solution.demandsFree().hasContent() && solution.suppliesFree().hasContent()) {
                if (IMPROVE_RUNTIME_GET_NEXT_LINE_BY_STREAM) {
                    // TODO Create faster version of initialization, based on 2 unordered streams.
                    solution.assign(solution.demandsFree().orderedLinesStream().findFirst().orElseThrow()
                            , solution.suppliesFree().orderedLinesStream().findFirst().orElseThrow());
                } else {
                    solution.assign(solution.demandsFree().orderedLine(0)
                            , solution.suppliesFree().orderedLine(0));
                }
            }
        }
    }
}
