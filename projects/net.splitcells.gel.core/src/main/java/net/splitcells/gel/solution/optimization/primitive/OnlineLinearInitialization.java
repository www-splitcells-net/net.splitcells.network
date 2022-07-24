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
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OnlineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;

public class OnlineLinearInitialization implements OnlineOptimization {

    public static OnlineLinearInitialization onlineLinearInitialization() {
        return new OnlineLinearInitialization();
    }

    private OnlineLinearInitialization() {

    }

    @Override
    public void optimize(Solution solution) {
        while (solution.demandsFree().hasContent() && solution.suppliesFree().hasContent()) {
            solution.allocate(solution.demandsFree().getLines().get(0)
                    , solution.suppliesFree().getLines().get(0));

        }
    }
}
