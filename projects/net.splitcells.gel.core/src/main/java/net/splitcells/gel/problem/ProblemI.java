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
package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.database.DatabaseSynchronization;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.problem.derived.DerivedSolution;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.Solutions;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.Solution;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.problem.derived.DerivedSolution.derivedSolution;

public class ProblemI implements Problem {

    private final Constraint constraint;
    private final Assignments assignments;
    private Solution asSolution;

    public static Problem problem(Assignments assignments, Constraint constraint) {
        final var problem = new ProblemI(assignments, constraint);
        problem.synchronize(constraint);
        return problem;
    }

    private ProblemI(Assignments assignments, Constraint constraint) {
        this.assignments = assignments;
        this.constraint = constraint;
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
        if (asSolution == null) {
            asSolution = Solutions.solution(this);
        }
        return asSolution;
    }

    @Override
    public DerivedSolution derived(Function<Rating, Rating> derivation) {
        return derivedSolution(() -> list(), assignments, constraint, derivation);
    }

    @Override
    public Database supplies() {
        return this.assignments.supplies();
    }

    @Override
    public Database suppliesUsed() {
        return this.assignments.suppliesUsed();
    }

    @Override
    public Database suppliesFree() {
        return this.assignments.suppliesFree();
    }

    @Override
    public Database demands() {
        return this.assignments.demands();
    }

    @Override
    public Database demandsUsed() {
        return this.assignments.demandsUsed();
    }

    @Override
    public Database demandsFree() {
        return this.assignments.demandsFree();
    }

    @Override
    public Line assign(final Line demand, final Line supply) {
        return this.assignments.assign(demand, supply);
    }

    @Override
    public Line demandOfAllocation(final Line allocation) {
        return this.assignments.demandOfAllocation(allocation);
    }

    @Override
    public Line supplyOfAllocation(final Line allocation) {
        return this.assignments.supplyOfAllocation(allocation);
    }

    @Override
    public Set<Line> allocationsOfSupply(final Line supply) {
        return this.assignments.allocationsOfSupply(supply);
    }

    @Override
    public Set<Line> assignmentsOf(final Line demand, final Line supply) {
        return this.assignments.assignmentsOf(demand, supply);
    }

    @Override
    public Line anyAssignmentOf(LinePointer demand, LinePointer supply) {
        return assignments.anyAssignmentOf(demand, supply);
    }

    @Override
    public Set<Line> allocationsOfDemand(final Line demand) {
        return this.assignments.allocationsOfDemand(demand);
    }

    @Override
    public Set<Line> supply_of_demand(final Line demand) {
        return this.assignments.supply_of_demand(demand);
    }

    @Override
    public Line addTranslated(List<?> values) {
        return this.assignments.addTranslated(values);
    }

    @Override
    public Line add(final Line line) {
        return this.assignments.add(line);
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        return assignments.addWithSameHeaderPrefix(line);
    }

    @Deprecated
    @Override
    public void remove(final int allocationIndex) {
        this.assignments.remove(allocationIndex);
    }

    @Override
    public void remove(final Line line) {
        this.assignments.remove(line);
    }

    @Override
    public void replace(final Line newLine) {
        this.assignments.replace(newLine);
    }

    @Override
    public void synchronize(DatabaseSynchronization subscriber) {
        this.assignments.synchronize(subscriber);
    }

    @Override
    public void subscribeToAfterAdditions(final AfterAdditionSubscriber subscriber) {
        this.assignments.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(final BeforeRemovalSubscriber subscriber) {
        this.assignments.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(final BeforeRemovalSubscriber subscriber) {
        this.assignments.subscribeToAfterRemoval(subscriber);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return this.assignments.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return this.assignments.headerView2();
    }

    @Override
    public <T extends Object> ColumnView<T> columnView(final Attribute<T> attribute) {
        return this.assignments.<T>columnView(attribute);
    }

    @Override
    public ListView<Column<Object>> columnsView() {
        return this.assignments.columnsView();
    }

    @Deprecated
    public ListView<Line> rawLinesView() {
        return this.assignments.rawLinesView();
    }

    @Override
    public boolean contains(final Line line) {
        return this.assignments.contains(line);
    }

    @Override
    public List<Line> unorderedLines() {
        return this.assignments.unorderedLines();
    }

    @Override
    public Line rawLine(final int index) {
        return this.assignments.rawLine(index);
    }

    @Override
    public int size() {
        return this.assignments.size();
    }

    @Override
    public boolean isEmpty() {
        return this.assignments.isEmpty();
    }

    @Override
    public boolean hasContent() {
        return this.assignments.hasContent();
    }

    @Override
    public List<Line> rawLines() {
        return this.assignments.rawLines();
    }

    public String toCSV() {
        return this.assignments.toCSV();
    }

    @Override
    public Line lookupEquals(final Attribute<Line> attribute, final Line value) {
        return this.assignments.lookupEquals(attribute, value);
    }

    @Override
    public Element toFods() {
        return this.assignments.toFods();
    }

    @Override
    public List<String> path() {
        return this.assignments.path();
    }

    public Node toDom() {
        return this.assignments.toDom();
    }

    @Override
    public Perspective toPerspective() {
        return assignments.toPerspective();
    }

    @Override
    public Object identity() {
        return this;
    }
}
