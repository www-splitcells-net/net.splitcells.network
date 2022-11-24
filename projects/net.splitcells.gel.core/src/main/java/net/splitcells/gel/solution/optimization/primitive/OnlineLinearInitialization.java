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

import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * Allocates {@link Solution#demandsFree()} and {@link Solution#suppliesFree()} in their respective order.
 */
public class OnlineLinearInitialization implements OnlineOptimization {

    /**
     * Setting this bool to true, improves the runtime performance.
     */
    private static final boolean GET_NEXT_LINE_BY_STREAM = true;

    public static OnlineLinearInitialization onlineLinearInitialization() {
        return new OnlineLinearInitialization();
    }

    private OnlineLinearInitialization() {

    }

    @Override
    public void optimize(Solution solution) {
        while (solution.demandsFree().hasContent() && solution.suppliesFree().hasContent()) {
            if (GET_NEXT_LINE_BY_STREAM) {
                solution.allocate(solution.demandsFree().orderedLinesStream().findFirst().orElseThrow()
                        , solution.suppliesFree().orderedLinesStream().findFirst().orElseThrow());
            } else {
                solution.allocate(solution.demandsFree().line(0)
                        , solution.suppliesFree().line(0));
            }
        }
    }
}
