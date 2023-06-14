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
package net.splitcells.gel.problem.derived;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.framework.MetaRating;
import net.splitcells.gel.rating.type.Optimality;
import net.splitcells.gel.solution.history.History;
import org.w3c.dom.Node;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.constraint.type.Derivation.derivation;
import static net.splitcells.gel.problem.derived.DerivedSolution.derivedSolution;

/**
 * TODO Create a test demonstrating its usefulness by applied simulated annealing to a fitting problem,
 * that cannot be solved by the random hill climber based on single value changes.
 */
public class SimplifiedAnnealingProblem implements Solution {

    private final Solution originalSolution;

    public static Solution simplifiedAnnealingProblem(Solution solution) {
        return simplifiedAnnealingProblem(solution, i ->
                1f / (i.doubleValue() + 1f));
    }

    public static Solution simplifiedAnnealingProblem(Solution solution, Function<Integer, Double> temperatureFunction) {
        return new SimplifiedAnnealingProblem(solution.allocations(), solution.constraint(), temperatureFunction);
    }

    public static Solution simplifiedAnnealingProblem(Solution solution, Function<Integer, Double> temperatureFunction
            , Randomness randomness) {
        return new SimplifiedAnnealingProblem(solution.allocations(), solution.constraint(), temperatureFunction
                , randomness);
    }

    protected SimplifiedAnnealingProblem(Allocations allocations, Constraint originalConstraint
            , Function<Integer, Double> temperatureFunction) {
        this(allocations, originalConstraint, temperatureFunction, randomness());
    }

    protected SimplifiedAnnealingProblem(Allocations allocations, Constraint originalConstraint
            , Function<Integer, Double> temperatureFunction
            , Randomness randomness) {
        originalSolution = derivedSolution(() -> list()
                , allocations
                , s -> derivation(originalConstraint
                        , rating -> s.history().supplyWithHistory(() -> {
                            if (randomness.truthValue(temperatureFunction.apply(s.history().size()))) {
                                return Optimality.optimality(1).asMetaRating();
                            }
                            return rating;
                        })));
    }

    @Override
    public Object identity() {
        return originalSolution.identity();
    }

    @Override
    public Node toDom() {
        return originalSolution.toDom();
    }

    @Override
    public List<String> path() {
        return originalSolution.path();
    }

    @Override
    public Line allocate(Line demand, Line supply) {
        return originalSolution.allocate(demand, supply);
    }

    @Override
    public Line allocationOf(LinePointer demand, LinePointer supply) {
        return originalSolution.allocationOf(demand, supply);
    }

    @Override
    public Database supplies() {
        return originalSolution.supplies();
    }

    @Override
    public Database suppliesUsed() {
        return originalSolution.suppliesUsed();
    }

    @Override
    public Database suppliesFree() {
        return originalSolution.suppliesFree();
    }

    @Override
    public Database demands() {
        return originalSolution.demands();
    }

    @Override
    public Database demandsUsed() {
        return originalSolution.demandsUsed();
    }

    @Override
    public Database demandsFree() {
        return originalSolution.demandsFree();
    }

    @Override
    public Line demandOfAllocation(Line allocation) {
        return originalSolution.demandOfAllocation(allocation);
    }

    @Override
    public Line supplyOfAllocation(Line allocation) {
        return originalSolution.supplyOfAllocation(allocation);
    }

    @Override
    public Set<Line> allocationsOfSupply(Line supply) {
        return originalSolution.allocationsOfSupply(supply);
    }

    @Override
    public Set<Line> allocationsOfDemand(Line demand) {
        return originalSolution.allocationsOfDemand(demand);
    }

    @Override
    public Line addTranslated(List<?> values) {
        return originalSolution.addTranslated(values);
    }

    @Override
    public Line add(Line line) {
        return originalSolution.add(line);
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        return originalSolution.addWithSameHeaderPrefix(line);
    }

    @Override
    public void remove(int lineIndex) {
        originalSolution.remove(lineIndex);
    }

    @Override
    public void remove(Line line) {
        originalSolution.remove(line);
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        originalSolution.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        originalSolution.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        originalSolution.subscribeToAfterRemoval(subscriber);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return originalSolution.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return originalSolution.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return originalSolution.columnView(attribute);
    }

    @Override
    public ListView<Column<Object>> columnsView() {
        return originalSolution.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return originalSolution.rawLinesView();
    }

    @Override
    public int size() {
        return originalSolution.size();
    }

    @Override
    public List<Line> rawLines() {
        return originalSolution.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line values) {
        return originalSolution.lookupEquals(attribute, values);
    }

    @Override
    public Solution asSolution() {
        return originalSolution.asSolution();
    }

    @Override
    public Constraint constraint() {
        return originalSolution.constraint();
    }

    @Override
    public Allocations allocations() {
        return originalSolution.allocations();
    }

    @Override
    public DerivedSolution derived(Function<Rating, Rating> derivation) {
        return originalSolution.derived(derivation);
    }

    @Override
    public History history() {
        return originalSolution.history();
    }
}
