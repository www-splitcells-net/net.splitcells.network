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

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.CsvDocument.csvDocument;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_OFFICE;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TABLE;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TEXT;
import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.lang.namespace.NameSpaces.HTML;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.MathUtils.floorToInt;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

import java.util.Optional;
import java.util.stream.Stream;

import net.splitcells.dem.data.Identifiable;
import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.column.ColumnView;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.attribute.Attribute;

public interface Table extends Discoverable, Domable, Identifiable {
    boolean GET_LINE_VIA_STREAM = true;

    String name();

    List<Attribute<Object>> headerView();

    List<Attribute<? extends Object>> headerView2();

    <T> ColumnView<T> columnView(Attribute<T> attribute);

    ListView<ColumnView<Object>> columnsView();

    /**
     * @return List containing every {@link Line}, where {@link List#indexOf(Object)} matches {@link Line#index()}.
     * If there are indexes without a matching {@link Line#index()},
     * the location in the {@link List} has the element null.
     */
    ListView<Line> rawLinesView();

    default boolean contains(Line line) {
        if (line.index() >= rawLinesView().size()) {
            return false;
        } else {
            return null != rawLinesView().get(line.index());
        }
    }

    /**
     * @return {@link List} of {@link Line}, that is ordered by {@link Line#index()}.
     */
    default List<Line> orderedLines() {
        throw notImplementedYet(this.getClass().getName() + " does not implemented `orderedLines`.");
    }

    /**
     * @return List of lines in any order.
     */
    default List<Line> unorderedLines() {
        return rawLinesView().stream()
                .filter(e -> e != null)
                .collect(toList());
    }

    /**
     * <p>This method exists for improved performance compared to calling
     * <code>lines().stream()</code> on certain {@link Table} implementations.
     * This is caused by the fact, that this method does not need a copy all {@link Line} references,
     * before constructing this {@link Stream}.</p>
     *
     * <p>Also, it is expected, that generally {@link #unorderedLinesStream()} is the fastest method,
     * in order to iterate or list all {@link Line}.
     * Especially, it is expected to be generally faster, than {@link #rawLines()}.
     * Only in select cases {@link #rawLines()} is expected to be faster,
     * where knowledge of the underlining {@link Table} implementation is present.
     * The reason for this, is the fact, that it is faster to construct {@link #unorderedLinesStream()}
     * via {@link #rawLines()}, than the other way around.</p>
     * <p>There is no guarantee regarding the ordering.</p>
     *
     * @return An ordered {@link Stream} of {@link #orderedLines()}.
     */
    default Stream<Line> unorderedLinesStream() {
        return unorderedLines().stream();
    }

    /**
     * @return {@link Stream} of {@link Line} ordered by {@link Line#index()}.
     */
    default Stream<Line> orderedLinesStream() {
        return orderedLines().stream();
    }

    /**
     * @return {@link Line} With Distinct Values
     * @see Database#query()
     */
    @Deprecated
    default List<Line> distinctLines() {
        return distinctLineValues().stream()
                .map(values -> lookupEquals(values).findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    default Set<List<Object>> distinctLineValues() {
        return rawLinesView().stream()
                .filter(e -> e != null)
                .map(line -> headerView().stream().map(line::value).collect(toList()))
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
     * <p>Retrieves the N-th {@link Line} of this {@link Table},
     * where null {@link Line}s are ignored.
     * Therefore, N is not necessarily equals the returned {@link Line#index()}.</p>
     *
     * @param n Position Of The Requested Line indexed by ignoring nulls
     * @return Requested Line
     */
    default Line orderedLine(int n) {
        if (GET_LINE_VIA_STREAM) {
            if (n == 0) {
                return orderedLinesStream().findFirst().orElseThrow();
            } else {
                return orderedLinesStream().skip(n).findFirst().orElseThrow();
            }
        } else {
            return unorderedLines().get(n);
        }
    }

    int size();

    default boolean isEmpty() {
        return 0 == size();
    }

    default boolean isPresent() {
        return 0 != size();
    }

    default boolean hasContent() {
        return !isEmpty();
    }

    /**
     * This method is deprecated, as write access to this list is not wanted.
     * See {@link #rawLinesView()}.
     *
     * @return raw lines
     */
    @Deprecated
    List<Line> rawLines();

    default String toCSV() {
        final var header = headerView().stream()
                .map(attribute -> attribute.name())
                .collect(toList());
        try (final var printer = csvDocument(header)) {
            unorderedLines().stream()
                    .map(line -> line.toStringList())
                    .forEach(line -> {
                        try {
                            printer.addLine(line);
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    });
            return printer.toString();
        } catch (Throwable e) {
            throw new RuntimeException();
        }
    }

    default String toSimplifiedCSV() {
        final var simplifiedCsv = new StringBuilder();
        simplifiedCsv.append(headerView().stream()
                .map(Attribute::name)
                .reduce("", StringUtils::mergeSimplifiedCsvList)
                + "\n");
        unorderedLines().forEach(line -> simplifiedCsv.append(line.values().stream()
                .map(Object::toString)
                .reduce("", StringUtils::mergeSimplifiedCsvList)
                + "\n"));
        return simplifiedCsv.toString();
    }

    default <T> Table lookup(Attribute<T> attribute, T value) {
        return columnView(attribute).lookup(value);
    }

    /**
     * TODO RENAME
     */
    Line lookupEquals(Attribute<Line> attribute, Line values);

    default Stream<Line> lookupEquals(ListView<Object> values) {
        return unorderedLines().stream()
                .filter(line -> range(0, headerView().size())
                        .mapToObj(i -> Thing.equals(values.get(i), line.value(headerView().get(i))))
                        .reduce(true, (a, b) -> a && b));
    }

    default Perspective toHtmlTable() {
        final var htmlTable = perspective("table", HTML);
        final var header = perspective("tr", HTML);
        header.withChild(perspective("th", HTML).withText("index"));
        headerView().forEach(attribute -> header.withChild(perspective("th", HTML).withText(attribute.name())));
        htmlTable.withChild(header);
        unorderedLines().forEach(line -> {
            final var row = perspective("tr", HTML);
            row.withChild(perspective("td", HTML).withText(line.index() + ""));
            headerView().forEach(attribute -> row.withChild(perspective("td", HTML)
                    .withText(line.value(attribute).toString())));
            htmlTable.withChild(row);
        });
        return htmlTable;
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
                            final var tableElements = elementWithChildren(FODS_TABLE, "table-cell");
                            final var tableValue = rElement(FODS_TEXT, "p");
                            tableElements.appendChild(tableValue);
                            tableValue.appendChild(textNode(attName));
                            return tableElements;
                        }).forEach(attDesc -> header.appendChild(attDesc));
                unorderedLines().forEach(line -> {
                    final var tableLine = elementWithChildren(FODS_TABLE, "table-row");
                    table.appendChild(tableLine);
                    headerView().stream()
                            .map(attribute -> line.value(attribute))
                            .map(value -> {
                                final var cellElement = elementWithChildren(FODS_TABLE, "table-cell");
                                final var cellValue = rElement(FODS_TEXT, "p");
                                cellElement.appendChild(cellValue);
                                if (value instanceof Domable) {
                                    cellValue.appendChild
                                            (textNode(((Domable) value).toPerspective().toXmlString()));
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

    default Attribute<? extends Object> attributeByName(String name) {
        return headerView().stream()
                .filter(da -> da.name().equals(name))
                .findFirst()
                .orElseThrow();
    }

    /**
     * <p>Normally a {@link Table} is drawn just like {@link #orderedLines()} is containing its data.
     * Also, the program works in this format, this is not always the best way to visualize a {@link Table}.</p>
     * <p>Draws this as {@link Table} in such a way,
     * that the columns and rows correspond to the given {@link Attribute} values.</p>
     * <p>TODO HACK Currently only one column attributes is working.</p>
     * <p>TODO The moment this method is extend the following has to be done,
     * as otherwise the implementation cost piles up too much.
     * This should be moved into its own class, in order to better document the variables inside this method.
     * This also makes it possible to test sub components more easily.</p>
     *
     * @param columnAttributes
     * @param rowAttributes
     * @return This is a 2 dimensional matrix. The first dimension is the column index and
     * the second dimension is the row index.
     */
    default List<List<String>> toReformattedTable(List<Attribute<? extends Object>> columnAttributes
            , List<Attribute<? extends Object>> rowAttributes) {
        if (orderedLines().isEmpty()) {
            return list();
        }
        final Map<Attribute<? extends Object>, List<String>> sortedAttributeValues = map();
        final var firstColumn = columnAttributes.get(0);
        concat(columnAttributes, rowAttributes).forEach(ca -> sortedAttributeValues
                .put(ca, columnView(ca).values().stream().distinct().sorted().map(e -> "" + e).collect(toList())));
        final List<Attribute<? extends Object>> unusedAttributes = list();
        final int columnsForRowHeaders = rowAttributes.size();
        final int firstAttributeColumnIndex = columnsForRowHeaders + 1;
        final int firstAttributeRowIndex = columnAttributes.size() + 1;
        headerView2().forEach(a -> {
            if (!columnAttributes.contains(a) && !rowAttributes.contains(a)) {
                unusedAttributes.add(a);
            }
        });
        final int unusedAttributeColumns;
        if (unusedAttributes.isEmpty()) {
            unusedAttributeColumns = 0;
        } else {
            unusedAttributeColumns = (unusedAttributes.size() - 1) * sortedAttributeValues.get(firstColumn).size();
        }
        /**
         * For every column/row attribute this map denotes, how many other columns/rows are between its columns.
         *
         * Every column of an column/row attribute corresponds to a value of the attribute.
         * Between any 2 neighbouring attribute column, there is a number of other attribute columns in between.
         */
        final Map<Attribute<? extends Object>, Integer> attributeDistances = map();
        {
            int rowSum = 1;
            for (int i = rowAttributes.size() - 1; i >= 0; --i) {
                attributeDistances.put(rowAttributes.get(i), rowSum);
                rowSum *= sortedAttributeValues.get(rowAttributes.get(i)).size();
            }
        }
        {
            int columnSum;
            if (unusedAttributes.isEmpty()) {
                columnSum = 1;
            } else {
                columnSum = unusedAttributes.size();
            }
            for (int i = columnAttributes.size() - 1; i >= 0; --i) {
                attributeDistances.put(columnAttributes.get(i), columnSum);
                columnSum *= sortedAttributeValues.get(columnAttributes.get(i)).size();
            }
        }
        final int attributeColumns = attributeDistances.get(firstColumn)
                * sortedAttributeValues.get(firstColumn).size();
        final int numberOfColumns = attributeColumns + columnsForRowHeaders;
        final List<List<String>> reformattedTable = list();

        // Create empty result table filled with empty string.
        final var firstRowDistance = attributeDistances.get(rowAttributes.get(0));
        final var rowValues = sortedAttributeValues.get(rowAttributes.get(0)).size();
        final int rowValueCount;
        if (rowAttributes.size() == 1) {
            rowValueCount = firstRowDistance;
        } else {
            rowValueCount = firstRowDistance * rowValues;
        }
        // `+2` is used for the Tables header.
        range(0, rowValueCount + 2)
                // `+1` is used for the names of the columns header.
                .forEach(c -> reformattedTable.add(listWithMultiple("", numberOfColumns + 1, String.class)));
        {
            // Create column header for the result table.
            range(0, columnAttributes.size()).forEach(c -> {
                final var attribute = columnAttributes.get(c);
                final var attributeDistance = attributeDistances.get(attribute);
                final var attributeValues = sortedAttributeValues.get(attribute);
                reformattedTable.get(c).set(firstAttributeColumnIndex - 1, attribute.name());
                range(0, sortedAttributeValues.get(attribute).size()).forEach(v -> {
                    final int valueColumn;
                    if (v != 0) {
                        valueColumn = firstAttributeColumnIndex + attributeDistance * v;
                    } else {
                        valueColumn = firstAttributeColumnIndex;
                    }
                    reformattedTable.get(0).set(valueColumn, attributeValues.get(v));
                });
            });
            range(0, attributeColumns).forEach(c -> {
                final var attribute = unusedAttributes.get(modulus(c, attributeDistances.get(firstColumn)));
                reformattedTable.get(firstAttributeRowIndex - 1).set(firstAttributeColumnIndex + c, attribute.name());
            });
            // Create row header for the result table.
            range(0, rowAttributes.size()).forEach(a -> {
                final var attribute = rowAttributes.get(a);
                final var attributeValues = sortedAttributeValues.get(attribute);
                final var valueCount = sortedAttributeValues.get(attribute).size();
                final var rowDistance = attributeDistances.get(attribute);
                reformattedTable.get(firstAttributeRowIndex - 1).set(a, attribute.name());
                range(0, rowValueCount).forEach(r -> {
                    final double distances = (double) r / rowDistance;
                    final double distanceMod = modulus(r, rowDistance);
                    final int distanceIndex = floorToInt(distances);
                    final int valueIndex = modulus(distanceIndex, valueCount);
                    final var value = attributeValues.get(valueIndex);
                    if (r == 0 || (!reformattedTable.get(r).get(a).equals(value)
                            && distanceMod == 0)) {
                        reformattedTable.get(r + firstAttributeRowIndex).set(a, value);
                    }
                });
            });
        }
        orderedLines().forEach(line -> {
            final int row;
            {
                int tmpRow = firstAttributeRowIndex; // The first row is a header row.
                for (int i = 0; i < rowAttributes.size(); ++i) {
                    final var attribute = rowAttributes.get(i);
                    final var attributeDistance = attributeDistances.get(attribute);
                    final var attributeIndex = sortedAttributeValues.get(attribute).indexOf("" + line.value(attribute));
                    tmpRow += attributeDistance * attributeIndex;
                }
                row = tmpRow;
            }
            final int column;
            {
                int tmpColumn = firstAttributeColumnIndex; // The first columns are used as row headers.
                for (int i = 0; i < columnAttributes.size(); ++i) {
                    final var attribute = columnAttributes.get(i);
                    final var attributeDistance = attributeDistances.get(attribute);
                    final var attributeIndex = sortedAttributeValues.get(attribute).indexOf("" + line.value(attribute));
                    if (attributeIndex != 0) {
                        tmpColumn += attributeDistance * attributeIndex;
                    }
                }
                column = tmpColumn;
            }
            if (unusedAttributeColumns == 0) {
                final var currentCellValue = reformattedTable.get(column).get(row);
                final String nextCellValue;
                if (currentCellValue.isEmpty()) {
                    nextCellValue = "x";
                } else {
                    logs().appendWarning(perspective("This code block should not be triggered as every cell should only have values of one line."));
                    nextCellValue = currentCellValue + ";x";
                }
                reformattedTable.get(row).set(column, nextCellValue);
            } else {
                range(0, unusedAttributes.size()).forEach(u -> {
                    final var currentCellValue = reformattedTable.get(row).get(column + u);
                    final String nextCellValue;
                    if (currentCellValue.isEmpty()) {
                        nextCellValue = "" + line.value(unusedAttributes.get(u));
                    } else {
                        logs().appendWarning(perspective("This code block should not be triggered as every cell should only have values of one line."));
                        nextCellValue = currentCellValue + "; " + line.value(unusedAttributes.get(u));
                    }
                    reformattedTable.get(row).set(column + u, nextCellValue);
                });
            }
        });
        return reformattedTable;
    }

    default boolean isEqualFormat(Database otherDatabase) {
        return setOfUniques(headerView()).hasContentOf((a, b) -> a.equalContentTo(b), otherDatabase.headerView())
                && name().equals(otherDatabase.name());
    }
}