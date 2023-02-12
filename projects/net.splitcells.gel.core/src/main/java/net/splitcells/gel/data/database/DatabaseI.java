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
package net.splitcells.gel.data.database;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.resource.communication.interaction.LogLevel.DEBUG;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.utils.CommonFunctions.removeAny;
import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.ListViewI.listView;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.table.LineI.line;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.LineI;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnI;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.data.table.column.ColumnViewI;
import org.w3c.dom.Element;
import net.splitcells.dem.utils.StreamUtils;
import net.splitcells.dem.object.Discoverable;

/**
 * <p>TODO Make all constructors private. One can use configurators for {@link DatabaseFactory} instead.</p>
 * <p>TODO Test consistency of meta data.</p>
 */
public class DatabaseI implements Database {
    protected final String name;
    protected final Optional<Discoverable> parent;
    protected final List<Attribute<Object>> attributes;
    protected final List<Attribute<? extends Object>> attributes2;
    protected final List<Column<Object>> columns = list();
    protected final Map<Attribute<?>, Integer> typed_column_index = map();
    protected final Set<Line> lines = setOfUniques();
    protected final List<Line> rawLines = list();
    protected final ListView<Line> rawLinesView = listView(rawLines);
    protected int size;
    protected final List<AfterAdditionSubscriber> additionSubscriber = list();
    protected final List<BeforeRemovalSubscriber> beforeRemovalSubscriber = list();
    protected final List<BeforeRemovalSubscriber> afterRemovalSubscriber = list();
    protected final net.splitcells.dem.data.set.Set<Integer> indexesOfFree = setOfUniques();
    private Optional<Constraint> constraint = Optional.empty();


    @Deprecated
    protected DatabaseI(List<Attribute<? extends Object>> attributes) {
        this("", null, attributes.mapped(a -> (Attribute<Object>) a));
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    protected DatabaseI(String name, Discoverable parent, List<Attribute<Object>> attributes) {
        this.name = name;
        this.parent = Optional.ofNullable(parent);
        final List<Attribute<Object>> headerAttributes = list();
        attributes.forEach(att -> {
            typed_column_index.put(att, headerAttributes.size());
            headerAttributes.add(att);
            columns.add(ColumnI.column(this, att));
        });
        this.attributes = listWithValuesOf(headerAttributes);
        columns.forEach(this::subscribeToAfterAdditions);
        columns.forEach(this::subscribeToBeforeRemoval);
        attributes2 = list();
        attributes2.addAll(attributes);
    }

    @Deprecated
    protected DatabaseI(List<Attribute<?>> attributes, Collection<List<Object>> linesValues) {
        this(attributes);
        linesValues.forEach(line_values -> addTranslated(line_values));
    }

    protected DatabaseI(String name, Discoverable parent, Attribute<? extends Object>... attributes) {
        this(name, parent, listWithValuesOf(attributes).mapped(a -> (Attribute<Object>) a));
    }

    @Deprecated
    public DatabaseI(Attribute<?>... attributes) {
        this(listWithValuesOf(attributes));
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return attributes;
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return attributes2;
    }

    /**
     * TODO PERFORMANCE Cache list views in Order to minimize number of objects.
     *
     * @param attribute This is the {@link Attribute} of the corresponding {@link Column}.
     * @param <T>       This is the type of the column.
     * @return This is the {@link Column} corresponding to given {@link Attribute}.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(headerView().contains(attribute))
                    .describedAs(attributes.stream()
                            .map(a -> a.name() + ", ")
                            .reduce((a, b) -> a + b)
                            .orElse("")
                            + ": " + attribute.name())
                    .isTrue();
            assertThat(typed_column_index.containsKey(attribute))
                    .describedAs(attribute.name() + " is not present in "
                            + typed_column_index.keySet().stream()
                            .map(a -> a.name())
                            .reduce((a, b) -> a + ", " + b)
                            .orElseThrow()
                    )
                    .isTrue();
        }
        try {
            return (Column<T>) columns.get(typed_column_index.get(attribute));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ListView<Line> rawLinesView() {
        return rawLinesView;
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        this.additionSubscriber.add(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        beforeRemovalSubscriber.add(subscriber);
    }

    @Override
    public Line add(Line line) {
        final List<Object> lineValues = list();
        range(0, attributes.size()).forEach(i -> {
            lineValues.add(line.value(attributes.get(i)));
        });
        return addTranslated(lineValues, line.index());
    }

    protected Line addTranslated(List<Object> lineValues, int index) {
        if (TRACING) {
            domsole().append(
                    event("addTranslatingAt." + Database.class.getSimpleName()
                            , path().toString()
                            , elementWithChildren("index", textNode("" + index))
                            , elementWithChildren("line-values", textNode(lineValues.toString()))
                    )
                    , this
                    , DEBUG
            );
        }
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(lineValues.size()).isEqualTo(headerView().size());
            require(indexesOfFree.contains(index) || index >= rawLines.size());
            range(0, lineValues.size()).forEach(i -> attributes.get(i).assertArgumentCompatibility(lineValues.get(i)));
        }
        if (index >= rawLines.size()) {
            range(0, lineValues.size()).forEach(i -> {
                extend_content_to(columns.get(i), index);
            });
            rawLines.prepareForSizeOf(index);
            rangeClosed(rawLines.size(), index).forEach(i -> {
                indexesOfFree.add(i);
                rawLines.add(null);
            });
        }
        indexesOfFree.delete(index);
        range(0, lineValues.size()).forEach(i -> columns.get(i).set(index, lineValues.get(i)));
        ++size;
        final var line = LineI.line(this, index);
        rawLines.set(line.index(), line);
        lines.add(line);
        additionSubscriber.forEach(subscriber -> subscriber.registerAddition(line));
        return line;
    }

    /**
     * TODO Move this to an utility class.
     */
    protected static void extend_content_to(List<?> list, int targetMaximalIndex) {
        while (list.size() < targetMaximalIndex + 1) {
            list.add(null);
        }
    }

    @Override
    public Line addTranslated(List<? extends Object> lineValues) {
        final int lineIndex;
        final Line line;
        if (indexesOfFree.isEmpty()) {
            lineIndex = rawLines.size();
            line = LineI.line(this, lineIndex);
            rawLines.add(line);
            range(0, lineValues.size()).forEach(i -> columns.get(i).add(lineValues.get(i)));
        } else {
            lineIndex = removeAny(indexesOfFree);
            range(0, lineValues.size()).forEach(i -> columns.get(i).set(lineIndex, lineValues.get(i)));
            line = LineI.line(this, lineIndex);
            rawLines.set(lineIndex, line);
        }
        ++size;
        lines.add(line);
        additionSubscriber.forEach(subscriber -> subscriber.registerAddition(line));
        return line;
    }

    @Override
    public void remove(int lineIndex) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(indexesOfFree).doesNotContain(lineIndex);
            assertThat(lineIndex).isNotNegative();
            assert lineIndex < rawLines.size() : lineIndex + ":" + rawLines.size() + path();
            assertThat(rawLines.get(lineIndex)).isNotNull();
            lines.hasOnlyOnce(rawLines.get(lineIndex));
            columns.forEach(column -> {
                assert lineIndex < column.size();
                assert rawLines.size() == column.size();
            });
            assert lineIndex < rawLines.size();
        }
        final var removalFrom = rawLines.get(lineIndex);
        beforeRemovalSubscriber.forEach(subscriber -> subscriber.registerBeforeRemoval(removalFrom));
        columns.forEach(kolonna -> {
            kolonna.set(lineIndex, null);
        });
        lines.remove(rawLines.get(lineIndex));
        rawLines.set(lineIndex, null);
        indexesOfFree.add(lineIndex);
        --size;
        afterRemovalSubscriber.forEach(subscriber -> subscriber.registerBeforeRemoval(removalFrom));
    }

    @Override
    public void remove(Line line) {
        remove(line.index());
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        afterRemovalSubscriber.add(subscriber);
    }

    /**
     * TODO PERFORMANCE
     *
     * @return
     */
    @Override
    public List<Column<Object>> columnsView() {
        return listWithValuesOf(columns);
    }

    @Override
    public String toString() {
        return Database.class.getSimpleName() + path().toString();
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        return parent.map(Discoverable::path).orElse(list()).withAppended(name);
    }

    @Override
    public Element toDom() {
        final var dom = elementWithChildren(Database.class.getSimpleName());
        rawLinesView().stream()
                .filter(rinda -> rinda != null)
                .forEach(rinda -> dom.appendChild(rinda.toDom()));
        return dom;
    }

    @Override
    public List<Line> rawLines() {
        return listWithValuesOf(lines);
    }

    @Override
    public Line lookupEquals(Attribute<Line> atribūts, Line rinda) {
        return lines.stream()
                .filter(citaRinda -> citaRinda.value(atribūts).index() == rinda.index())
                .reduce(StreamUtils.ensureSingle())
                .orElseThrow();
    }

    @Override
    public Stream<Line> linesStream() {
        return lines.stream();
    }

    @Override
    public List<Line> orderedLines() {
        return rawLines.stream()
                .filter(e -> e != null)
                .collect(Lists.toList());

    }

    @Override
    public Stream<Line> orderedLinesStream() {
        return rawLines.stream().filter(e -> e != null);
    }

    @Override
    public Query query() {
        if (constraint.isEmpty()) {
            final var constraintRoot = forAll();
            synchronize(constraintRoot);
            constraint = Optional.of(constraintRoot);
            linesStream().forEach(constraintRoot::registerAddition);
        }
        return constraint.get().query();
    }

    @Override
    public Object identity() {
        return this;
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Database) {
            final var castedArg = (Database) arg;
            return identity() == castedArg.identity();
        }
        throw executionException("Invalid argument type: " + arg);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
