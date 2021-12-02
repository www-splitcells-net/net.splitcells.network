package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OnlineOptimization;
import net.splitcells.gel.solution.optimization.space.EnumerableOptimizationSpace;

import java.util.Optional;
import java.util.stream.IntStream;

import static net.splitcells.gel.solution.optimization.primitive.enumerable.Initializer.initializer;
import static net.splitcells.gel.solution.optimization.space.EnumerableOptimizationSpaceI.enumerableOptimizationSpace;

/**
 * This is an implementation of the backtracking algorithm.
 * The backtracking exits, when the {@link Solution#isComplete()} is {@link true}.
 * This implementation backtracks,
 * if a given {@link Solution} is worse than the previous {@link Solution}
 * found via {@link EnumerableOptimizationSpace#parent()}.
 */
public class Backtracking implements OnlineOptimization {
    public static Backtracking backtracking() {
        return new Backtracking();
    }

    private Backtracking() {

    }

    @Override
    public void optimize(Solution solution) {
        final var searchSpace = enumerableOptimizationSpace(solution, initializer());
        final var startRating = searchSpace.currentState().constraint().rating();
        optimize(searchSpace, startRating);
    }

    private EnumerableOptimizationSpace optimize(EnumerableOptimizationSpace searchSpace, Rating startRating) {
        if (searchSpace.currentState().isComplete()) {
            return searchSpace;
        }
        for (int i = 0; i < searchSpace.childrenCount(); ++i) {
            final var nextChild = searchSpace.child(i);
            if (nextChild.currentState().constraint().rating().betterThanOrEquals(startRating)) {
                final var resultingChild = optimize(nextChild, startRating);
                if (resultingChild.currentState().isOptimal()) {
                    return resultingChild;
                }
            }
            searchSpace = nextChild.parent().get();
        }
        return searchSpace;
    }
}
