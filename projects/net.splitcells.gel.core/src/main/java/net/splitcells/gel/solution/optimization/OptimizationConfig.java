package net.splitcells.gel.solution.optimization;

import net.splitcells.gel.solution.Solution;

import java.util.Optional;
import java.util.function.BiPredicate;

public class OptimizationConfig {
    public static OptimizationConfig optimizationConfig() {
        return new OptimizationConfig();
    }

    private boolean recordHistory = true;
    private boolean optimizeOnce = false;

    private BiPredicate<Solution, Integer> continuationCondition = (s, i) -> i == 0;

    private OptimizationConfig() {

    }

    public OptimizationConfig withRecordHistory(boolean recordHistory) {
        this.recordHistory = recordHistory;
        return this;
    }

    public boolean recordHistory() {
        return recordHistory;
    }

    public OptimizationConfig withOptimizeOnce(boolean optimizeOnce) {
        this.optimizeOnce = optimizeOnce;
        return this;
    }

    public boolean optimizeOnce() {
        return optimizeOnce;
    }

    public OptimizationConfig withContinuationCondition(BiPredicate<Solution, Integer> continuationCondition) {
        this.continuationCondition = continuationCondition;
        return this;
    }

    public BiPredicate<Solution, Integer> continuationCondition() {
        return continuationCondition;
    }
}
