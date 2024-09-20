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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.database.DatabaseSynchronization;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.solution.history.Histories;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.problem.derived.DerivedSolution;

import java.util.function.Function;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class SolutionI implements Solution {
    private final Problem problem;
    private final History history;

    public static Solution solution(Problem problem) {
        return new SolutionI(problem);
    }

    public SolutionI(Problem problem) {
        this.problem = problem;
        history = Histories.history(this);
    }

    @Override
    public boolean isComplete() {
        return demandsFree().size() == 0
                || demands().size() < supplies().size()
                && demandsUsed().size() == supplies().size();
    }

    public boolean balanced() {
        return demands().rawLinesView().size() == supplies().rawLinesView().size();
    }

    public boolean tolerable(final long errorThreshold) {
        throw notImplementedYet();
    }

    @Override
    public History history() {
        return history;
    }

    @SuppressWarnings("all")
    public Constraint constraint() {
        return this.problem.constraint();
    }

    @Override
    public Assignments allocations() {
        return problem.allocations();
    }

    @SuppressWarnings("all")
    public Solution asSolution() {
        return this.problem.asSolution();
    }

    @Override
    public DerivedSolution derived(Function<Rating, Rating> derivation) {
        return problem.derived(derivation);
    }

    @SuppressWarnings("all")
    public Database supplies() {
        return this.problem.supplies();
    }

    @SuppressWarnings("all")
    public Database suppliesUsed() {
        return this.problem.suppliesUsed();
    }

    @SuppressWarnings("all")
    public Database suppliesFree() {
        return this.problem.suppliesFree();
    }

    @SuppressWarnings("all")
    public Database demands() {
        return this.problem.demands();
    }

    @SuppressWarnings("all")
    public Database demandsUsed() {
        return this.problem.demandsUsed();
    }

    @SuppressWarnings("all")
    public Database demandsFree() {
        return this.problem.demandsFree();
    }

    @SuppressWarnings("all")
    public Line assign(final Line demand, final Line supply) {
        return this.problem.assign(demand, supply);
    }

    @SuppressWarnings("all")
    public Line demandOfAssignment(final Line allocation) {
        return this.problem.demandOfAssignment(allocation);
    }

    @SuppressWarnings("all")
    public Line supplyOfAssignment(final Line allocation) {
        return this.problem.supplyOfAssignment(allocation);
    }

    @SuppressWarnings("all")
    public Set<Line> assignmentsOfSupply(final Line supply) {
        return this.problem.assignmentsOfSupply(supply);
    }

    @SuppressWarnings("all")
    public Set<Line> assignmentsOf(final Line demand, final Line supply) {
        return this.problem.assignmentsOf(demand, supply);
    }

    @Override
    public Line anyAssignmentOf(LinePointer demand, LinePointer supply) {
        return this.problem.anyAssignmentOf(demand, supply);
    }

    @SuppressWarnings("all")
    public Set<Line> assignmentsOfDemand(final Line demand) {
        return this.problem.assignmentsOfDemand(demand);
    }

    @SuppressWarnings("all")
    public Set<Line> suppliesOfDemand(final Line demand) {
        return this.problem.suppliesOfDemand(demand);
    }

    @SuppressWarnings("all")
    public Line addTranslated(final ListView<?> values) {
        return this.problem.addTranslated(values);
    }

    @SuppressWarnings("all")
    public Line add(final Line line) {
        return this.problem.add(line);
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        return problem.addWithSameHeaderPrefix(line);
    }

    @SuppressWarnings("all")
    public void remove(final int lineIndex) {
        this.problem.remove(lineIndex);
    }

    @SuppressWarnings("all")
    public void remove(final Line line) {
        this.problem.remove(line);
    }

    @SuppressWarnings("all")
    public void replace(final Line newLine) {
        this.problem.replace(newLine);
    }

    @SuppressWarnings("all")
    public void synchronize(DatabaseSynchronization subscriber) {
        this.problem.synchronize(subscriber);
    }

    @SuppressWarnings("all")
    public void subscribeToAfterAdditions(final AfterAdditionSubscriber subscriber) {
        this.problem.subscribeToAfterAdditions(subscriber);
    }

    @SuppressWarnings("all")
    public void subscribeToBeforeRemoval(final BeforeRemovalSubscriber subscriber) {
        this.problem.subscribeToBeforeRemoval(subscriber);
    }

    @SuppressWarnings("all")
    public void subscribeToAfterRemoval(final BeforeRemovalSubscriber listener) {
        this.problem.subscribeToAfterRemoval(listener);
    }

    @Override
    public String name() {
        return problem.name();
    }

    @SuppressWarnings("all")
    public List<Attribute<Object>> headerView() {
        return this.problem.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return problem.headerView2();
    }

    @SuppressWarnings("all")
    public <T extends Object> ColumnView<T> columnView(final Attribute<T> attribute) {
        return this.problem.<T>columnView(attribute);
    }

    @SuppressWarnings("all")
    public ListView<ColumnView<Object>> columnsView() {
        return this.problem.columnsView();
    }

    @Deprecated
    @SuppressWarnings("all")
    public ListView<Line> rawLinesView() {
        return this.problem.rawLinesView();
    }

    @SuppressWarnings("all")
    public boolean contains(final Line line) {
        return this.problem.contains(line);
    }

    @SuppressWarnings("all")
    public List<Line> unorderedLines() {
        return this.problem.unorderedLines();
    }

    @SuppressWarnings("all")
    public Line rawLine(final int index) {
        return this.problem.rawLine(index);
    }

    @SuppressWarnings("all")
    public int size() {
        return this.problem.size();
    }

    @SuppressWarnings("all")
    public boolean isEmpty() {
        return this.problem.isEmpty();
    }

    @SuppressWarnings("all")
    public boolean hasContent() {
        return this.problem.hasContent();
    }

    @SuppressWarnings("all")
    public List<Line> rawLines() {
        return this.problem.rawLines();
    }

    @SuppressWarnings("all")
    public String toCSV() {
        return this.problem.toCSV();
    }

    @SuppressWarnings("all")
    public Line lookupEquals(final Attribute<Line> attribute, final Line line) {
        return this.problem.lookupEquals(attribute, line);
    }

    @SuppressWarnings("all")
    public Perspective toFods() {
        return this.problem.toFods();
    }

    @SuppressWarnings("all")
    public List<String> path() {
        return this.problem.path();
    }

    @SuppressWarnings("all")
    public org.w3c.dom.Node toDom() {
        return this.problem.toDom();
    }

    @Override
    public Perspective toPerspective() {
        return problem.toPerspective();
    }

    @Override
    public String toString() {
        return path().toString();
    }

    @Override
    public Object identity() {
        return this;
    }

    @Override
    public List<Line> orderedLines() {
        return problem.orderedLines();
    }
}
