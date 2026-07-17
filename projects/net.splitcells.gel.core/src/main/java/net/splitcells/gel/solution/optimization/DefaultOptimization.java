/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization;

import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.meta.OfflineEscalator;

import static net.splitcells.gel.solution.optimization.meta.Deescalation.deescalation;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.constraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.RepairConfig.repairConfig;

/**
 * This optimization is the default recommendation for problem-solving.
 * It represents currently the best available optimization for problems in general.
 * Note, that other optimizers might be better for specific problems or
 * if the optimizer ist adjusted to a given problem.
 * @see DefaultOptimizationStaging
 */
public class DefaultOptimization implements OnlineOptimization {
    public static OnlineOptimization defaultOptimization() {
        return new DefaultOptimization();
    }

    private DefaultOptimization() {

    }

    @Override
    public void optimize(Solution solution) {
        onlineLinearInitialization().optimize(solution);
        final var maxDepth = solution.constraint().longestConstraintPathLength();
        final var deescalation = deescalation(currentDepth -> s -> {
                    final int execCount = currentDepth + 1;
                    for (int j = 0; j < execCount; ++j) {
                        constraintGroupBasedRepair(repairConfig().withMinimumConstraintGroupPath(currentDepth))
                                .optimize(s);
                    }
                }
                , maxDepth, 0, maxDepth);
        for (int x = 0; x <= 100; ++x) {
            deescalation.optimize(solution);
        }
        // Ensures, that at the end of the optimization all values are assigned.
        onlineLinearInitialization().optimize(solution);
    }
}
