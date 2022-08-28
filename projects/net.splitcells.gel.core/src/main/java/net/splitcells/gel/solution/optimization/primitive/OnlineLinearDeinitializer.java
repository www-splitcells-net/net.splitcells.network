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

public class OnlineLinearDeinitializer implements OnlineOptimization {

    public static OnlineLinearDeinitializer onlineLinearDeinitializer() {
        return new OnlineLinearDeinitializer();
    }

    private OnlineLinearDeinitializer() {

    }

    @Override
    public void optimize(Solution solution) {
        while (!solution.isEmpty()) {
            solution.remove(solution.allocations().lines().get(0));
        }
    }
}