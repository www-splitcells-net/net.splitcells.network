/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization;

import net.splitcells.gel.solution.Solution;

import static net.splitcells.gel.solution.optimization.meta.Deescalation.deescalation;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.constraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.DemandSelectors.commitCompliance;
import static net.splitcells.gel.solution.optimization.primitive.repair.RepairConfig.repairConfig;

/**
 * This class is a staging ground for changes to {@link DefaultOptimization}.
 */
public class DefaultOptimizationStaging implements OnlineOptimization {
    public static OnlineOptimization defaultOptimizationStaging() {
        return new DefaultOptimizationStaging();
    }

    private DefaultOptimizationStaging() {
    }

    @Override
    public void optimize(Solution solution) {
        onlineLinearInitialization().optimize(solution);
        final var initialProposal = solution.propose();
        final var maxDepth = solution.constraint().longestConstraintPathLength();
        final var deescalation = deescalation(currentDepth -> s -> {
                    final int execCount = currentDepth + 1;
                    for (int j = 0; j < execCount; ++j) {
                        final var config = repairConfig()
                                .withMinimumConstraintGroupPath(currentDepth)
                                .withDemandSelector(commitCompliance(repairConfig().demandSelector(), initialProposal));
                        constraintGroupBasedRepair(config).optimize(s);
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
