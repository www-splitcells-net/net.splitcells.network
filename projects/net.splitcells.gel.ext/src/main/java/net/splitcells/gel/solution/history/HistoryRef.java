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
package net.splitcells.gel.solution.history;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.table.AfterAdditionSubscriber;
import net.splitcells.gel.data.table.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.LinePointer;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.gel.solution.Solution;

import java.util.function.Supplier;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.gel.solution.history.HistoryI.historyI;
import static org.assertj.core.api.Assertions.assertThat;

public class HistoryRef implements History {

    public static History historyRef(Solution solution) {
        return new HistoryRef(solution);
    }

    private final History history;

    private HistoryRef(Solution solution) {
        history = historyI(solution);
    }

    /**
     * "-1" stands for the initial state of the solution.
     * The target index should not be reverted itself.
     *
     * @param index
     */
    @Override
    public void resetTo(int index) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(history.size()).isGreaterThan(index);
            assertThat(index).isGreaterThanOrEqualTo(-1);
        }
        history.resetTo(index);
    }

    @Override
    public void processWithoutHistory(Runnable runnable) {
        history.processWithoutHistory(runnable);
    }

    @Override
    public void processWithHistory(Runnable runnable) {
        history.processWithHistory(runnable);
    }

    @Override
    public <T> T supplyWithHistory(Supplier<T> supplier) {
        return history.supplyWithHistory(supplier);
    }

    @Override
    public boolean isHistoryConsistent() {
        return history.isHistoryConsistent();
    }

    @Override
    public boolean isRegisterEventIsEnabled() {
        return history.isRegisterEventIsEnabled();
    }

    @Override
    public History withRegisterEventIsEnabled(boolean arg) {
        history.withRegisterEventIsEnabled(arg);
        return this;
    }

    @Override
    public History withLogNaturalArgumentation(boolean logNaturalArgumentation) {
        return history.withLogNaturalArgumentation(logNaturalArgumentation);
    }

    @Override
    public boolean logNaturalArgumentation() {
        return history.logNaturalArgumentation();
    }

    @Override
    public int currentIndex() {
        return history.currentIndex();
    }

    @Override
    public Object identity() {
        return history.identity();
    }

    @Override
    public List<String> path() {
        return history.path();
    }

    @Override
    public Line assign(Line demand, Line supply) {
        return history.assign(demand, supply);
    }

    @Override
    public Line anyAssignmentOf(LinePointer demand, LinePointer supply) {
        return history.anyAssignmentOf(demand, supply);
    }

    @Override
    public Table supplies() {
        return history.supplies();
    }

    @Override
    public Table suppliesUsed() {
        return history.suppliesUsed();
    }

    @Override
    public Table suppliesFree() {
        return history.suppliesFree();
    }

    @Override
    public Table demands() {
        return history.demands();
    }

    @Override
    public Table demandsUsed() {
        return history.demandsUsed();
    }

    @Override
    public Table demandsFree() {
        return history.demandsFree();
    }

    @Override
    public Line demandOfAssignment(Line allocation) {
        return history.demandOfAssignment(allocation);
    }

    @Override
    public Line supplyOfAssignment(Line allocation) {
        return history.supplyOfAssignment(allocation);
    }

    @Override
    public Set<Line> assignmentsOfSupply(Line supply) {
        return history.assignmentsOfSupply(supply);
    }

    @Override
    public Set<Line> assignmentsOfDemand(Line demand) {
        return history.assignmentsOfDemand(demand);
    }

    @Override
    public void registerAddition(Line line) {
        history.registerAddition(line);
    }

    @Override
    public void registerBeforeRemoval(Line line) {
        history.registerBeforeRemoval(line);
    }

    @Override
    public Line addTranslated(ListView<?> values) {
        return history.addTranslated(values);
    }

    @Override
    public Line add(Line line) {
        return history.add(line);
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        return history.addWithSameHeaderPrefix(line);
    }

    @Override
    public void remove(int lineIndex) {
        history.remove(lineIndex);
    }

    @Override
    public void remove(Line line) {
        history.remove(line);
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        history.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        history.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        history.subscribeToAfterRemoval(subscriber);
    }

    @Override
    public String name() {
        return history.name();
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return history.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return history.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return history.columnView(attribute);
    }

    @Override
    public ListView<ColumnView<Object>> columnsView() {
        return history.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return history.rawLinesView();
    }

    @Override
    public int size() {
        return history.size();
    }

    @Override
    public List<Line> rawLines() {
        return history.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line values) {
        return history.lookupEquals(attribute, values);
    }
}
