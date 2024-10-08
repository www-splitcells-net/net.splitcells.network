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
package net.splitcells.gel.data.lookup;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.ListViewI.listView;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.ConnectingConstructorI.connectingConstructor;
import static net.splitcells.dem.resource.communication.log.LogLevel.DEBUG;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.dem.utils.ExecutionException.executionException;

import java.util.function.Consumer;
import java.util.stream.Stream;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.dem.data.set.list.Lists;

/**
 * <p>Provides a view to a subset of a {@link Table} as a {@link Table}.
 * By default the {@link LookupTable} is empty.
 * {@link Line} has to be removed via {@link #register(Line)} and {@link #removeRegistration(Line)}.</p>
 * <p>TODO Test runtime improvements by {@link #USE_EXPERIMENTAL_RUNTIME_IMPROVEMENTS},
 * {@link #USE_EXPERIMENTAL_RAW_LINE_CACHE} and {@link #USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE}.</p>
 */
public class LookupTable implements Table {

    private static final boolean USE_EXPERIMENTAL_RUNTIME_IMPROVEMENTS = true;
    private static final boolean USE_EXPERIMENTAL_RAW_LINE_CACHE = true;
    private static final boolean USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE = true;
    private final Table tableView;
    private final String name;
    private final Set<Integer> content = setOfUniques();
    private final List<Column<Object>> columns;
    private final List<ColumnView<Object>> columnsView;
    private final List<Line> rawLinesCache = list();
    private final Map<Integer, Line> rawLinesHashedCache = map();

    private final boolean useExperimentalRawLineCache;

    public static LookupTableFactory lookupTableFactory() {
        return new LookupTableFactory() {
            private final ConnectingConstructor<LookupTable> connector = connectingConstructor();

            @Override
            public ConnectingConstructor<LookupTable> withConnector(Consumer<LookupTable> connector) {
                this.connector.withConnector(connector);
                return this;
            }

            @Override
            public LookupTable connect(LookupTable subject) {
                return connector.connect(subject);
            }

            @Override
            public LookupTable lookupTable(Table table, String name) {
                return connector.connect(LookupTable.lookupTable(table, name));
            }

            @Override
            public LookupTable lookupTable(Table table, Attribute<?> attribute) {
                return LookupTable.lookupTable(table, attribute);
            }

            @Override
            public LookupTable lookupTable(Table table, Attribute<?> attribute, boolean cacheRawLines) {
                return LookupTable.lookupTable(table, attribute, cacheRawLines);
            }
        };
    }

    private static LookupTable lookupTable(Table table, String name) {
        return new LookupTable(table, name, USE_EXPERIMENTAL_RAW_LINE_CACHE);
    }

    private static LookupTable lookupTable(Table table, Attribute<?> attribute) {
        return new LookupTable(table, attribute.name(), USE_EXPERIMENTAL_RAW_LINE_CACHE);
    }

    private static LookupTable lookupTable(Table table, Attribute<?> attribute, boolean cacheRawLines) {
        return new LookupTable(table, attribute.name(), cacheRawLines);
    }

    private LookupTable(Table table, String name) {
        this(table, name, USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE);
    }

    private LookupTable(Table table, String name, boolean useExperimentalRawLineCache) {
        this.tableView = table;
        this.name = name;
        columns = table.headerView().stream()
                .map(attribute -> LookupColumn.lookupColumn(this, attribute))
                .collect(toList());
        columnsView = Lists.<ColumnView<Object>>list();
        columnsView.addAll(columns);
        this.useExperimentalRawLineCache = useExperimentalRawLineCache;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return tableView.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return tableView.headerView2();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Column<T> columnView(Attribute<T> attribute) {
        int index = 0;
        for (final var headerAttribute : tableView.headerView()) {
            if (headerAttribute.equals(attribute)) {
                return (Column<T>) columns.get(index);
            }
            ++index;
        }
        throw new IllegalArgumentException(attribute.toString());
    }

    @Override
    public Line rawLine(int index) {
        if (useExperimentalRawLineCache) {
            return rawLinesCache.get(index);
        }
        if (USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE) {
            return rawLinesHashedCache.getOrDefault(index, null);
        }
        if (content.contains(index)) {
            return tableView.rawLine(index);
        }
        return null;
    }

    @Override
    public Stream<Line> unorderedLinesStream() {
        if (useExperimentalRawLineCache) {
            return rawLinesCache.stream().filter(e -> e != null);
        }
        if (USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE) {
            return rawLinesHashedCache.values().stream();
        }
        return content.stream().map(tableView::rawLine).filter(e -> e != null);
    }

    /**
     * The indexes needs to be preserved and therefore the null value gaps needs to be added as well.
     *
     * @return
     */
    @Override
    public ListView<Line> rawLinesView() {
        if (ENFORCING_UNIT_CONSISTENCY) {
            range(0, tableView.rawLinesView().size()).forEach(i -> {
                if (content.contains(i)) {
                    requireNotNull(tableView.rawLinesView().get(i));
                } else {
                    content.requireAbsenceOf(i);
                }
            });
        }
        if (useExperimentalRawLineCache) {
            return listView(rawLinesCache);
        }
        if (USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE) {
            return tableView.rawLinesView().stream().map(l -> {
                if (l == null) {
                    return null;
                }
                if (rawLinesHashedCache.containsKey(l.index())) {
                    return l;
                }
                return null;
            }).collect(toList());
        }
        final var rawLines = Lists.<Line>list();
        final var parentRawLines = tableView.rawLinesView();
        range(0, parentRawLines.size()).forEach(i -> {
            final Line rElement;
            if (content.contains(i)) {
                rElement = parentRawLines.get(i);
            } else {
                rElement = null;
            }
            rawLines.add(rElement);
        });
        return rawLines;
    }

    @Override
    public int size() {
        return content.size();
    }

    public void register(Line line) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            content.requireAbsenceOf(line.index());
        }
        if (TRACING) {
            logs().append(tree("register.LookupTable")
                            .withProperty("path", path().toString())
                            .withProperty("line", line.toTree())
                    , this
                    , DEBUG);
        }
        if (useExperimentalRawLineCache) {
            if (rawLinesCache.size() < line.index()) {
                rawLinesCache.prepareForSizeOf(line.index());
                if (USE_EXPERIMENTAL_RUNTIME_IMPROVEMENTS) {
                    final var limit = line.index() - rawLinesCache.size();
                    for (int i = 1; i <= limit; ++i) {
                        rawLinesCache.add(null);
                    }
                } else {
                    rangeClosed(1, line.index() - rawLinesCache.size()).forEach(i -> {
                        rawLinesCache.add(null);
                    });
                }
                rawLinesCache.add(line);
            } else if (rawLinesCache.size() == line.index()) {
                rawLinesCache.prepareForSizeOf(rawLinesCache.size() + 1);
                rawLinesCache.add(line);
            } else if (rawLinesCache.size() > line.index()) {
                rawLinesCache.set(line.index(), line);
            } else {
                throw executionException("Unaccounted indexes: rawLinesCache-size=" + rawLinesCache.size() + ", line-index=" + line.index());
            }
        }
        if (USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE) {
            rawLinesHashedCache.put(line.index(), line);
        }
        content.add(line.index());
        // TODO PERFORMANCE
        // TODO FIX
        final var header = tableView.headerView();
        range(0, columns.size()).forEach(i -> {
            // HACK
            final var column = columns.get(i);
            column.set(line.index(), line.value(header.get(i)));
        });
        columns.forEach(column -> column.registerAddition(line));
    }

    public void removeRegistration(Line line) {
        if (TRACING) {
            logs().append(tree("deregister." + getClass().getSimpleName())
                            .withProperty("subject", path().toString())
                            .withProperty("content", content.toString())
                            .withProperty("line", line.toTree())
                    , this, DEBUG);
        }
        if (ENFORCING_UNIT_CONSISTENCY) {
            content.requirePresenceOf(line.index());
        }
        columns.forEach(column -> column.registerBeforeRemoval(line));
        content.remove(line.index());
        range(0, columns.size()).forEach(i -> {
            // HACK
            final var column = columns.get(i);
            column.set(line.index(), null);
        });
        if (useExperimentalRawLineCache) {
            if (rawLinesCache.size() == line.index() + 1) {
                rawLinesCache.remove(line.index());
            } else {
                rawLinesCache.set(line.index(), null);
            }
        }
        if (USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE) {
            rawLinesHashedCache.remove(line.index());
        }
        if (TRACING) {
            logs().append(
                    tree("after.deregister." + getClass().getSimpleName()).withChildren(
                            tree("subject").withChild(tree(path().toString()))
                            , tree("content").withChild(tree(content.toString()))
                            , line.toTree())
                    , this, DEBUG);
        }
    }

    @Override
    public List<ColumnView<Object>> columnsView() {
        return columnsView;
    }

    public Table base() {
        return tableView;
    }

    @Override
    public List<String> path() {
        final var path = tableView.path();
        path.add(LookupTable.class.getSimpleName() + "(" + name + ")");
        return path;
    }

    @Override
    public Tree toTree() {
        final var rVal = tree(LookupTable.class.getSimpleName());
        // REMOVE
        rVal.withProperty("hashCode", "" + hashCode());
        rVal.withProperty("subject", path().toString());
        rVal.withProperty("content", content.toString());
        content.forEach(i -> rVal.withChild(rawLinesView().get(i).toTree()));
        if (ENFORCING_UNIT_CONSISTENCY) {
            content.forEach(i -> requireNotNull(rawLinesView().get(i)));
        }
        return rVal;
    }

    @Override
    public String toString() {
        return LookupTable.class.getSimpleName() + path().toString();
    }

    @Override
    public List<Line> rawLines() {
        if (useExperimentalRawLineCache) {
            return listWithValuesOf(rawLinesCache);
        }
        if (content.isEmpty()) {
            return list();
        }
        final var contentList = Lists.listWithValuesOf(content);
        contentList.sort(ASCENDING_INTEGERS);
        final var rawLines = Lists.<Line>list();
        range(0, contentList.requireLastValue() + 1).forEach(i -> rawLines.add(null));
        content.forEach(index -> rawLines.set(index, tableView.rawLine(index)));
        return rawLines;
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line values) {
        final var rBase = tableView.lookupEquals(attribute, values);
        if (content.contains(rBase.index())) {
            return rBase;
        }
        // TODO Fix interface. Instead of return null, an error should be thrown.
        return null;
    }

    @Override
    public Object identity() {
        return this;
    }

    @Override
    public Stream<Line> orderedLinesStream() {
        if (useExperimentalRawLineCache) {
            return rawLinesCache.stream().filter(e -> e != null);
        }
        final var sortedContent = content.stream().collect(toList());
        sortedContent.sort(ASCENDING_INTEGERS);
        return sortedContent.stream().map(tableView::rawLine).collect(toList()).stream();
    }
}
