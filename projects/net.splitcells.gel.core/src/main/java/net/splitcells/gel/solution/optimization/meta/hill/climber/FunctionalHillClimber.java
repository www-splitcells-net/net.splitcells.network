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
package net.splitcells.gel.solution.optimization.meta.hill.climber;

import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.Optional;
import java.util.function.Supplier;

import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.gel.solution.optimization.meta.LinearIterator.linearIterator;
import static net.splitcells.gel.solution.optimization.primitive.FreeSupplySwitcher.freeSupplySwitcher;
import static net.splitcells.gel.solution.optimization.primitive.UsedSupplySwitcher.usedSupplySwitcher;

public class FunctionalHillClimber implements Optimization {

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

    public static FunctionalHillClimber functionalHillClimber(Optimization optimization, int i) {
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
    private final Optimization optimizationNeighbour;

    private FunctionalHillClimber(Optimization optimization, Supplier<Boolean> planner) {
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
                recommendations.forEach
                        (suggestion -> domsole().append
                                (suggestion.toDom()
                                        , () -> solution.path().withAppended
                                                (Optimization.class.getSimpleName()
                                                        , getClass().getSimpleName())
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

