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
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.AfterAdditionSubscriber;
import net.splitcells.gel.data.table.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.LinePointer;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.type.Optimality;
import net.splitcells.gel.solution.history.History;

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

    protected SimplifiedAnnealingProblem(Assignments assignments, Constraint originalConstraint
            , Function<Integer, Double> temperatureFunction) {
        this(assignments, originalConstraint, temperatureFunction, randomness());
    }

    protected SimplifiedAnnealingProblem(Assignments assignments, Constraint originalConstraint
            , Function<Integer, Double> temperatureFunction
            , Randomness randomness) {
        originalSolution = derivedSolution(() -> list()
                , assignments
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
    public List<String> path() {
        return originalSolution.path();
    }

    @Override
    public Line assign(Line demand, Line supply) {
        return originalSolution.assign(demand, supply);
    }

    @Override
    public Line anyAssignmentOf(LinePointer demand, LinePointer supply) {
        return originalSolution.anyAssignmentOf(demand, supply);
    }

    @Override
    public Table supplies() {
        return originalSolution.supplies();
    }

    @Override
    public Table suppliesUsed() {
        return originalSolution.suppliesUsed();
    }

    @Override
    public Table suppliesFree() {
        return originalSolution.suppliesFree();
    }

    @Override
    public Table demands() {
        return originalSolution.demands();
    }

    @Override
    public Table demandsUsed() {
        return originalSolution.demandsUsed();
    }

    @Override
    public Table demandsFree() {
        return originalSolution.demandsFree();
    }

    @Override
    public Line demandOfAssignment(Line allocation) {
        return originalSolution.demandOfAssignment(allocation);
    }

    @Override
    public Line supplyOfAssignment(Line allocation) {
        return originalSolution.supplyOfAssignment(allocation);
    }

    @Override
    public Set<Line> assignmentsOfSupply(Line supply) {
        return originalSolution.assignmentsOfSupply(supply);
    }

    @Override
    public Set<Line> assignmentsOfDemand(Line demand) {
        return originalSolution.assignmentsOfDemand(demand);
    }

    @Override
    public Line addTranslated(ListView<?> values) {
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
    public String name() {
        return "Simplified annealing problem of " + originalSolution.name();
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
    public ListView<ColumnView<Object>> columnsView() {
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
    public Assignments allocations() {
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
