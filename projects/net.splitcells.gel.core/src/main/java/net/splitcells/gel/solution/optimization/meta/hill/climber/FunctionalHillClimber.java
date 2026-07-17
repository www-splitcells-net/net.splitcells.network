/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.meta.hill.climber;

import lombok.val;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.Optional;
import java.util.function.Supplier;

import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.gel.solution.optimization.meta.LinearIterator.linearIterator;
import static net.splitcells.gel.solution.optimization.primitive.FreeSupplySwitcher.freeSupplySwitcher;
import static net.splitcells.gel.solution.optimization.primitive.UsedSupplySwitcher.usedSupplySwitcher;

public class FunctionalHillClimber implements OfflineOptimization {

    public static FunctionalHillClimber functionalHillClimber(int i) {
        return new FunctionalHillClimber
                (linearIterator
                        (usedSupplySwitcher()
                                , freeSupplySwitcher())
                        , new Supplier<Boolean>() {
                    int counter = 0;

                    @Override
                    public Boolean get() {
                        final var rVal = counter < i;
                        counter += 1;
                        return rVal;
                    }
                });
    }

    public static FunctionalHillClimber functionalHillClimber(OfflineOptimization optimization, int i) {
        return new FunctionalHillClimber(optimization, new Supplier<Boolean>() {
            int counter = 0;

            @Override
            public Boolean get() {
                final var rVal = counter < i;
                counter += 1;
                return rVal;
            }
        });
    }

    private final Supplier<Boolean> planner;
    private final OfflineOptimization optimizationNeighbour;

    private FunctionalHillClimber(OfflineOptimization optimization, Supplier<Boolean> planner) {
        this.planner = planner;
        this.optimizationNeighbour = optimization;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final var rootRating = solution.constraint().rating();
        final var rootHistoryIndex = solution.history().currentIndex();
        Optional<Rating> bestNeighbourRating = Optional.empty();
        List<OptimizationEvent> bestNeighbourOperation = list();
        while (planner.get()) {
            final var recommendations = optimizationNeighbour.optimize(solution);
            if (recommendations.isEmpty()) {
                continue;
            }
            if (TRACING) {
                val logPath = solution.path().shallowCopy().withAppended(OfflineOptimization.class.getSimpleName(), getClass().getSimpleName());
                recommendations.forEach(suggestion -> logs().append
                        (suggestion.toTree()
                                , () -> logPath
                                , LogLevel.TRACE)
                );
            }
            final var currentRating = solution.rating(recommendations);
            if (bestNeighbourRating.isEmpty()
                    || currentRating.betterThan(bestNeighbourRating.get())) {
                bestNeighbourRating = Optional.of(currentRating);
                bestNeighbourOperation = recommendations;
            }
            solution.history().resetTo(rootHistoryIndex);
        }
        if (!bestNeighbourRating.isEmpty() && bestNeighbourRating.get().betterThan(rootRating)) {
            return bestNeighbourOperation;
        }
        return list();
    }
}

