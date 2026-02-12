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
package net.splitcells.gel.solution;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.Xml.attribute;
import static net.splitcells.dem.resource.Files.createDirectory;
import static net.splitcells.dem.resource.Files.writeToFile;
import static net.splitcells.dem.testing.reporting.ErrorReporting.getWithReportedErrors;
import static net.splitcells.gel.proposal.Proposals.proposalsForConstraintTree;
import static net.splitcells.gel.solution.OptimizationParameters.optimizationParameters;
import static net.splitcells.gel.solution.optimization.DefaultOptimization.defaultOptimization;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.testing.reporting.ErrorReporter;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OnlineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationConfig;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * <p>The 80-20 rule maintains that 80% of outcomes come from 20% of causes. - Pareto principle - Vilfredo Pareto
 * </p>
 * <p>TODO Clean up optimize functions. Their usage is too complicated.</p>
 * <p>TODO IDEA Is it possible to model everything of a solution as one database? Does this make sense?
 * Does this make anything easier, by being able to store a solution as one singular thing which is a simple table?
 * Maybe one could implement a very low level compatibility system based on this for other software?</p>
 */
public interface Solution extends Problem, SolutionView {

    default Proposal propose() {
        return proposalsForConstraintTree(this);
    }

    /**
     * @return Returns this with the default optimization being applied to it.
     */
    default Solution optimize() {
        defaultOptimization().optimize(this);
        return this;
    }

    @ReturnsThis
    default Solution optimize(OfflineOptimization optimization) {
        return optimizeWithFunction(s -> optimization.optimize(s));
    }

    @ReturnsThis
    default Solution optimize(OnlineOptimization optimization) {
        return optimizeOnlineWithFunction(s -> optimization.optimize(s));
    }

    /**
     * @param optimization
     * @param config
     * @return
     * @deprecated This should done by via an {@link OnlineOptimization} wrapper and not via the {@link Solution} interface,
     * in order to not clutter the interface.
     */
    @Deprecated
    @ReturnsThis
    default Solution optimize(OnlineOptimization optimization, OptimizationConfig config) {
        return optimizeOnlineWithFunction(s -> optimization.optimize(s), config);
    }

    /**
     * @param optimizationFunction
     * @return
     * @deprecated This should done by via an {@link OnlineOptimization} wrapper and not via the {@link Solution} interface,
     * in order to not clutter the interface.
     */
    @Deprecated
    @ReturnsThis
    default Solution optimizeWithFunction(OfflineOptimization optimizationFunction) {
        return optimizeWithFunction(optimizationFunction, (currentSolution, i) -> !currentSolution.isOptimal());
    }

    /**
     * @param optimizationFunction
     * @return
     * @deprecated This should done by via an {@link OnlineOptimization} wrapper and not via the {@link Solution} interface,
     * in order to not clutter the interface.
     */
    @Deprecated
    @ReturnsThis
    default Solution optimizeOnlineWithFunction(OnlineOptimization optimizationFunction) {
        return optimizeWithFunction(optimizationFunction, (currentSolution, i) -> !currentSolution.isOptimal());
    }

    /**
     * @param optimizationFunction
     * @param config
     * @return
     * @deprecated This should done by via an {@link OnlineOptimization} wrapper and not via the {@link Solution} interface,
     * in order to not clutter the interface.
     */
    @Deprecated
    @ReturnsThis
    default Solution optimizeOnlineWithFunction(OnlineOptimization optimizationFunction, OptimizationConfig config) {
        return optimizeWithFunction(optimizationFunction, (currentSolution, i) -> !currentSolution.isOptimal(), config);
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

    /**
     * @param optimizationFunction
     * @param continuationCondition
     * @return
     * @deprecated This is deprecated, because this method checks automatically,
     * if the {@code optimizationFunction} did nothing and thereby aborts the executions.
     * This should done by via an {@link OnlineOptimization} wrapper and not via the {@link Solution} interface.
     */
    @Deprecated
    @ReturnsThis
    default Solution optimizeWithFunction(OnlineOptimization optimizationFunction, BiPredicate<Solution, Integer> continuationCondition) {
        int i = 0;
        while (continuationCondition.test(this, i)) {
            final int startAge = history().size();
            optimizationFunction.optimize(this);
            if (startAge == history().size()) {
                break;
            }
            ++i;
        }
        return this;
    }

    /**
     * @param optimizationFunction
     * @param continuationCondition
     * @return
     * @deprecated This should done by via an {@link OnlineOptimization} wrapper and not via the {@link Solution} interface,
     * in order to not clutter the interface.
     */
    @Deprecated
    @ReturnsThis
    default Solution optimizeWithMethod(OnlineOptimization optimizationFunction, BiPredicate<Solution, Integer> continuationCondition) {
        int i = 0;
        while (continuationCondition.test(this, i)) {
            optimizationFunction.optimize(this);
            ++i;
        }
        return this;
    }

    /**
     * @param optimizationFunction
     * @param continuationCondition
     * @param config
     * @return
     * @deprecated This should done by via an {@link OnlineOptimization} wrapper and not via the {@link Solution} interface,
     * in order to not clutter the interface.
     */
    @Deprecated
    @ReturnsThis
    default Solution optimizeWithFunction(OnlineOptimization optimizationFunction, BiPredicate<Solution, Integer> continuationCondition, OptimizationConfig config) {
        if (config.recordHistory()) {
            if (config.optimizeOnce()) {
                optimizationFunction.optimize(this);
            } else {
                int i = 0;
                while (continuationCondition.test(this, i)) {
                    final int startAge = history().size();
                    optimizationFunction.optimize(this);
                    if (startAge == history().size()) {
                        break;
                    }
                    ++i;
                }
            }
        } else {
            history().processWithoutHistory(() -> {
                if (config.optimizeOnce()) {
                    optimizationFunction.optimize(this);
                } else {
                    int i = 0;
                    while (continuationCondition.test(this, i)) {
                        optimizationFunction.optimize(this);
                        ++i;
                    }
                }
            });
        }
        return this;
    }

    /**
     * @param optimizationFunction
     * @param config
     * @return
     * @deprecated This should done by via an {@link OnlineOptimization} wrapper and not via the {@link Solution} interface,
     * in order to not clutter the interface.
     */
    @Deprecated
    @ReturnsThis
    default Solution optimizeWithFunction(OnlineOptimization optimizationFunction, OptimizationConfig config) {
        if (config.recordHistory()) {
            if (config.optimizeOnce()) {
                optimizationFunction.optimize(this);
            } else {
                int i = 0;
                while (config.continuationCondition().test(this, i)) {
                    final int startAge = history().size();
                    optimizationFunction.optimize(this);
                    if (startAge == history().size()) {
                        break;
                    }
                    ++i;
                }
            }
        } else {
            history().processWithoutHistory(() -> {
                if (config.optimizeOnce()) {
                    optimizationFunction.optimize(this);
                } else {
                    int i = 0;
                    while (config.continuationCondition().test(this, i)) {
                        optimizationFunction.optimize(this);
                        ++i;
                    }
                }
            });
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
            this.assign(
                    demandsFree().rawLine(event.demand().interpret().orElseThrow().index()),
                    suppliesFree().rawLine(event.supply().interpret().orElseThrow().index()));
        } else if (event.stepType().equals(REMOVAL)) {
            final var demandBeforeRemoval = event.demand().interpret();
            final var supplyBeforeRemoval = event.supply().interpret();
            if (parameters.dublicateRemovalAllowed()
                    && demandBeforeRemoval.isEmpty()
                    && supplyBeforeRemoval.isEmpty()) {
                return this;
            }
            remove(assignmentsOf
                    (demandBeforeRemoval.orElseThrow()
                            , supplyBeforeRemoval.orElseThrow())
                    .iterator()
                    .next());
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    default void createAnalysis() {
        final var dataContainer = dataContainer();
        createDirectory(dataContainer);
        writeToFile(dataContainer.resolve("solution.constraint.toDom.xml"), constraint().toTree());
        writeToFile(dataContainer.resolve("solution.constraint.graph.xml"), constraint().graph());
    }

    default Rating rating(List<OptimizationEvent> events) {
        final var historyRootIndex = history().currentIndex();
        optimize(events);
        final var rating = constraint().rating();
        history().resetTo(historyRootIndex);
        return rating;
    }

    /**
     * <p>Initializes the {@link #constraint()}.</p>
     * <p>TODO Introduce a solve methode,
     * that always calls init and maybe hide methods like optimize behind an indirection.</p>
     */
    default void init() {
        constraint().init(this);
    }


    /**
     * @deprecated Only return String matrices like {@link #toReformattedTable(List, List)},
     * in order to make it portable.
     */
    @Deprecated
    default String toSimplifiedCSV(ErrorReporter reporter) {
        return getWithReportedErrors(this::toSimplifiedCSV, reporter);
    }

    /**
     * @deprecated Only return String matrices like {@link #toReformattedTable(List, List)},
     * in order to make it portable.
     */
    @Override
    default String toSimplifiedCSV() {
        final var simplifiedCsv = new StringBuilder();
        simplifiedCsv.append(headerView().stream()
                .map(Attribute::name)
                .reduce("", StringUtils::mergeSimplifiedCsvList)
                + ",argumentation\n");

        unorderedLines().forEach(line -> simplifiedCsv.append(line.values().stream()
                .map(Object::toString)
                .reduce("", StringUtils::mergeSimplifiedCsvList)
                + ","
                + singleLineArgumentation(line).orElse("")
                + "\n"));
        return simplifiedCsv.toString();
    }

    /**
     * TODO Move this to {@link #constraint()}.
     *
     * @param allocation
     * @return
     */
    @Deprecated
    default Optional<String> singleLineArgumentation(Line allocation) {
        // TODO The comma replacement is an hack.
        return constraint().naturalArgumentation(allocation, constraint().injectionGroup())
                .map(argumentation -> argumentation.toStringPaths()
                        .stream()
                        .reduce((a, b) -> a + ". " + b)
                        .orElse("")
                        .replace(',', ' '));
    }

    @Override default Optional<Solution> lookupAsSolution() {
        return Optional.of(this);
    }
}
