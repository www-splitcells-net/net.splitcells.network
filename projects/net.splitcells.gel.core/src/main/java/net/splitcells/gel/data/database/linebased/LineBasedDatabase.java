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
package net.splitcells.gel.data.database.linebased;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.StreamUtils;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnI;
import net.splitcells.gel.data.table.column.ColumnView;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StreamUtils.ensureSingle;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.database.linebased.LineBasedColumn.lineBasedColumn;
import static net.splitcells.gel.data.table.LineWithValues.lineWithValues;

public class LineBasedDatabase implements Database {

    private final String name;
    private final Optional<Discoverable> parent;
    private final List<Attribute<Object>> attributes;
    private final List<Attribute<? extends Object>> attributes2;
    private final List<Line> rawLines = list();
    private final Set<Line> lines = setOfUniques();
    private final List<LineBasedColumn<Object>> columns;
    private final List<ColumnView<Object>> columnsView = list();
    private final List<AfterAdditionSubscriber> additionSubscriber = list();
    private final List<BeforeRemovalSubscriber> beforeRemovalSubscriber = list();
    private final List<BeforeRemovalSubscriber> afterRemovalSubscriber = list();
    private final Set<Integer> indexesOfFree = setOfUniques();
    private final Map<Attribute<?>, Integer> typedColumnIndex = map();
    private Optional<Constraint> constraint = Optional.empty();

    public static Database lineBasedDatabase(String name, Optional<Discoverable> parent, List<Attribute<Object>> attributes) {
        return new LineBasedDatabase(name, parent, attributes);
    }

    private LineBasedDatabase(String name, Optional<Discoverable> parent, List<Attribute<Object>> attributes) {
        this.name = name;
        this.parent = parent;
        this.attributes = attributes;
        this.attributes2 = list();
        attributes2.addAll(attributes);
        columns = attributes.mapped(a -> lineBasedColumn(this, a));
        columnsView.addAll(columns);
        range(0, attributes.size()).forEach(i -> typedColumnIndex.put(attributes.get(i), i));
        columns.forEach(this::subscribeToAfterAdditions);
        columns.forEach(this::subscribeToBeforeRemoval);

    }

    @Override
    public Object identity() {
        return this;
    }

    @Override
    public List<String> path() {
        return parent.map(Discoverable::path).orElse(list()).withAppended(name);
    }

    @Override
    public Line addTranslated(ListView<? extends Object> values) {
        final Line newLine;
        if (indexesOfFree.isEmpty()) {
            newLine = lineWithValues(this, values, rawLines.size());
            rawLines.add(newLine);
        } else {
            final int lineIndex = indexesOfFree.removeAny();
            newLine = lineWithValues(this, values, lineIndex);
            rawLines.set(lineIndex, newLine);
        }
        lines.add(newLine);
        additionSubscriber.forEach(subscriber -> subscriber.registerAddition(newLine));
        return newLine;
    }

    @Override
    public Line add(Line argLine) {
        final List<Object> values = list();
        attributes.forEach(a -> values.add(argLine.value(a)));
        return addTranslated(values, argLine.index());
    }

    private Line addTranslated(ListView<?> values, int index) {
        final var newLine = lineWithValues(this, values, index);
        final var rawLinesSize = rawLines.size();
        if (rawLinesSize > index) {
            indexesOfFree.remove(index);
            rawLines.set(newLine.index(), newLine);
        } else if (rawLinesSize == index) {
            rawLines.add(newLine);
        } else {
            range(rawLinesSize, index).forEach(i -> {
                indexesOfFree.add(i);
                rawLines.add(null);
            });
            rawLines.add(newLine);
        }
        lines.add(newLine);
        additionSubscriber.forEach(subscriber -> subscriber.registerAddition(newLine));
        return newLine;
    }

    @Override
    public Line addWithSameHeaderPrefix(Line argLine) {
        return addTranslated(argLine.values().getRemovedUntilExcludedIndex(attributes.size()), argLine.index());
    }

    @Override
    public void remove(int lineIndex) {
        final var removalFrom = rawLines.get(lineIndex);
        beforeRemovalSubscriber.forEach(subscriber -> subscriber.registerBeforeRemoval(removalFrom));
        rawLines.set(lineIndex, null);
        lines.remove(removalFrom);
        indexesOfFree.add(lineIndex);
        afterRemovalSubscriber.forEach(subscriber -> subscriber.registerBeforeRemoval(removalFrom));
    }

    @Override
    public void remove(Line line) {
        remove(line.index());
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        additionSubscriber.add(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        beforeRemovalSubscriber.add(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        afterRemovalSubscriber.add(subscriber);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return attributes;
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return attributes2;
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return (ColumnView<T>) columns.get(typedColumnIndex.get(attribute));
    }

    @Override
    public ListView<ColumnView<Object>> columnsView() {
        return columnsView;
    }

    @Override
    public ListView<Line> rawLinesView() {
        return rawLines;
    }

    @Override
    public int size() {
        return lines.size();
    }

    @Override
    public List<Line> rawLines() {
        return rawLines;
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line line) {
        return lines.stream()
                .filter(otherLine -> otherLine.value(attribute).index() == line.index())
                .reduce(ensureSingle())
                .orElseThrow();
    }

    @Override
    public Stream<Line> unorderedLinesStream() {
        return lines.stream();
    }

    @Override
    public List<Line> orderedLines() {
        return rawLines.stream()
                .filter(e -> e != null)
                .collect(toList());

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
            unorderedLinesStream().forEach(constraintRoot::registerAddition);
        }
        return constraint.get().query();
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
