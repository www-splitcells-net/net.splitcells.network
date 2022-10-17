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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.database.DatabaseSynchronization;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.solution.history.Histories;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.allocation.Allocations;
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

    public boolean tolerable(final long error_threshold) {
        throw notImplementedYet();
    }

    @Override
    public History history() {
        return history;
    }

    @java.lang.SuppressWarnings("all")
    public Constraint constraint() {
        return this.problem.constraint();
    }

    @Override
    public Allocations allocations() {
        return problem.allocations();
    }

    @java.lang.SuppressWarnings("all")
    public Solution asSolution() {
        return this.problem.asSolution();
    }

    @Override
    public DerivedSolution derived(Function<Rating, Rating> derivation) {
        return problem.derived(derivation);
    }

    @java.lang.SuppressWarnings("all")
    public Database supplies() {
        return this.problem.supplies();
    }

    @java.lang.SuppressWarnings("all")
    public Database suppliesUsed() {
        return this.problem.suppliesUsed();
    }

    @java.lang.SuppressWarnings("all")
    public Database suppliesFree() {
        return this.problem.suppliesFree();
    }

    @java.lang.SuppressWarnings("all")
    public Database demands() {
        return this.problem.demands();
    }

    @java.lang.SuppressWarnings("all")
    public Database demandsUsed() {
        return this.problem.demandsUsed();
    }

    @java.lang.SuppressWarnings("all")
    public Database demandsFree() {
        return this.problem.demandsFree();
    }

    @java.lang.SuppressWarnings("all")
    public Line allocate(final Line demand, final Line supply) {
        return this.problem.allocate(demand, supply);
    }

    @java.lang.SuppressWarnings("all")
    public Line demandOfAllocation(final Line allocation) {
        return this.problem.demandOfAllocation(allocation);
    }

    @java.lang.SuppressWarnings("all")
    public Line supplyOfAllocation(final Line allocation) {
        return this.problem.supplyOfAllocation(allocation);
    }

    @java.lang.SuppressWarnings("all")
    public Set<Line> allocationsOfSupply(final Line supply) {
        return this.problem.allocationsOfSupply(supply);
    }

    @java.lang.SuppressWarnings("all")
    public Set<Line> allocationsOf(final Line demand, final Line supply) {
        return this.problem.allocationsOf(demand, supply);
    }

    @Override
    public Line allocationOf(LinePointer demand, LinePointer supply) {
        return this.problem.allocationOf(demand, supply);
    }

    @java.lang.SuppressWarnings("all")
    public Set<Line> allocationsOfDemand(final Line demand) {
        return this.problem.allocationsOfDemand(demand);
    }

    @java.lang.SuppressWarnings("all")
    public Set<Line> supply_of_demand(final Line demand) {
        return this.problem.supply_of_demand(demand);
    }

    @java.lang.SuppressWarnings("all")
    public Line addTranslated(final List<?> values) {
        return this.problem.addTranslated(values);
    }

    @java.lang.SuppressWarnings("all")
    public Line add(final Line line) {
        return this.problem.add(line);
    }

    @java.lang.SuppressWarnings("all")
    public void remove(final int lineIndex) {
        this.problem.remove(lineIndex);
    }

    @java.lang.SuppressWarnings("all")
    public void remove(final Line line) {
        this.problem.remove(line);
    }

    @java.lang.SuppressWarnings("all")
    public void replace(final Line newLine) {
        this.problem.replace(newLine);
    }

    @java.lang.SuppressWarnings("all")
    public void synchronize(DatabaseSynchronization subscriber) {
        this.problem.synchronize(subscriber);
    }

    @java.lang.SuppressWarnings("all")
    public void subscribeToAfterAdditions(final AfterAdditionSubscriber subscriber) {
        this.problem.subscribeToAfterAdditions(subscriber);
    }

    @java.lang.SuppressWarnings("all")
    public void subscribeToBeforeRemoval(final BeforeRemovalSubscriber subscriber) {
        this.problem.subscribeToBeforeRemoval(subscriber);
    }

    @java.lang.SuppressWarnings("all")
    public void subscribeToAfterRemoval(final BeforeRemovalSubscriber listener) {
        this.problem.subscribeToAfterRemoval(listener);
    }

    @java.lang.SuppressWarnings("all")
    public List<Attribute<Object>> headerView() {
        return this.problem.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return problem.headerView2();
    }

    @java.lang.SuppressWarnings("all")
    public <T extends Object> ColumnView<T> columnView(final Attribute<T> atribūts) {
        return this.problem.<T>columnView(atribūts);
    }

    @java.lang.SuppressWarnings("all")
    public List<Column<Object>> columnsView() {
        return this.problem.columnsView();
    }

    @Deprecated
    @java.lang.SuppressWarnings("all")
    public ListView<Line> rawLinesView() {
        return this.problem.rawLinesView();
    }

    @java.lang.SuppressWarnings("all")
    public boolean contains(final Line line) {
        return this.problem.contains(line);
    }

    @java.lang.SuppressWarnings("all")
    public List<Line> lines() {
        return this.problem.lines();
    }

    @java.lang.SuppressWarnings("all")
    public Line rawLine(final int index) {
        return this.problem.rawLine(index);
    }

    @java.lang.SuppressWarnings("all")
    public int size() {
        return this.problem.size();
    }

    @java.lang.SuppressWarnings("all")
    public boolean isEmpty() {
        return this.problem.isEmpty();
    }

    @java.lang.SuppressWarnings("all")
    public boolean hasContent() {
        return this.problem.hasContent();
    }

    @java.lang.SuppressWarnings("all")
    public List<Line> rawLines() {
        return this.problem.rawLines();
    }

    @java.lang.SuppressWarnings("all")
    public java.lang.String toCSV() {
        return this.problem.toCSV();
    }

    @java.lang.SuppressWarnings("all")
    public Line lookupEquals(final Attribute<Line> atribūts, final Line cits) {
        return this.problem.lookupEquals(atribūts, cits);
    }

    @java.lang.SuppressWarnings("all")
    public org.w3c.dom.Element toFods() {
        return this.problem.toFods();
    }

    @java.lang.SuppressWarnings("all")
    public List<String> path() {
        return this.problem.path();
    }

    @java.lang.SuppressWarnings("all")
    public org.w3c.dom.Node toDom() {
        return this.problem.toDom();
    }

    @Override
    public String toString() {
        return path().toString();
    }

    @Override
    public Object identity() {
        return this;
    }
}
