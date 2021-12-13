package net.splitcells.gel.solution.optimization;

import net.splitcells.gel.solution.Solution;

/**
 * <p>What benefit is there in reviewing past sufferings, and in being unhappy, just because once you were unhappy?
 * - Seneca</p>
 * <p>TODO IDEA Implement random forest.</p>
 */
@FunctionalInterface
public interface OnlineOptimization {
    void optimize(Solution solution);
}
