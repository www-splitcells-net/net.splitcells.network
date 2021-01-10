package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;

public class Escalator implements Optimization {

    public static Escalator escalator(Function<Integer, Optimization> optimizations) {
        return new Escalator(optimizations);
    }

    private final Function<Integer, Optimization> optimizations;
    private int escalationLevel = 0;

    private Escalator(Function<Integer, Optimization> optimizations) {
        this.optimizations = optimizations;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final var rootRating = solution.constraint().rating();
        final var rootHistoryIndex = solution.history().currentIndex();
        if (escalationLevel < 0) {
            return list();
        }
        final var optimizations = this.optimizations.apply(escalationLevel).optimize(solution);
        final var currentRating = solution.rating(optimizations);
        if (currentRating.betterThan(rootRating)) {
            escalationLevel += 1;
        } else {
            escalationLevel -= 1;
        }
        return optimizations;
    }
}