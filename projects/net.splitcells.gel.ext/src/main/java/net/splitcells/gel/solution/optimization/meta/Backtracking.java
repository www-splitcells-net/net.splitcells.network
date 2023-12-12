/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
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
 * The backtracking exits, when the {@link Solution#isComplete()} is {@code true}.
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
