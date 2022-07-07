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
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import org.w3c.dom.Element;
import net.splitcells.dem.data.set.list.Lists;

public class LookupTable implements Table {
    protected final Table tableView;
    protected final String name;
    protected final Set<Integer> content = setOfUniques();
    protected final List<Column<Object>> columns;
    protected final List<Column<Object>> columnsView;

    public static LookupTable lookupTable(Table table, String name) {
        return new LookupTable(table, name);
    }

    public static LookupTable lookupTable(Table table, Attribute<?> attribute) {
        return new LookupTable(table, attribute.name());
    }

    protected LookupTable(Table table, String name) {
        this.tableView = table;
        this.name = name;
        columns = listWithValuesOf
                (table.headerView().stream()
                        .map(attribute -> LookupColumn.lookupColumn(this, attribute))
                        .collect(toList()));
        columnsView = listWithValuesOf(columns);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return tableView.headerView();
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
    public Line getRawLine(int index) {
        if (content.contains(index)) {
            return tableView.getRawLine(index);
        }
        return null;
    }

    @Override
    public Stream<Line> linesStream() {
        return content.stream().map(tableView::getRawLine);
    }

    @Override
    public List<Line> rawLinesView() {
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
        content.add(line.index());
        // TODO PERFORMANCE
        // TODO FIX
        range(0, columns.size()).forEach(i -> {
            // HACK
            final var column = columns.get(i);
            column.set(line.index(), line.value(tableView.headerView().get(i)));
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
        // TODO PERFORMANCE
        final var rawLines = Lists.<Line>list();
        content.forEach(index -> rawLines.add(tableView.getRawLine(index)));
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
}
