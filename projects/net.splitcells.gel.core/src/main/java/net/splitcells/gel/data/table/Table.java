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
package net.splitcells.gel.data.table;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_OFFICE;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TABLE;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TEXT;
import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.attribute.Attribute;

public interface Table extends Discoverable, Domable {
    /**
     * true is faster than false, according to a manual test run with a CPU profiler.
     */
    boolean GET_LINE_VIA_STREAM = false;

    List<Attribute<Object>> headerView();

    List<Attribute<? extends Object>> headerView2();

    <T> ColumnView<T> columnView(Attribute<T> atribūts);

    List<Column<Object>> columnsView();

    ListView<Line> rawLinesView();

    default boolean contains(Line line) {
        if (line.index() >= rawLinesView().size()) {
            return false;
        } else {
            return null != rawLinesView().get(line.index());
        }
    }

    default List<Line> lines() {
        return rawLinesView().stream()
                .filter(e -> e != null)
                .collect(Lists.toList());
    }

    /**
     * <p>This method exists for improved performance compared to calling
     * <code>lines().stream()</code> on certain {@link Table} implementations.
     * This is caused by the fact, that this method does not need a copy all {@link Line} references,
     * before constructing this {@link Stream}.</p>
     *
     * <p>Also, it is expected, that generally {@link #linesStream()} is the fastest method,
     * in order to iterate or list all {@link Line}.
     * Especially, it is expected to be generally faster, than {@link #rawLines()}.
     * Only in select cases {@link #rawLines()} is expected to be faster,
     * where knowledge of the underlining {@link Table} implementation is present.
     * The reason for this, is the fact, that it is faster to construct {@link #linesStream()} via {@link #rawLines()},
     * than the other way around.</p>
     *
     * @return An ordered {@linl Stream} of {@łink #lines}.
     */
    default Stream<Line> linesStream() {
        return lines().stream();
    }

    default List<Line> distinctLines() {
        return distinctLineValues().stream()
                .map(values -> lookupEquals(values).findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Lists.toList());
    }

    default Set<List<Object>> distinctLineValues() {
        return rawLinesView().stream()
                .filter(e -> e != null)
                .map(line -> headerView().stream().map(line::value).collect(Lists.toList()))
                .collect(toSetOfUniques());
    }

    /**
     * This is a helper method in order to retrieve a {@link Line} quickly.
     *
     * @param index This is the raw index of the {@link Line} to be retrieved.
     * @return This is the {@link Line} according to the {@param index} or null,
     * if the {@param index} is smaller than {@link #size()} and no {@link Line} is located there.
     */
    default Line rawLine(int index) {
        return rawLinesView().get(index);
    }

    /**
     * TODO RENAME
     *
     * @param index Index Of The Requested Line
     * @return Requested Line
     */
    default Line line(int index) {
        if (GET_LINE_VIA_STREAM) {
            if (index == 0) {
                return linesStream().findFirst().orElseThrow();
            } else {
                // TODO TOFIX This does not work in BacktrackingTest#testBranching.
                return linesStream().skip(index).findFirst().orElseThrow();
            }
        } else {
            return lines().get(index);
        }
    }

    int size();

    default boolean isEmpty() {
        return 0 == size();
    }

    default boolean hasContent() {
        return !isEmpty();
    }

    @Deprecated
    List<Line> rawLines();

    default String toCSV() {
        final var csv = new StringBuffer();
        final var header = headerView().stream()
                .map(atribūts -> atribūts.name())
                .collect(toList());
        try (final var printer = new CSVPrinter
                (csv, CSVFormat.RFC4180.withHeader(header.toArray(new String[header.size()])))) {
            lines().stream()
                    .map(line -> line.toStringList())
                    .forEach(line -> {
                        try {
                            printer.printRecord(line);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return csv.toString();
    }

    default <T> Table lookup(Attribute<T> attribute, T value) {
        return columnView(attribute).lookup(value);
    }

    /**
     * TODO RENAME
     */
    Line lookupEquals(Attribute<Line> attribute, Line values);

    default Stream<Line> lookupEquals(List<Object> values) {
        return lines().stream()
                .filter(line ->
                        IntStream.range(0, headerView().size())
                                .mapToObj(i -> Objects.equals(values.get(i), line.value(headerView().get(i))))
                                .reduce(true, (a, b) -> a && b));
    }

    default Element toFods() {
        final var fods = rElement(FODS_OFFICE, "document");
        final var body = elementWithChildren(FODS_OFFICE, "body");
        fods.setAttributeNode
                (attribute(FODS_OFFICE, "mimetype", "application/vnd.oasis.opendocument.spreadsheet"));
        fods.appendChild(body);
        {
            final var spreadsheet = elementWithChildren(FODS_OFFICE, "spreadsheet");
            body.appendChild(spreadsheet);
            final var table = rElement(FODS_TABLE, "table");
            spreadsheet.appendChild(table);
            table.setAttributeNode(attribute(FODS_TABLE, "name", "values"));
            {
                final var header = elementWithChildren(FODS_TABLE, "table-row");
                table.appendChild(header);
                headerView().stream()
                        .map(att -> att.name())
                        .map(attName -> {
                            final var tabulasElements = elementWithChildren(FODS_TABLE, "table-cell");
                            final var tabulasVertība = rElement(FODS_TEXT, "p");
                            tabulasElements.appendChild(tabulasVertība);
                            tabulasVertība.appendChild(textNode(attName));
                            return tabulasElements;
                        }).forEach(attDesc -> header.appendChild(attDesc));
                lines().forEach(line -> {
                    final var tableLine = elementWithChildren(FODS_TABLE, "table-row");
                    table.appendChild(tableLine);
                    headerView().stream()
                            .map(attribute -> line.value(attribute))
                            .map(value -> {
                                final var cellElement = elementWithChildren(FODS_TABLE, "table-cell");
                                final var cellValue = rElement(FODS_TEXT, "p");
                                cellElement.appendChild(cellValue);
                                if (value instanceof Domable) {
                                    final var domValue = ((Domable) value).toDom();
                                    cellValue.appendChild
                                            (textNode(toPrettyString(domValue)));
                                } else {
                                    cellValue.appendChild(textNode(value.toString()));
                                }
                                return cellElement;
                            }).forEach(tableElement -> tableLine.appendChild(tableElement));
                });
            }
        }
        return fods;
    }

    static boolean referToSameData(Table a, Table b) {
        return a.path().equals(b.path());
    }
}
