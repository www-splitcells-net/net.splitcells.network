/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
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
            solution.remove(solution.allocations().unorderedLines().get(0));
        }
    }
}
