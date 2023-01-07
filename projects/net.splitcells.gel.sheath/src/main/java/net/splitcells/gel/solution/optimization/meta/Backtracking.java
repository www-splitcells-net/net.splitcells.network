/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OnlineOptimization;
import net.splitcells.gel.solution.optimization.space.EnumerableOptimizationSpace;

import static net.splitcells.gel.solution.optimization.primitive.enumerable.Initializer.initializer;
import static net.splitcells.gel.solution.optimization.space.EnumerableOptimizationSpaceI.enumerableOptimizationSpace;

/**
 * <p>This is an implementation of the backtracking algorithm.
 * The backtracking exits, when the {@link Solution#isComplete()} is {@link true}.
 * This implementation backtracks,
 * if a given {@link Solution} is worse than the previous {@link Solution}
 * found via {@link EnumerableOptimizationSpace#parent()}.</p>
 * <p>TODO Record best found {@link Solution},
 * that is not specific to an optimizer.</p>
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
            searchSpace = nextChild.parent().orElseThrow();
        }
        return searchSpace;
    }
}
