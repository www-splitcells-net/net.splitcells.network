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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;

import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * Makes a given {@link Table} thread safe.
 */
public class TableSynchronizationAspect implements Table {
    public static Table tableSynchronizationAspect(Table content) {
        return new TableSynchronizationAspect(content);
    }

    private final Table content;

    private TableSynchronizationAspect(Table argContent) {
        content = argContent;
    }

    @Override
    public Line addTranslated(ListView<Object> lineValues, int index) {
        return content.addTranslated(lineValues, index);
    }

    @Override
    public synchronized Line addTranslated(ListView<?> values) {
        return content.addTranslated(values);
    }

    @Override
    public synchronized Line add(Line line) {
        return content.add(line);
    }

    @Override
    public synchronized Line addWithSameHeaderPrefix(Line line) {
        return content.addWithSameHeaderPrefix(line);
    }

    @Override
    public synchronized Query query() {
        throw notImplementedYet();
    }

    @Override
    public synchronized void remove(int lineIndex) {
        content.remove(lineIndex);
    }

    @Override
    public synchronized void remove(Line line) {
        content.remove(line);
    }

    @Override
    public synchronized void replace(Line newLine) {
        content.replace(newLine);
    }

    @Override
    public synchronized void synchronize(TableSynchronization subscriber) {
        content.synchronize(subscriber);
    }

    @Override
    public synchronized void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        content.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public synchronized void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        content.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public synchronized void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        content.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public synchronized Tree toTree() {
        return content.toTree();
    }

    @Override
    public synchronized Table withAddSimplifiedCsv(String csvFile) {
        content.withAddSimplifiedCsv(csvFile);
        return this;
    }

    @Override
    public synchronized String name() {
        return content.name();
    }

    @Override
    public synchronized List<Attribute<Object>> headerView() {
        return content.headerView();
    }

    @Override
    public synchronized List<Attribute<? extends Object>> headerView2() {
        return content.headerView2();
    }

    @Override
    public synchronized <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return content.columnView(attribute);
    }

    @Override
    public synchronized ListView<ColumnView<Object>> columnsView() {
        return content.columnsView();
    }

    @Override
    public synchronized ListView<Line> rawLinesView() {
        return content.rawLinesView();
    }

    @Override
    public synchronized boolean contains(Line line) {
        return content.contains(line);
    }

    @Override
    public synchronized List<Line> orderedLines() {
        return content.orderedLines();
    }

    @Override
    public synchronized List<Line> unorderedLines() {
        return content.unorderedLines();
    }

    @Override
    public synchronized Stream<Line> unorderedLinesStream() {
        return content.unorderedLinesStream();
    }

    @Override
    public synchronized Stream<Line> orderedLinesStream() {
        return content.orderedLinesStream();
    }

    @Override
    public synchronized List<Line> distinctLines() {
        return content.distinctLines();
    }

    @Override
    public synchronized Set<List<Object>> distinctLineValues() {
        return content.distinctLineValues();
    }

    @Override
    public synchronized Line rawLine(int index) {
        return content.rawLine(index);
    }

    @Override
    public synchronized Line orderedLine(int n) {
        return content.orderedLine(n);
    }

    @Override
    public synchronized int size() {
        return content.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return content.isEmpty();
    }

    @Override
    public synchronized boolean isPresent() {
        return content.isPresent();
    }

    @Override
    public synchronized boolean hasContent() {
        return content.hasContent();
    }

    @Override
    public synchronized List<Line> rawLines() {
        return content.rawLines();
    }

    @Override
    public synchronized String toCSV() {
        return content.toCSV();
    }

    @Override
    public synchronized String toSimplifiedCSV() {
        return content.toSimplifiedCSV();
    }

    @Override
    public synchronized <T> View persistedLookup(Attribute<T> attribute, T value) {
        return content.persistedLookup(attribute, value);
    }

    @Override
    public synchronized Line lookupEquals(Attribute<Line> attribute, Line values) {
        return content.lookupEquals(attribute, values);
    }

    @Override
    public synchronized Stream<Line> lookupEquals(ListView<Object> values) {
        return content.lookupEquals(values);
    }

    @Override
    public synchronized Tree toHtmlTable() {
        return content.toHtmlTable();
    }

    @Override
    public synchronized Tree toFods() {
        return content.toFods();
    }

    @Override
    public synchronized Attribute<? extends Object> attributeByName(String name) {
        return content.attributeByName(name);
    }

    @Override
    public synchronized List<List<String>> toReformattedTable(List<Attribute<?>> columnAttributes, List<Attribute<?>> rowAttributes) {
        return content.toReformattedTable(columnAttributes, rowAttributes);
    }

    @Override
    public synchronized boolean isEqualFormat(Table otherTable) {
        return content.isEqualFormat(otherTable);
    }

    @Override
    public synchronized DiscoverableRenderer discoverableRenderer() {
        return content.discoverableRenderer();
    }

    @Override
    public synchronized Object identity() {
        return content.identity();
    }

    @Override
    public synchronized List<String> path() {
        return content.path();
    }

    @Override
    public synchronized Discoverable child(List<String> extension) {
        return content.child(extension);
    }

    @Override
    public synchronized boolean equals(Object arg) {
        if (arg instanceof Table table) {
            return table.identity() == content.identity();
        }
        throw ExecutionException.execException("Invalid argument type: " + arg);
    }

    @Override
    public synchronized int hashCode() {
        return content.hashCode();
    }
}
