package net.splitcells.gel.solution.optimization;

import net.splitcells.gel.solution.Solution;

public interface EnumerableOnlineOptimization extends ParameterizedOnlineOptimization<Integer> {
    /**
     * Returns the number of possible parameters for {@link #optimize(Solution, Object)}
     * for a given {@link Solution}.
     * The parameter ranges from 0 to {@link #numberOfBranches(Solution)} - 1.
     *
     * @param solution The Solution For Which The Branches Are Calculated
     * @return Number Of Branches
     */
    int numberOfBranches(Solution solution);
}
