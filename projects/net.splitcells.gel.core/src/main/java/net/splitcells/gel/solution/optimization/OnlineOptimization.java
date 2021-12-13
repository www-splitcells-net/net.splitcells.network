package net.splitcells.gel.solution.optimization;

import net.splitcells.gel.solution.Solution;

/**
 * <p>What benefit is there in reviewing past sufferings, and in being unhappy, just because once you were unhappy?
 * - Seneca</p>
 * <p>Prefer {@link OnlineOptimization} over {@link OfflineOptimization},
 * because these are easier to write.
 * The reason for this, is the fact, that one often needs to organize an
 * {@link OptimizationEvent} {@link net.splitcells.dem.data.set.list.List}
 * and oftentimes need to write queries for these {@link OptimizationEvent}s.
 * There are no ready to go data structure for this,
 * which makes this harder.
 * During the usage of {@link OnlineOptimization} one has access to an up to
 * date {@link Solution}, which provides organizational structures
 * for the current state.
 * On the other hand, {@link OfflineOptimization} provides organizational
 * data structures via a {@link net.splitcells.gel.solution.SolutionView}
 * for the initial state,
 * which can be useful as well.
 * </p>
 * <p>TODO IDEA Implement random forest.</p>
 */
@FunctionalInterface
public interface OnlineOptimization {
    void optimize(Solution solution);
}
