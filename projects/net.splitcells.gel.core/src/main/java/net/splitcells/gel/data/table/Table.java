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
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_OFFICE;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TABLE;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TEXT;
import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.attribute.Attribute;

public interface Table extends Discoverable, Domable {
    List<Attribute<Object>> headerView();

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

    default List<Line> getLines() {
        return listWithValuesOf
                (rawLinesView().stream()
                        .filter(e -> e != null)
                        .collect(Collectors.toList()));
    }

    default Line getRawLine(int index) {
        return rawLinesView().get(index);
    }

    default Line getLines(int index) {
        return getLines().get(index);
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
            getLines().stream()
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

    /**
     * TODO RENAME
     */
    Line lookupEquals(Attribute<Line> attribute, Line values);

    default Stream<Line> lookupEquals(List<Object> values) {
        return getLines().stream()
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
                getLines().forEach(line -> {
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
}
