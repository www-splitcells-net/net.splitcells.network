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
package net.splitcells.gel.data.database.history;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.solution.history.History;
import org.w3c.dom.Node;

import java.util.function.Supplier;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.common.Language.DATABASE_HISTORY;
import static net.splitcells.gel.common.Language.EVENTS;
import static net.splitcells.gel.common.Language.HISTORIC_VALUES;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.data.database.history.DatabaseEventType.ADDITION;
import static net.splitcells.gel.data.database.history.DatabaseEventType.REMOVAL;

/**
 * TODO This is an experimental implementation of {@link History} for {@link Database} instead of {@link Assignments}.
 * This is probably not working yet.
 */
public class HistoryForDatabase implements History {

    public static final Attribute<DatabaseEventType> DATABASE_EVENT_TYPE = attribute(DatabaseEventType.class, "database-event-type");
    public static final Attribute<Integer> LINE_INDEX = attribute(Integer.class, "line-index");

    public static HistoryForDatabase historyForDatabase(Database database) {
        return new HistoryForDatabase(database);
    }

    private static final String ERROR_HISTORY_DISABLED = "History is disabled.";
    private static final String ERROR_HISTORY_INCONSISTENT = "History is inconsistent.";

    private final Database database;
    private Assignments history;

    private HistoryForDatabase(Database database) {
        history = assignments(DATABASE_HISTORY.value(),
                database(HISTORIC_VALUES.value(), database, database.headerView2())
                , database(EVENTS.value(), database, list(LINE_INDEX, DATABASE_EVENT_TYPE)));
        this.database = database;
        database.subscribeToAfterAdditions(this);
        database.subscribeToBeforeRemoval(this);
    }

    @Override
    public void registerAddition(Line addition) {
        final var addedLine = history.demands().addTranslated(addition.values());
        final var eventType = history.supplies().addTranslated(list(addition.index(), ADDITION));
        history.assign(addedLine, eventType);
    }

    @Override
    public void registerBeforeRemoval(Line removal) {
        final var removedLine = history.demands().addTranslated(removal.values());
        final var eventType = history.supplies().addTranslated(list(removal.index(), REMOVAL));
        history.assign(removedLine, eventType);
    }

    @Override
    public void resetTo(int index) {
        throw notImplementedYet();
    }

    @Override
    public void processWithoutHistory(Runnable runnable) {
        throw notImplementedYet();
    }

    @Override
    public void processWithHistory(Runnable runnable) {
        throw notImplementedYet();
    }

    @Override
    public <T> T supplyWithHistory(Supplier<T> supplier) {
        throw notImplementedYet();
    }

    @Override
    public void remove(Line line) {
        throw notImplementedYet();
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
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber beforeRemovalSubscriber) {
        history.subscribeToAfterRemoval(beforeRemovalSubscriber);
    }

    @Override
    public Line addTranslated(List<?> values) {
        throw notImplementedYet();
    }

    @Override
    public Line add(Line line) {
        throw notImplementedYet();
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        throw notImplementedYet();
    }

    @Override
    public void remove(int indexes) {
        throw notImplementedYet();
    }

    @Override
    public int currentIndex() {
        return history.size() - 1;
    }

    @Override
    public Line assign(Line demand, Line supply) {
        throw notImplementedYet();
    }

    @Override
    public Line anyAssignmentOf(LinePointer demand, LinePointer supply) {
        throw notImplementedYet();
    }

    @Override
    public Database supplies() {
        throw notImplementedYet();
    }

    @Override
    public Database suppliesUsed() {
        throw notImplementedYet();
    }

    @Override
    public Database suppliesFree() {
        throw notImplementedYet();
    }

    @Override
    public Database demands() {
        throw notImplementedYet();
    }

    @Override
    public Database demandsUsed() {
        throw notImplementedYet();
    }

    @Override
    public Database demandsFree() {
        throw notImplementedYet();
    }

    @Override
    public Line demandOfAssignment(Line allocation) {
        throw notImplementedYet();
    }

    @Override
    public Line supplyOfAssignment(Line allocation) {
        throw notImplementedYet();
    }

    @Override
    public Set<Line> assignmentsOfSupply(Line supply) {
        throw notImplementedYet();
    }

    @Override
    public Set<Line> assignmentsOfDemand(Line demand) {
        throw notImplementedYet();
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
    public ListView<Column<Object>> columnsView() {
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
    public Line lookupEquals(Attribute<Line> attribute, Line other) {
        return history.lookupEquals(attribute, other);
    }

    @Override
    public Node toDom() {
        return history.toDom();
    }

    @Override
    public List<String> path() {
        return history.path();
    }

    public boolean isHistoryConsistent() {
        throw notImplementedYet();
    }

    @Override
    public boolean isRegisterEventIsEnabled() {
        throw notImplementedYet();
    }

    @Override
    public History withRegisterEventIsEnabled(boolean arg) {
        throw notImplementedYet();
    }

    @Override
    public Object identity() {
        return this;
    }

    @Override
    public boolean logNaturalArgumentation() {
        throw notImplementedYet();
    }

    @Override
    public History withLogNaturalArgumentation(boolean newValue) {
        throw notImplementedYet();
    }
}
