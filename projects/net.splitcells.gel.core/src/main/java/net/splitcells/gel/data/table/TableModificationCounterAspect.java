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
package net.splitcells.gel.data.table;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.website.server.project.renderer.CsvRenderer;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class TableModificationCounterAspect implements Table {
    public static Table tableModificationCounterAspect(Table table) {
        return new TableModificationCounterAspect(table);
    }

    private final Table table;

    private TableModificationCounterAspect(Table argTable) {
        table = argTable;
        ObjectsRenderer.registerObject(new CsvRenderer() {
            @Override
            public String renderCsv() {
                final var counter = configValue(TableModificationCounter.class)
                        .counters()
                        .get(table.path());
                if (counter == null) {
                    logs().appendError(executionException(tree("Could not find counter")
                            .withProperty("database", table.path().toString())));
                    return "";
                }
                return "time,count\n" + counter.measurements()
                        .stream()
                        .map(m -> m.time() + "," + m.value() + "\n")
                        .reduce("", (a, b) -> a + b);
            }

            @Override
            public Optional<String> title() {
                return Optional.of(path().toString());
            }

            @Override
            public List<String> path() {
                return table.path().withAppended("database-modification-counter.csv");
            }
        });
    }

    @Override
    public Line addTranslated(List<Object> lineValues, int index) {
        configValue(TableModificationCounter.class).count(this, 1 + (long) lineValues.size());
        return table.addTranslated(lineValues, index);
    }

    @Override
    public Line addTranslated(ListView<?> values) {
        configValue(TableModificationCounter.class).count(this, 1 + (long) values.size());
        return table.addTranslated(values);
    }

    @Override
    public Line add(Line line) {
        configValue(TableModificationCounter.class).count(this, 1 + (long) line.values().size());
        return table.add(line);
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        configValue(TableModificationCounter.class).count(this, 1 + (long) line.values().size());
        return table.addWithSameHeaderPrefix(line);
    }

    @Override
    public void remove(int lineIndex) {
        configValue(TableModificationCounter.class).count(this, 1 + (long) rawLine(lineIndex).values().size());
        table.remove(lineIndex);
    }

    @Override
    public void remove(Line line) {
        configValue(TableModificationCounter.class).count(this, 1 + (long) line.values().size());
        table.remove(line);
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        table.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        table.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        table.subscribeToAfterRemoval(subscriber);
    }

    @Override
    public String name() {
        return table.name();
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return table.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return table.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return table.columnView(attribute);
    }

    @Override
    public ListView<ColumnView<Object>> columnsView() {
        return table.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return table.rawLinesView();
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public List<Line> rawLines() {
        return table.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line values) {
        return table.lookupEquals(attribute, values);
    }

    @Override
    public DiscoverableRenderer discoverableRenderer() {
        return table.discoverableRenderer();
    }

    @Override
    public Object identity() {
        return table.identity();
    }

    @Override
    public List<String> path() {
        return table.path();
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Table) {
            final var castedArg = (Table) arg;
            return castedArg.identity() == table.identity();
        }
        throw executionException("Invalid argument type: " + arg);
    }

    @Override
    public int hashCode() {
        return table.hashCode();
    }
}
