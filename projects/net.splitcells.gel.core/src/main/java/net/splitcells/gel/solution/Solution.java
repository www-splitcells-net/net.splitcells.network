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
package net.splitcells.gel.solution;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Files.createDirectory;
import static net.splitcells.dem.resource.Files.writeToFile;
import static net.splitcells.gel.solution.OptimizationParameters.optimizationParameters;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * <p>The 80-20 rule maintains that 80% of outcomes come from 20% of causes. - Pareto principle - Vilfredo Pareto
 * </p>
 * <p>TODO IDEA Is it possible to model everything of a solution as one database? Does this make sense?
 * Does this make anything easier, by being able to store a solution as one singular thing which is a simple table?
 * Maybe one could implement a very low level compatibility system based on this for other software?</p>
 */
public interface Solution extends Problem, SolutionView {

    @ReturnsThis
    default Solution optimize(OfflineOptimization optimization) {
        return optimizeWithFunction(s -> optimization.optimize(s));
    }

    @ReturnsThis
    default Solution optimizeWithFunction(OfflineOptimization optimizationFunction) {
        return optimizeWithFunction(optimizationFunction, (currentSolution, i) -> !currentSolution.isOptimal());
    }

    @ReturnsThis
    default Solution optimizeWithFunction(OfflineOptimization optimizationFunction, BiPredicate<Solution, Integer> continuationCondition) {
        int i = 0;
        while (continuationCondition.test(this, i)) {
            final var recommendations = optimizationFunction.optimize(this);
            if (recommendations.isEmpty()) {
                break;
            }
            ++i;
            optimize(recommendations);
        }
        return this;
    }

    @ReturnsThis
    default Solution optimizeOnce(OfflineOptimization optimization) {
        return optimizeWithFunctionOnce(s -> optimization.optimize(s));
    }

    @ReturnsThis
    default Solution optimizeWithFunctionOnce(Function<Solution, List<OptimizationEvent>> optimization) {
        final var recommendations = optimization.apply(this);
        if (recommendations.isEmpty()) {
            return this;
        }
        optimize(recommendations);
        return this;
    }

    @ReturnsThis
    default Solution optimize(List<OptimizationEvent> events) {
        events.forEach(this::optimize);
        return this;
    }

    @ReturnsThis
    default Solution optimize(List<OptimizationEvent> events, OptimizationParameters parameters) {
        events.forEach(e -> optimize(e, parameters));
        return this;
    }

    @ReturnsThis
    default Solution optimize(OptimizationEvent event) {
        return optimize(event, optimizationParameters());
    }

    @ReturnsThis
    default Solution optimize(OptimizationEvent event, OptimizationParameters parameters) {
        if (event.stepType().equals(ADDITION)) {
            this.allocate(
                    demandsFree().getRawLine(event.demand().interpret().get().index()),
                    suppliesFree().getRawLine(event.supply().interpret().get().index()));
        } else if (event.stepType().equals(REMOVAL)) {
            final var demandBeforeRemoval = event.demand().interpret();
            final var supplyBeforeRemoval = event.supply().interpret();
            if (parameters.dublicateRemovalAllowed()) {
                if (demandBeforeRemoval.isEmpty() && supplyBeforeRemoval.isEmpty()) {
                    return this;
                }
            }
            remove(allocationsOf
                    (demandBeforeRemoval.get()
                            , supplyBeforeRemoval.get())
                    .iterator()
                    .next());
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    default void createAnalysis() {
        createDirectory(environment().config().configValue(ProcessPath.class));
        final var path = this.path().stream().reduce((left, right) -> left + "." + right);
        writeToFile(environment().config().configValue(ProcessPath.class).resolve(path + ".solution.constraint.toDom.xml"), constraint().toDom());
        writeToFile(environment().config().configValue(ProcessPath.class).resolve(path + ".solution.constraint.graph.xml"), constraint().graph());
    }

    default Rating rating(List<OptimizationEvent> events) {
        final var historyRootIndex = history().currentIndex();
        optimize(events);
        final var rating = constraint().rating();
        history().resetTo(historyRootIndex);
        return rating;
    }
}
