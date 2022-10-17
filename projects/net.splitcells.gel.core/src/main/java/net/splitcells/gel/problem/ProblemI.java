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
package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
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
import net.splitcells.gel.data.allocation.Allocations;
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
    private final Allocations allocations;
    protected Solution asSolution;

    public static Problem problem(Allocations allocations, Constraint constraint) {
        final var problem = new ProblemI(allocations, constraint);
        problem.synchronize(constraint);
        return problem;
    }

    protected ProblemI(Allocations allocations, Constraint constraint) {
        this.allocations = allocations;
        this.constraint = constraint;
    }

    @Override
    public Constraint constraint() {
        return constraint;
    }

    @Override
    public Allocations allocations() {
        return allocations;
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
        return derivedSolution(() -> list(), allocations, constraint, derivation);
    }

    @Override
    public Database supplies() {
        return this.allocations.supplies();
    }

    @Override
    public Database suppliesUsed() {
        return this.allocations.suppliesUsed();
    }

    @Override
    public Database suppliesFree() {
        return this.allocations.suppliesFree();
    }

    @Override
    public Database demands() {
        return this.allocations.demands();
    }

    @Override
    public Database demandsUsed() {
        return this.allocations.demandsUsed();
    }

    @Override
    public Database demandsFree() {
        return this.allocations.demandsFree();
    }

    @Override
    public Line allocate(final Line demand, final Line supply) {
        return this.allocations.allocate(demand, supply);
    }

    @Override
    public Line demandOfAllocation(final Line allocation) {
        return this.allocations.demandOfAllocation(allocation);
    }

    @Override
    public Line supplyOfAllocation(final Line allocation) {
        return this.allocations.supplyOfAllocation(allocation);
    }

    @Override
    public Set<Line> allocationsOfSupply(final Line supply) {
        return this.allocations.allocationsOfSupply(supply);
    }

    @Override
    public Set<Line> allocationsOf(final Line demand, final Line supply) {
        return this.allocations.allocationsOf(demand, supply);
    }

    @Override
    public Line allocationOf(LinePointer demand, LinePointer supply) {
        return allocations.allocationOf(demand, supply);
    }

    @Override
    public Set<Line> allocationsOfDemand(final Line demand) {
        return this.allocations.allocationsOfDemand(demand);
    }

    @Override
    public Set<Line> supply_of_demand(final Line demand) {
        return this.allocations.supply_of_demand(demand);
    }

    @Override
    public Line addTranslated(List<?> values) {
        return this.allocations.addTranslated(values);
    }

    @Override
    public Line add(final Line line) {
        return this.allocations.add(line);
    }

    @Deprecated
    @Override
    public void remove(final int allocationIndex) {
        this.allocations.remove(allocationIndex);
    }

    @Override
    public void remove(final Line line) {
        this.allocations.remove(line);
    }

    @Override
    public void replace(final Line newLine) {
        this.allocations.replace(newLine);
    }

    @Override
    public void synchronize(DatabaseSynchronization subscriber) {
        this.allocations.synchronize(subscriber);
    }

    @Override
    public void subscribeToAfterAdditions(final AfterAdditionSubscriber subscriber) {
        this.allocations.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(final BeforeRemovalSubscriber subscriber) {
        this.allocations.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(final BeforeRemovalSubscriber subscriber) {
        this.allocations.subscribeToAfterRemoval(subscriber);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return this.allocations.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return this.allocations.headerView2();
    }

    @Override
    public <T extends Object> ColumnView<T> columnView(final Attribute<T> attribute) {
        return this.allocations.<T>columnView(attribute);
    }

    @Override
    public List<Column<Object>> columnsView() {
        return this.allocations.columnsView();
    }

    @Deprecated
    public ListView<Line> rawLinesView() {
        return this.allocations.rawLinesView();
    }

    @Override
    public boolean contains(final Line line) {
        return this.allocations.contains(line);
    }

    @Override
    public List<Line> lines() {
        return this.allocations.lines();
    }

    @Override
    public Line rawLine(final int index) {
        return this.allocations.rawLine(index);
    }

    @Override
    public int size() {
        return this.allocations.size();
    }

    @Override
    public boolean isEmpty() {
        return this.allocations.isEmpty();
    }

    @Override
    public boolean hasContent() {
        return this.allocations.hasContent();
    }

    @Override
    public List<Line> rawLines() {
        return this.allocations.rawLines();
    }

    public String toCSV() {
        return this.allocations.toCSV();
    }

    @Override
    public Line lookupEquals(final Attribute<Line> attribute, final Line value) {
        return this.allocations.lookupEquals(attribute, value);
    }

    @Override
    public Element toFods() {
        return this.allocations.toFods();
    }

    @Override
    public List<String> path() {
        return this.allocations.path();
    }

    public Node toDom() {
        return this.allocations.toDom();
    }

    @Override
    public Object identity() {
        return this;
    }
}
