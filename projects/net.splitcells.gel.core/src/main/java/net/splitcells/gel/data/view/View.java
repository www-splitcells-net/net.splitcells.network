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
package net.splitcells.gel.data.view;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.CsvPrinter.csvDocument;
import static net.splitcells.dem.lang.CsvPrinter.toCsvString;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.testing.reporting.ErrorReporting.getWithReportedErrors;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.MathUtils.floorToInt;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.table.Tables.table2;
import static net.splitcells.gel.data.view.ViewHtmlTableConfig.viewHtmlTableConfig;

import java.util.Optional;
import java.util.stream.Stream;

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.Identifiable;
import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.dem.testing.reporting.ErrorReporter;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;

/**
 * <p>This is a view of a table like thing.
 * It is named like this in order to adhere to the SQL naming and
 * thereby makes the naming more relatable by extern people.</p>
 * <p>This was designed with a focus on columns instead of rows,
 * as iterating over a column's values was considered more important,
 * than iterating over a row's values.</p>
 */
public interface View extends Discoverable, Domable, Identifiable {
    /**
     * This is the {@link Line#index()}, that never exists.
     * This constant is helpful, in order to state the semantic intent of an index.
     */
    int INVALID_INDEX = -1;
    /**
     * This name is used in order to mark mirrors of {@link View},
     * so that a recursion error can be avoided,
     * when {@link #discoverableRenderer()} creates a thread safe mirror for rendering via an {@link ConnectingConstructor}.
     */
    String MIRROR_NAME = "mirror";
    boolean GET_LINE_VIA_STREAM = true;

    String name();

    /**
     * TODO This method should return a {@link ListView}.
     *
     * @return
     */
    List<Attribute<Object>> headerView();

    default String simplifiedHeaderCsv() {
        return headerView().stream().map(h -> h.name()).reduce((a, b) -> a + "," + b).orElse("");
    }

    /**
     * TODO This method should return a {@link ListView}.
     *
     * @return
     */
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

    default boolean misses(Line line) {
        return !contains(line);
    }

    /**
     * @return {@link List} of {@link Line}, that is ordered by {@link Line#index()}.
     */
    default List<Line> orderedLines() {
        return rawLines().stream()
                .filter(e -> e != null)
                .collect(Lists.toList());
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
     * @return
     * @see #unorderedLinesStream2()
     */
    @Deprecated
    default Stream<Line> unorderedLinesStream() {
        return unorderedLines().stream();
    }

    /**
     * <p>This method exists for improved performance compared to calling
     * <code>lines().stream()</code> on certain {@link View} implementations.
     * This is caused by the fact, that this method does not need a copy all {@link Line} references,
     * before constructing this {@link Stream}.</p>
     *
     * <p>Also, it is expected, that generally {@link #unorderedLinesStream()} is the fastest method,
     * in order to iterate or list all {@link Line}.
     * Especially, it is expected to be generally faster, than {@link #rawLines()}.
     * Only in select cases {@link #rawLines()} is expected to be faster,
     * where knowledge of the underlining {@link View} implementation is present.
     * The reason for this, is the fact, that it is faster to construct {@link #unorderedLinesStream()}
     * via {@link #rawLines()}, than the other way around.</p>
     * <p>There is no guarantee regarding the ordering.</p>
     * <p>TODO Replace {@link #unorderedLinesStream()} with this method.</p>
     *
     * @return An ordered {@link Stream} of {@link #orderedLines()}.
     */
    default Flow<Line> unorderedLinesStream2() {
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
     * @see Table#query()
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

    default boolean isRawLine(int index) {
        return rawLine(index) != null;
    }

    /**
     * <p>Retrieves the N-th {@link Line} of this {@link View},
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
            return orderedLines().get(n);
        }
    }

    default Line anyLine() {
        return unorderedLine(0);
    }

    default Line unorderedLine(int n) {
        if (GET_LINE_VIA_STREAM) {
            if (n == 0) {
                return unorderedLinesStream().findFirst().orElseThrow();
            } else {
                return unorderedLinesStream().skip(n).findFirst().orElseThrow();
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

    /**
     * @deprecated Only return String matrices like {@link #toReformattedTable(List, List)},
     * in order to make it portable.
     */
    @Deprecated
    default String toCSV(ErrorReporter reporter) {
        return getWithReportedErrors(() -> toCSV(), reporter);
    }

    /**
     * @deprecated Only return String matrices like {@link #toReformattedTable(List, List)},
     * in order to make it portable.
     */
    @Deprecated
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
                            execException(tree("Could not render line for CSV rendering.")
                                    .withProperty("line", line.toString()));
                        }
                    });
            return printer.toString();
        } catch (Throwable e) {
            throw execException(tree("Could not render " + getClass() + " as a CSV.")
                    .withProperty("path", path().toString()));
        }
    }

    /**
     * @deprecated Only return String matrices like {@link #toReformattedTable(List, List)},
     * in order to make it portable.
     */
    @Deprecated
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

    default <T> View persistedLookup(Attribute<T> attribute, T value) {
        return columnView(attribute).persistedLookup(value);
    }

    default <T> View lookup(Attribute<T> attribute, T value) {
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

    default Tree toHtmlTable() {
        return toHtmlTable(viewHtmlTableConfig());
    }

    default Tree toHtmlTable(ViewHtmlTableConfig config) {
        final var htmlTable = tree("table", HTML);
        htmlTable.withProperty("class", HTML_ATTRIBUTE, "net-splitcells-website-visually-replaceable");
        final var header = tree("tr", HTML);
        header.withChild(tree("th", HTML).withText("index"));
        headerView().forEach(attribute -> {
            final var headAttribute = tree("th", HTML).withText(attribute.name());
            if (config.tablePopupViaColumnContent().isPresent()
                    && config.tablePopupViaColumnContent().get().equals(attribute.name())) {
                headAttribute.withProperty("class", HTML_ATTRIBUTE, "net-splitcells-website-table-popup-via-column-content");
            }
            header.withChild(headAttribute);
        });
        htmlTable.withChild(header);
        unorderedLines().forEach(line -> {
            final var row = tree("tr", HTML);
            row.withChild(tree("td", HTML).withText(line.index() + ""));
            headerView().forEach(attribute -> row.withChild(tree("td", HTML)
                    .withText(line.value(attribute).toString())));
            htmlTable.withChild(row);
        });
        return htmlTable;
    }

    default Tree toFods() {
        final var fods = tree("document", FODS_OFFICE)
                .withXmlAttribute("mimetype", "application/vnd.oasis.opendocument.spreadsheet", FODS_OFFICE);
        final var body = tree("body", FODS_OFFICE);
        fods.withChild(body);
        final var spreadSheet = tree("spreadsheet", FODS_OFFICE);
        body.withChild(spreadSheet);
        final var table = tree("table", FODS_TABLE);
        spreadSheet.withChild(table);
        table.withProperty("name", FODS_TABLE, "values");
        final var header = tree("table-row", FODS_TABLE);
        table.withChild(header);
        headerView().stream()
                .map(att -> att.name())
                .map(attName -> {
                    final var cell = tree("table-cell", FODS_TABLE);
                    cell.withChild(tree("p", FODS_TEXT).withChild(tree(attName)));
                    return cell;
                }).forEach(attDesc -> header.withChild(attDesc));
        unorderedLines().forEach(line -> {
            final var tableLine = tree("table-row", FODS_TABLE);
            table.withChild(tableLine);
            headerView().stream()
                    .map(attribute -> line.value(attribute))
                    .map(value -> {
                        final var cell = tree("table-cell", FODS_TABLE);
                        cell
                                .withChild(tree("p", FODS_TEXT).withChild(tree(value.toString())));
                        return cell;
                    }).forEach(tableElement -> tableLine.withChild(tableElement));
        });
        return fods;
    }

    static boolean referToSameData(View a, View b) {
        return a.path().equals(b.path());
    }

    default Attribute<? extends Object> attributeByName(String name) {
        return headerView().stream()
                .filter(da -> da.name().equals(name))
                .findFirst()
                .orElseThrow(() -> execException(tree("Could not find attribute by name.")
                        .withProperty("Table", toString())
                        .withProperty("Search Attribute Name", name)));
    }

    default Optional<Attribute<? extends Object>> searchAttributeByName(String name) {
        return headerView2().stream()
                .filter(da -> da.name().equals(name))
                .findFirst();
    }

    /**
     * @deprecated Only return String matrices like {@link #toReformattedTable(List, List)},
     * in order to make it portable.
     */
    default String toReformattedCsv(List<Attribute<? extends Object>> columnAttributes
            , List<Attribute<? extends Object>> rowAttributes) {
        final var reformattedSolution = toReformattedTable(columnAttributes, rowAttributes);
        if (reformattedSolution.isEmpty()) {
            return "";
        }
        final List<List<String>> csvContent = list();
        csvContent.addAll(rangeClosed(1, reformattedSolution.get(0).size())
                .mapToObj(i -> "" + i)
                .collect(toList()));
        csvContent.addAll(reformattedSolution);
        return toCsvString(csvContent);
    }

    /**
     * <p>Normally a {@link View} is drawn just like {@link #orderedLines()} is containing its data.
     * Also, the program works in this format, this is not always the best way to visualize a {@link View}.</p>
     * <p>Draws this as {@link View} in such a way,
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
            for (int i = rowAttributes.size() - 1 ; i >= 0 ; --i) {
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
            for (int i = columnAttributes.size() - 1 ; i >= 0 ; --i) {
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
                for (int i = 0 ; i < rowAttributes.size() ; ++i) {
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
                for (int i = 0 ; i < columnAttributes.size() ; ++i) {
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
                    logs().warn(tree("This code block should not be triggered as every cell should only have values of one line."));
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
                        logs().warn(tree("This code block should not be triggered as every cell should only have values of one line."));
                        nextCellValue = currentCellValue + "; " + line.value(unusedAttributes.get(u));
                    }
                    reformattedTable.get(row).set(column + u, nextCellValue);
                });
            }
        });
        return reformattedTable;
    }

    default View requireEqualFormat(Table otherTable) {
        if (!isEqualFormat(otherTable)) {
            throw execException(tree("Tables should have equal headers, but do not.")
                    .withProperty("This", toString())
                    .withProperty("OtherTable", toString()));
        }
        return this;
    }

    default boolean isEqualFormat(Table otherTable) {
        return setOfUniques(headerView()).hasContentOf((a, b) -> a.equalContentTo(b), otherTable.headerView())
                && name().equals(otherTable.name());
    }

    DiscoverableRenderer discoverableRenderer();

    /**
     * <p>Sometimes one needs to store {@link Line} inside a new {@link View}.
     * The problem is to decide,
     * whether the new {@link View} should reference the {@link Line} via one {@link ColumnView} or
     * whether to store all {@link Attribute} values of the referenced {@link Line} in dedicated {@link View#columnView(Attribute)}.
     * In other words, one has to decide whether to reference the {@link Line} or to copy the {@link Line#values()},
     * in order to avoid a reference lookup for each access to such a {@link Line}.
     * Both strategies have different performance trade-offs and at time of writing,
     * it is not clear which strategy is better in which situation.</p>
     * <p>It was decided to ignore this issue, by providing an interface,
     * that allows such a {@link View} to hide the fact, which of these strategies is chosen.
     * Therefore, such an interface can be backed by different implementations with different trade-offs.
     * Note, that copying the {@link Line#values()} would require one to use shadow {@link ColumnView},
     * that are not present in {@link View#headerView()}.</p>
     *
     * @param lineAttribute This is the {@link Attribute} of this {@link View}, that contains the relevant {@link Line}.
     * @param <T>           This is the type of the values being retrieved.
     * @return Returns a {@link Flow} of valueAttribute values,
     * that are contained in the {@link Line} of the lineAttribute's {@link ColumnView}.
     */
    default <T> Flow<Line> linesByReference(Attribute<Line> lineAttribute) {
        return columnView(lineAttribute).flow().map(l -> l);
    }

    /**
     * <p>Sometimes one needs to store {@link Line} inside a new {@link View}.
     * The problem is to decide,
     * whether the new {@link View} should reference the {@link Line} via one {@link ColumnView} or
     * whether to store all {@link Attribute} values of the referenced {@link Line} in dedicated {@link View#columnView(Attribute)}.
     * In other words, one has to decide whether to reference the {@link Line} or to copy the {@link Line#values()},
     * in order to avoid a reference lookup for each access to such a {@link Line}.
     * Both strategies have different performance trade-offs and at time of writing,
     * it is not clear which strategy is better in which situation.</p>
     * <p>It was decided to ignore this issue, by providing an interface,
     * that allows such a {@link View} to hide the fact, which of these strategies is chosen.
     * Therefore, such an interface can be backed by different implementations with different trade-offs.
     * Note, that copying the {@link Line#values()} would require one to use shadow {@link ColumnView},
     * that are not present in {@link View#headerView()}.</p>
     *
     * @param lineAttribute  This is the {@link Attribute} of this {@link View}, that contains the relevant {@link Line}.
     * @param valueAttribute This is the {@link Attribute} of the {@link View},
     *                       that are referenced by the {@link Line} of lineAttribute.
     * @param <T>            This is the type of the values being retrieved.
     * @return Returns a {@link Flow} of valueAttribute values,
     * that are contained in the {@link Line} of the lineAttribute's {@link ColumnView}.
     */
    default <T> Flow<T> referencedValues(Attribute<Line> lineAttribute, Attribute<T> valueAttribute) {
        return columnView(lineAttribute).flow().map(line -> line.value(valueAttribute));
    }

    default View requireSizeOf(int targetSize) {
        final int actualSize = size();
        if (actualSize != targetSize) {
            throw ExecutionException.execException("Flow should have the size of " + targetSize + ", but has a size of " + actualSize + " instead.");
        }
        return this;
    }
}