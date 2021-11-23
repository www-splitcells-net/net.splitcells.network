package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OnlineOptimization;
import net.splitcells.gel.solution.optimization.space.EnumerableOptimizationSpace;

import java.util.Optional;
import java.util.stream.IntStream;

import static net.splitcells.gel.solution.optimization.primitive.enumerable.Initializer.initializer;
import static net.splitcells.gel.solution.optimization.space.EnumerableOptimizationSpaceI.enumerableOptimizationSpace;

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

    private void optimize(EnumerableOptimizationSpace searchSpace, Rating startRating) {
        IntStream.range(0, searchSpace.childrenCount())
                .map(i -> {
                    final var nextChild = searchSpace.child(i);
                    if (nextChild.currentState().constraint().rating().betterThanOrEquals(startRating)) {
                        optimize(nextChild, startRating);
                        return 1;
                    } else {
                        nextChild.parent();
                        return 0;
                    }
                })
                .filter(e -> e == 1)
                .findFirst();
    }
}
