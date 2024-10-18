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
package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.ColumnView;

import static net.splitcells.dem.Dem.configValue;

public class DatabaseModificationCounterAspect implements Database {
    public static Database databaseModificationCounterAspect(Database database) {
        return new DatabaseModificationCounterAspect(database);
    }

    private final Database database;

    private DatabaseModificationCounterAspect(Database argDatabase) {
        database = argDatabase;
    }

    @Override
    public Line addTranslated(ListView<?> values) {
        configValue(DatabaseModificationCounter.class).count(this, 1 + values.size());
        return database.addTranslated(values);
    }

    @Override
    public Line add(Line line) {
        configValue(DatabaseModificationCounter.class).count(this, 1 + line.values().size());
        return database.add(line);
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        configValue(DatabaseModificationCounter.class).count(this, 1 + line.values().size());
        return database.addWithSameHeaderPrefix(line);
    }

    @Override
    public void remove(int lineIndex) {
        configValue(DatabaseModificationCounter.class).count(this, 1 + rawLine(lineIndex).values().size());
        database.remove(lineIndex);
    }

    @Override
    public void remove(Line line) {
        configValue(DatabaseModificationCounter.class).count(this, 1 + line.values().size());
        database.remove(line);
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        database.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        database.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        database.subscribeToAfterRemoval(subscriber);
    }

    @Override
    public String name() {
        return database.name();
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return database.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return database.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return database.columnView(attribute);
    }

    @Override
    public ListView<ColumnView<Object>> columnsView() {
        return database.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return database.rawLinesView();
    }

    @Override
    public int size() {
        return database.size();
    }

    @Override
    public List<Line> rawLines() {
        return database.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line values) {
        return database.lookupEquals(attribute, values);
    }

    @Override
    public Object identity() {
        return database.identity();
    }

    @Override
    public List<String> path() {
        return database.path();
    }
}
