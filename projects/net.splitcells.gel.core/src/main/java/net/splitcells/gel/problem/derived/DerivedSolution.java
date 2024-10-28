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
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.view.LinePointer;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.solution.history.Histories;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;

import java.util.function.Function;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.type.Derivation.derivation;


public class DerivedSolution implements Solution {

    private Constraint constraint;
    private final Assignments assignments;
    private final History history;
    private final Discoverable contexts;

    public static DerivedSolution derivedSolution(Discoverable context, Assignments assignments
            , Constraint originalConstraint, Function<Rating, Rating> derivation) {
        return new DerivedSolution(context, assignments, originalConstraint
                , derivation(originalConstraint, derivation));
    }

    public static DerivedSolution derivedSolution(Discoverable context, Assignments assignments
            , Constraint constraint, Constraint derivation) {
        return new DerivedSolution(context, assignments, constraint, derivation);
    }

    private DerivedSolution(Discoverable context, Assignments assignments, Constraint originalConstraints
            , Function<Rating, Rating> derivation) {
        this(context, assignments, originalConstraints, derivation(originalConstraints, derivation));
    }

    public static DerivedSolution derivedSolution(Discoverable contexts, Assignments assignments, Function<Solution, Constraint> constraintBuilder) {
        final var derivedSolution = new DerivedSolution(contexts, assignments);
        derivedSolution.constraint = constraintBuilder.apply(derivedSolution);
        return derivedSolution;
    }

    private DerivedSolution(Discoverable context, Assignments assignments, Constraint constraint
            , Constraint derivation) {
        this.assignments = assignments;
        this.constraint = derivation;
        this.history = Histories.history(this);
        this.contexts = context;
    }

    private DerivedSolution(Discoverable contexts, Assignments assignments) {
        this.assignments = assignments;
        history = Histories.history(this);
        this.contexts = contexts;
    }

    @Override
    public Constraint constraint() {
        return constraint;
    }

    @Override
    public Assignments allocations() {
        return assignments;
    }

    @Override
    public Solution asSolution() {
        throw notImplementedYet();
    }

    @Override
    public DerivedSolution derived(Function<Rating, Rating> derivation) {
        throw notImplementedYet();
    }

    @Override
    public Database supplies() {
        return assignments.supplies();
    }

    @Override
    public Database suppliesUsed() {
        return assignments.suppliesUsed();
    }

    @Override
    public Database suppliesFree() {
        return assignments.suppliesFree();
    }

    @Override
    public Database demands() {
        return assignments.demands();
    }

    @Override
    public Database demandsUsed() {
        return assignments.demandsUsed();
    }

    @Override
    public Database demandsFree() {
        return assignments.demandsFree();
    }

    @Override
    public Line assign(Line demand, Line supply) {
        return assignments.assign(demand, supply);
    }

    @Override
    public Line anyAssignmentOf(LinePointer demand, LinePointer supply) {
        return assignments.anyAssignmentOf(demand, supply);
    }

    @Override
    public Line demandOfAssignment(Line allocation) {
        return assignments.demandOfAssignment(allocation);
    }

    @Override
    public Line supplyOfAssignment(Line allocation) {
        return assignments.supplyOfAssignment(allocation);
    }

    @Override
    public Set<Line> assignmentsOfSupply(Line supply) {
        return assignments.assignmentsOfSupply(supply);
    }

    @Override
    public Set<Line> assignmentsOfDemand(Line demand) {
        return assignments.assignmentsOfDemand(demand);
    }

    @Override
    public Line addTranslated(ListView<?> values) {
        return assignments.addTranslated(values);
    }

    @Override
    public Line add(Line line) {
        return assignments.add(line);
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        return assignments.addWithSameHeaderPrefix(line);
    }

    @Override
    public void remove(int lineIndex) {
        assignments.remove(lineIndex);
    }

    @Override
    public void remove(Line line) {
        assignments.remove(line);
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        assignments.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        assignments.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        assignments.subscribeToAfterRemoval(subscriber);
    }

    @Override
    public String name() {
        return assignments.name();
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return assignments.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return assignments.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return assignments.columnView(attribute);
    }

    @Override
    public ListView<ColumnView<Object>> columnsView() {
        return assignments.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return assignments.rawLinesView();
    }

    @Override
    public int size() {
        return assignments.size();
    }

    @Override
    public List<Line> rawLines() {
        return assignments.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line other) {
        return assignments.lookupEquals(attribute, other);
    }

    @Override
    public Tree toTree() {
        return assignments.toTree();
    }

    @Override
    public List<String> path() {
        return contexts.path().withAppended(DerivedSolution.class.getSimpleName());
    }

    @Override
    public History history() {
        return history;
    }

    @Override
    public Object identity() {
        return this;
    }
}
