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
package net.splitcells.gel.solution.optimization.meta.hill.climber;

import java.util.Optional;
import java.util.function.Supplier;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.solution.history.meta.type.CompleteRating;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

@Deprecated
public class BranchingHillClimber implements OfflineOptimization {

    public static BranchingHillClimber branchingHillClimber() {
        return new BranchingHillClimber();
    }

    private final Supplier<Boolean> planner = () -> true;

    private BranchingHillClimber() {

    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final var nextBranch = nextBranch(solution);
        throw notImplementedYet();
    }

    private Optional<Solution> nextBranch(SolutionView solution) {
        final var rootRating = solution.constraint().rating();
        var bestNeighbour = Optional.<Solution>empty();
        while (planner.get()) {
            final var currentNeighbour = solution.branch();
            final var currentRating = solution
                    .history()
                    .lines()
                    .lastValue()
                    .orElseThrow()
                    .value(History.META_DATA)
                    .value(CompleteRating.class)
                    .orElseThrow()
                    .value();
            if (currentRating.betterThan(rootRating)) {
                bestNeighbour = Optional.of(currentNeighbour);
            }
        }
        return bestNeighbour;
    }
}
