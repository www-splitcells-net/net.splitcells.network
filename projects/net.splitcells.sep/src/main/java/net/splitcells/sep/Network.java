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
package net.splitcells.sep;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OnlineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationConfig;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.data.set.map.Maps.map;

/**
 * <p>TODO Migrate this into {@link net.splitcells.gel.editor.Editor}.</p>
 * <p>TODO Clean up the interface and document intent of this.</p>
 * <p>TODO Ensure that all solutions have a name,
 * because otherwise it can get hard pretty easily to distinguish one {@link net.splitcells.gel.problem.Problem}
 * from another.</p>
 * <p>TODO Make this based on {@link net.splitcells.dem.execution.EffectSystem}.
 * Note that {@link net.splitcells.dem.execution.EffectSystem} is not in a good
 * state and needs to be fixed first.</p>
 */
@Deprecated
public class Network {
    public static Network network() {
        return new Network();
    }

    private final Map<String, Solution> solutions = map();

    private Network() {

    }

    @ReturnsThis
    public Network withNode(String key, Solution solution) {
        if (solutions.containsKey(key)) {
            throw new IllegalArgumentException(key);
        }
        solutions.put(key, solution);
        return this;
    }

    @ReturnsThis
    public Network withNode(String key, Function<Solution, Solution> constructor, String dependencyKey) {
        if (solutions.containsKey(key)) {
            throw new IllegalArgumentException(key);
        }
        withNode(key, constructor.apply(solutions.get(dependencyKey)));
        return this;
    }

    @ReturnsThis
    public Network withExecution(String argumentKey, Consumer<Solution> execution) {
        execution.accept(solutions.get(argumentKey));
        return this;
    }

    public Solution node(String argumentKey) {
        return solutions.get(argumentKey);
    }

    public <T> T extract(String argumentKey, Function<Solution, T> execution) {
        return execution.apply(solutions.get(argumentKey));
    }

    public void process(String argumentKey, Consumer<Solution> execution) {
        execution.accept(solutions.get(argumentKey));
    }

    public void processAll(Consumer<Solution> execution) {
        solutions.values().forEach(execution::accept);
    }

    @ReturnsThis
    public Network withOptimization(String argumentKey, OfflineOptimization execution) {
        return withExecution(argumentKey, s -> s.optimize(execution));
    }

    @ReturnsThis
    public Network withOptimization(String argumentKey, OnlineOptimization optimization) {
        return withExecution(argumentKey, s -> {
            optimization.optimize(s);
        });
    }

    @ReturnsThis
    public Network withOptimization(String argumentKey, OnlineOptimization optimization, OptimizationConfig config) {
        return withExecution(argumentKey, s -> {
            s.optimize(optimization, config);
        });
    }

    @ReturnsThis
    public Network withOptimization(String argumentKey, OfflineOptimization optimizationFunction, BiPredicate<Solution, Integer> continuationCondition) {
        final var solution = solutions.get(argumentKey);
        int i = 0;
        while (continuationCondition.test(solution, i)) {
            final var recommendations = optimizationFunction.optimize(solution);
            if (recommendations.isEmpty()) {
                break;
            }
            ++i;
            solution.optimize(recommendations);
        }
        return this;
    }

    @ReturnsThis
    public Network withOptimization(String argumentKey, OnlineOptimization optimizationFunction, BiPredicate<Solution, Integer> continuationCondition) {
        final var solution = solutions.get(argumentKey);
        int i = 0;
        while (continuationCondition.test(solution, i)) {
            if (solution.isOptimal()) {
                break;
            }
            ++i;
            optimizationFunction.optimize(solution);
        }
        return this;
    }
}
