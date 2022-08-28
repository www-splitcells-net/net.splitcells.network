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

import net.bytebuddy.implementation.bytecode.Throw;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.problem.derived.DerivedSolution;
import net.splitcells.gel.rating.framework.MetaRating;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import org.w3c.dom.Node;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.gel.common.Language.RATING;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;
import static org.assertj.core.api.Assertions.assertThat;

public class SolutionAspect implements Solution {
    public static SolutionAspect solutionAspect(Solution solution) {
        return new SolutionAspect(solution);
    }

    private final Solution solution;

    private SolutionAspect(Solution solution) {
        this.solution = solution;
    }

    @Override
    public Solution optimize(List<OptimizationEvent> events) {
        final var solution = Solution.super.optimize(events);
        if (StaticFlags.TRACING) {
            domsole().append
                    (constraint().rating()
                            , () -> solution.path().withAppended(RATING.value())
                            , LogLevel.TRACE);
        }
        return solution;
    }

    @Override
    public Rating rating(List<OptimizationEvent> event) {
        final var rating = Solution.super.rating(event);
        if (StaticFlags.TRACING) {
            domsole().append
                    (rating
                            , () -> solution.path().withAppended(RATING.value())
                            , LogLevel.TRACE);
        }
        return rating;
    }

    @ReturnsThis
    public Solution optimize(OptimizationEvent event, OptimizationParameters parameters) {
        if (event.demand().interpret().isEmpty()) {
            throw new IllegalArgumentException("Unknown demand: " + event.demand().index() + ", " + event.demand().context().path());
        }
        if (event.supply().interpret().isEmpty()) {
            throw new IllegalArgumentException("Unknown supply: " + event.supply().index() + ", " + event.supply().context().path());
        }
        if (event.stepType().equals(REMOVAL)) {
            event.demand()
                    .interpret()
                    .ifPresent(demandForRemoval
                            -> assertThat(demandForRemoval.context())
                            .describedAs("Context of demand for removal should be part demands or used demands, but is not: Removal context is " + demandForRemoval.context() + ", demands context is " + solution.demands() + " and used demands context is " + solution.demandsUsed() + ".")
                            .matches(e -> Table.referToSameData(e, solution.demands())
                                    || Table.referToSameData(e, solution.demandsUsed())));
            event.supply()
                    .interpret()
                    .ifPresent(supplyForRemoval
                            -> assertThat(list(supplyForRemoval.context()))
                            .containsAnyOf(solution.supplies(), solution.suppliesUsed()));
        }
        final var result = solution.optimize(event, parameters);
        if (StaticFlags.TELLING_STORY) {
            domsole().append(perspective("" + constraint().rating().asMetaRating().getContentValue(Cost.class).value())
                    , () -> path().withAppended("optimize", "after", "cost")
                    , LogLevel.DEBUG);
            if (isComplete()) {
                domsole().append(perspective(this.history().size() + ", " + constraint().rating().asMetaRating().getContentValue(Cost.class).value())
                        , () -> path().withAppended("isComplete", "optimize", "after", "cost")
                        , LogLevel.DEBUG);
            }
        }
        return result;
    }

    @Override
    public History history() {
        return solution.history();
    }

    @Override
    public Solution asSolution() {
        return solution.asSolution();
    }

    @Override
    public Line allocate(Line demand, Line supply) {
        return solution.allocate(demand, supply);
    }

    @Override
    public Line allocationOf(LinePointer demand, LinePointer supply) {
        return solution.allocationOf(demand, supply);
    }

    @Override
    public Line addTranslated(List<?> values) {
        return solution.addTranslated(values);
    }

    @Override
    public Line add(Line line) {
        return solution.add(line);
    }

    @Override
    public void remove(int lineIndex) {
        solution.remove(lineIndex);
    }

    @Override
    public void remove(Line line) {
        solution.remove(line);
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        solution.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        solution.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        solution.subscribeToAfterRemoval(subscriber);
    }

    @Override
    public Constraint constraint() {
        return solution.constraint();
    }

    @Override
    public Allocations allocations() {
        return solution.allocations();
    }

    @Override
    public DerivedSolution derived(Function<Rating, Rating> derivation) {
        return solution.derived(derivation);
    }

    @Override
    public Database supplies() {
        return solution.supplies();
    }

    @Override
    public Database suppliesUsed() {
        return solution.suppliesUsed();
    }

    @Override
    public Database suppliesFree() {
        return solution.suppliesFree();
    }

    @Override
    public Database demands() {
        return solution.demands();
    }

    @Override
    public Database demandsUsed() {
        return solution.demandsUsed();
    }

    @Override
    public Database demandsFree() {
        return solution.demandsFree();
    }

    @Override
    public Line demandOfAllocation(Line allocation) {
        return solution.demandOfAllocation(allocation);
    }

    @Override
    public Line supplyOfAllocation(Line allocation) {
        return solution.supplyOfAllocation(allocation);
    }

    @Override
    public Set<Line> allocationsOfSupply(Line supply) {
        return solution.allocationsOfSupply(supply);
    }

    @Override
    public Set<Line> allocationsOfDemand(Line demand) {
        return solution.allocationsOfDemand(demand);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return solution.headerView();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return solution.columnView(attribute);
    }

    @Override
    public List<Column<Object>> columnsView() {
        return solution.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return solution.rawLinesView();
    }

    @Override
    public int size() {
        return solution.size();
    }

    @Override
    public List<Line> rawLines() {
        return solution.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line value) {
        return solution.lookupEquals(attribute, value);
    }

    @Override
    public Node toDom() {
        return solution.toDom();
    }

    @Override
    public List<String> path() {
        return solution.path();
    }

    @Override
    public String toString() {
        return path().toString();
    }
}