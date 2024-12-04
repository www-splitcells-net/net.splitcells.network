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
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;

import java.util.stream.Stream;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.atom.DescribedBool.describedBool;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.resource.communication.log.LogLevel.DEBUG;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.dem.testing.Assertions.requireNull;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.common.Language.LINE;
import static net.splitcells.gel.common.Language.PATH_ACCESS_SYMBOL;
import static net.splitcells.gel.common.Language.REMOVE;

/**
 * <p>This aspect adds mainly logging and runtime check functionality to {@link Table} instances.</p>
 * <p>TODO Make this an aspect in order to make it usable for other implementations of {@link Table}.</p>
 * <p>TODO Require the usage of a non empty name during construction.</p>
 * <p>TODO Invalidate Lines pointing to an index where values are already replaced.</p>
 * <p>TODO PERFORMANCE Abstract Database implementation with generic storage in order to
 * simplify implementation and maintenance row and column based Databases.</p>
 * <p>TODO IDEA Implement Java collection interface.</p>
 */
public class TableMetaAspect implements Table {

    public static Table databaseIRef(Table table) {
        return new TableMetaAspect(table);
    }

    final Table table;

    private TableMetaAspect(Table table) {
        this.table = table;
    }

    /**
     * <p>TODO PERFORMANCE No copies have to be created, as it is guaranteed that a Line
     * does not change its content during its life cycle. This is important for
     * constraints.</p>
     * <p>TODO Test whether the line is added to the correct place.</p>
     * <p>TODO FIX Why does List&lt;?&gt; not work?</p>
     * <p>TODO PERFORMANCE Reduce the high number of copies.</p>
     *
     * @param line
     * @return
     */
    @Override
    public Line add(Line line) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            table.headerView().requireSizeOf(line.context().headerView().size());
            table.unorderedLines().requireComplianceByEveryElementWith(e -> !line.equalsTo(e));
            describedBool(line.index() >= table.rawLines().size()
                            || table.rawLines().get(line.index()) == null
                    , () -> "path: " + path().toString() + ", line.index(): " + line.index())
                    .required();
            range(0, table.headerView().size()).forEach(i -> {
                requireEquals(table.headerView().get(i), line.context().headerView().get(i));
            });
        }
        return table.add(line);
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        return table.addWithSameHeaderPrefix(line);
    }

    @Override
    public void remove(int lineIndex) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            if (table.rawLinesView().size() <= lineIndex) {
                throw executionException(tree("Cannot remove line by index, because the index is bigger than the biggest index in the database.")
                        .withText("lineIndex = " + lineIndex)
                        .withText("database = " + table.path())
                        .withText("database.size() = " + table.size()));
            }
            if (table.rawLinesView().get(lineIndex) == null) {
                throw executionException(tree("Cannot remove line by index, because this line was already removed.")
                        .withText("lineIndex = " + lineIndex)
                        .withText("database = " + table.path()));
            }
        }
        table.remove(lineIndex);
    }

    @Override
    public Line addTranslated(ListView<Object> lineValues, int index) {
        return table.addTranslated(lineValues, index);
    }

    /**
     * TODO REMOVE Code duplication of {@link TableMetaAspect#addTranslated} methods.
     */
    @Override
    public Line addTranslated(ListView<? extends Object> lineValues) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            /**
             * TODO Check for {@link Attribute} compatibility and not Class compatibility.
             */
            lineValues.requireSizeOf(table.headerView().size());
            lineValues.stream().forEach(e ->
                    requireNotNull(e, "The line " + lineValues + " should not contain nulls."));
            range(0, lineValues.size()).forEach(i -> table.headerView().get(i).isInstanceOf(lineValues.get(i)).required());
        }
        final var translatedAddition = table.addTranslated(lineValues);
        if (TRACING) {
            logs().append(tree("addTranslating." + Table.class.getSimpleName())
                            .withProperty("path", path().toString())
                            .withProperty("index", "" + translatedAddition.index())
                            .withProperty("line-values", lineValues.toString())
                    , this, DEBUG
            );
        }
        return translatedAddition;
    }

    @Override
    public void remove(Line line) {
        if (TRACING) {
            logs().append(tree(REMOVE.value()
                            + PATH_ACCESS_SYMBOL.value()
                            + Table.class.getSimpleName())
                            .withProperty("path", path().toString())
                            .withProperty(LINE.value(), line.toTree())
                    , this, LogLevel.DEBUG);
        }
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
    public Tree toTree() {
        return table.toTree();
    }

    @Override
    public List<String> path() {
        return table.path();
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
    public Object identity() {
        return table.identity();
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Table) {
            final var castedArg = (Table) arg;
            return identity().equals(castedArg.identity());
        }
        throw executionException("Invalid argument type: " + arg);
    }

    @Override
    public int hashCode() {
        return table.hashCode();
    }

    @Override
    public List<Line> orderedLines() {
        return table.orderedLines();
    }

    @Override
    public Stream<Line> unorderedLinesStream() {
        return table.unorderedLinesStream();
    }

    @Override
    public boolean contains(Line line) {
        return table.contains(line);
    }

    @Override
    public List<Line> unorderedLines() {
        return table.unorderedLines();
    }

    @Override
    public Stream<Line> orderedLinesStream() {
        return table.orderedLinesStream();
    }

    @Override
    public Set<List<Object>> distinctLineValues() {
        return table.distinctLineValues();
    }

    @Override
    public Line rawLine(int index) {
        return table.rawLine(index);
    }

    @Override
    public Line orderedLine(int n) {
        return table.orderedLine(n);
    }

    @Override
    public String toCSV() {
        return table.toCSV();
    }

    @Override
    public <T> View persistedLookup(Attribute<T> attribute, T value) {
        return table.persistedLookup(attribute, value);
    }

    @Override
    public Stream<Line> lookupEquals(ListView<Object> values) {
        return table.lookupEquals(values);
    }

    @Override
    public Tree toHtmlTable() {
        return table.toHtmlTable();
    }

    @Override
    public Tree toFods() {
        return table.toFods();
    }

    @Override
    public DiscoverableRenderer discoverableRenderer() {
        return table.discoverableRenderer();
    }
}
