package net.splitcells.gel.solution.optimization;

import net.splitcells.gel.solution.Solution;

@FunctionalInterface
public interface ParameterizedOnlineOptimization<T> {
    void optimize(Solution solution, T parameter);
}
