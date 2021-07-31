/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.solution.optimization.meta.hill.climber;

import java.util.Optional;
import java.util.function.Supplier;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.solution.history.meta.type.CompleteRating;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

@Deprecated
public class BranchingHillClimber implements Optimization {

    public static BranchingHillClimber branchingHillClimber() {
        return new BranchingHillClimber();
    }

    private final Supplier<Boolean> planner = () -> true;

    private BranchingHillClimber() {

    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final var nextBranch = nextBranch(solution);
        return nextOperation(nextBranch.get());
    }

    private List<OptimizationEvent> nextOperation(Solution branch) {
        throw notImplementedYet();
    }

    private Optional<Solution> nextBranch(SolutionView solution) {
        final var rootRating = solution.constraint().rating();
        var bestNeighbour = Optional.<Solution>empty();
        while (planner.get()) {
            final var currentNeighbour = solution.branch();
            final var currentRating = solution
                    .history()
                    .getLines()
                    .lastValue()
                    .get()
                    .value(History.META_DATA)
                    .value(CompleteRating.class)
                    .get()
                    .value();
            if (currentRating.betterThan(rootRating)) {
                bestNeighbour = Optional.of(currentNeighbour);
            }
        }
        return bestNeighbour;
    }
}
