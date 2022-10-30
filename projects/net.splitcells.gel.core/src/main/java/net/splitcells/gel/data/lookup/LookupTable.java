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
package net.splitcells.gel.data.lookup;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.ListViewI.listView;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Stream;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import org.w3c.dom.Element;
import net.splitcells.dem.data.set.list.Lists;

/**
 * TODO Test runtime improvements by {@link #USE_EXPERIMENTAL_RUNTIME_IMPROVEMENTS},
 * {@link #USE_EXPERIMENTAL_RAW_LINE_CACHE} and {@link #USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE}.
 */
public class LookupTable implements Table {

    private static final boolean USE_EXPERIMENTAL_RUNTIME_IMPROVEMENTS = true;
    private static final boolean USE_EXPERIMENTAL_RAW_LINE_CACHE = true;
    private static final boolean USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE = true;
    protected final Table tableView;
    protected final String name;
    protected final Set<Integer> content = setOfUniques();
    protected final List<Column<Object>> columns;
    protected final List<Column<Object>> columnsView;
    private final List<Line> rawLinesCache = list();
    private final Map<Integer, Line> rawLinesHashedCache = map();

    private final boolean useExperimentalRawLineCache;

    /**
     * @param table The {@link Table} on which the lookup will be performed.
     * @param name This is the name of the {@link LookupTable} being constructed.
     * @return An instance, where no {@link Line} of {@link Table} is {@link #register(Line)}.
     */
    public static LookupTable lookupTable(Table table, String name) {
        return new LookupTable(table, name, USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE);
    }

    /**
     * @param table The {@link Table} on which the lookup will be performed.
     * @param attribute The {@link Attribute}, that will be looked up.
     * @return An instance, where no {@link Line} of {@link Table} is {@link #register(Line)}.
     */
    public static LookupTable lookupTable(Table table, Attribute<?> attribute) {
        return new LookupTable(table, attribute.name(), USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE);
    }

    public static LookupTable lookupTable(Table table, Attribute<?> attribute, boolean cacheRawLines) {
        return new LookupTable(table, attribute.name(), cacheRawLines);
    }

    protected LookupTable(Table table, String name) {
        this(table, name, USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE);
    }

    protected LookupTable(Table table, String name, boolean useExperimentalRawLineCache) {
        this.tableView = table;
        this.name = name;
        columns = table.headerView().stream()
                        .map(attribute -> LookupColumn.lookupColumn(this, attribute))
                        .collect(Lists.toList());
        columnsView = listWithValuesOf(columns);
        this.useExperimentalRawLineCache = useExperimentalRawLineCache;
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
    public Stream<Line> linesStream() {
        if (useExperimentalRawLineCache) {
            return rawLinesCache.stream().filter(e -> e != null);
        }
        if (USE_EXPERIMENTAL_RAW_LINE_HASHED_CACHE) {
            return rawLinesHashedCache.values().stream();
        }
        return content.stream().map(tableView::rawLine).filter(e -> e != null);
    }

    @Override
    public ListView<Line> rawLinesView() {
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
            }).collect(Lists.toList());
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
    }

    @Override
    public List<Column<Object>> columnsView() {
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
    public Element toDom() {
        final var rVal = Xml.elementWithChildren(LookupTable.class.getSimpleName());
        // REMOVE
        rVal.appendChild(textNode("" + hashCode()));
        rVal.appendChild(Xml.elementWithChildren("subject", textNode(path().toString())));
        rVal.appendChild(Xml.elementWithChildren("content", textNode(content.toString())));
        content.forEach(i -> rVal.appendChild(rawLinesView().get(i).toDom()));
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
        final var rawLines = Lists.<Line>list();
        content.forEach(index -> rawLines.add(tableView.rawLine(index)));
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
}
