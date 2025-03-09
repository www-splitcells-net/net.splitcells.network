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
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.DescribedBool.describedBool;
import static net.splitcells.dem.data.atom.Integers.requireEqualInts;
import static net.splitcells.dem.data.atom.Integers.requireNotNegative;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.LogLevel.DEBUG;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.ListViewI.listView;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.table.TableSynchronizationAspect.tableSynchronizationAspect;
import static net.splitcells.gel.data.table.Tables.table2;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.execution.EffectSynchronization;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.type.framework.ConstraintAspect;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.LineI;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.Column;
import net.splitcells.gel.data.view.column.ColumnI;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.dem.utils.StreamUtils;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;

/**
 * <p>TODO Make all constructors private. One can use configurators for {@link TableFactory} instead.</p>
 * <p>TODO Test consistency of meta data.</p>
 */
public class TableI implements Table {
    private final String name;
    private final Optional<Discoverable> parent;
    private final List<Attribute<Object>> attributes;
    private final List<Attribute<? extends Object>> attributes2;
    private final List<Column<Object>> columns = list();
    private final ListView<Column<Object>> columnsView = listView(columns);
    private final ListView<ColumnView<Object>> columnsViewView;
    private final Map<Attribute<?>, Integer> typedColumnIndex = map();
    private final Set<Line> lines = setOfUniques();
    private final List<Line> rawLines = list();
    private final ListView<Line> rawLinesView = listView(rawLines);
    private int size;
    private final List<AfterAdditionSubscriber> additionSubscriber = list();
    private final List<BeforeRemovalSubscriber> beforeRemovalSubscriber = list();
    private final List<BeforeRemovalSubscriber> afterRemovalSubscriber = list();
    private final net.splitcells.dem.data.set.Set<Integer> indexesOfFree = setOfUniques();
    private Optional<Constraint> constraint = Optional.empty();
    private Optional<Table> threadSafeMirror = Optional.empty();

    @Deprecated
    public static Table tableI(List<Attribute<? extends Object>> attributes) {
        return new TableI(attributes);
    }


    @Deprecated
    private TableI(List<Attribute<? extends Object>> attributes) {
        this("", null, attributes.mapped(a -> (Attribute<Object>) a));
    }

    public static Table tableI(String name, Discoverable parent, List<Attribute<Object>> attributes) {
        return new TableI(name, parent, attributes);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    private TableI(String name, Discoverable parent, List<Attribute<Object>> attributes) {
        this.name = name;
        this.parent = Optional.ofNullable(parent);
        final List<Attribute<Object>> headerAttributes = list();
        attributes.forEach(att -> {
            typedColumnIndex.put(att, headerAttributes.size());
            headerAttributes.add(att);
            columns.add(ColumnI.column(this, att));
        });
        this.attributes = listWithValuesOf(headerAttributes);
        columns.forEach(this::subscribeToAfterAdditions);
        columns.forEach(this::subscribeToBeforeRemoval);
        attributes2 = list();
        attributes2.addAll(attributes);
        final List<ColumnView<Object>> columnsViewViewList = list();
        columns.forEach(c -> columnsViewViewList.add(c));
        columnsViewView = listView(columnsViewViewList);
    }

    public static Table tableI(List<Attribute<?>> attributes, Collection<List<Object>> linesValues) {
        return new TableI(attributes, linesValues);
    }

    public static Table tableI(String name, Discoverable parent, Attribute<? extends Object>... attributes) {
        return new TableI(name, parent, attributes);
    }

    @Deprecated
    public static Table tableI(Attribute<?>... attributes) {
        return new TableI(attributes);
    }

    @Deprecated
    private TableI(List<Attribute<?>> attributes, Collection<List<Object>> linesValues) {
        this(attributes);
        linesValues.forEach(lineValues -> addTranslated(lineValues));
    }

    private TableI(String name, Discoverable parent, Attribute<? extends Object>... attributes) {
        this(name, parent, listWithValuesOf(attributes).mapped(a -> (Attribute<Object>) a));
    }

    @Deprecated
    private TableI(Attribute<?>... attributes) {
        this(listWithValuesOf(attributes));
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
            describedBool(headerView().contains(attribute)
                    , () -> "The table with the following attributes:"
                            + attributes.stream()
                            .map(a -> a.name() + ", ")
                            .reduce((a, b) -> a + b)
                            .orElse("")
                            + "\n should contain " + attribute.name()
                            + " but does not.")
                    .required();
            describedBool(typedColumnIndex.containsKey(attribute)
                    , () -> attribute.name() + " is not present in "
                            + typedColumnIndex.keySet().stream()
                            .map(a -> a.name())
                            .reduce((a, b) -> a + ", " + b)
                            .orElseThrow())
                    .required();
        }
        try {
            return (Column<T>) columns.get(typedColumnIndex.get(attribute));
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
    public Line addWithSameHeaderPrefix(Line line) {
        final List<Object> lineValues = list();
        final var lineColumns = line.context().columnsView();
        final var lineIndex = line.index();
        range(0, attributes.size()).forEach(i -> {
            lineValues.add(lineColumns.get(i).get(lineIndex));
        });
        return addTranslated(lineValues, line.index());
    }

    @Override
    public Line add(Line line) {
        final List<Object> lineValues = list();
        range(0, attributes.size()).forEach(i -> {
            lineValues.add(line.value(attributes.get(i)));
        });
        return addTranslated(lineValues, line.index());
    }

    @Override
    public Line addTranslated(ListView<Object> lineValues, int index) {
        if (TRACING) {
            logs().append(tree("addTranslatingAt." + Table.class.getSimpleName())
                            .withProperty("path", path().toString())
                            .withProperty("index", "" + index)
                            .withProperty("line-values", lineValues.toString())
                    , this
                    , DEBUG
            );
        }
        if (ENFORCING_UNIT_CONSISTENCY) {
            requireEqualInts(lineValues.size(), headerView().size());
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
        // TODO Wouldn't the size of the header be more important in order to detect errors?
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
    private static void extend_content_to(List<?> list, int targetMaximalIndex) {
        while (list.size() < targetMaximalIndex + 1) {
            list.add(null);
        }
    }

    @Override
    public Line addTranslated(ListView<? extends Object> lineValues) {
        final int lineIndex;
        final Line line;
        if (ENFORCING_UNIT_CONSISTENCY) {
            lineValues.requireSizeOf(columns.size());
        }
        if (indexesOfFree.isEmpty()) {
            lineIndex = rawLines.size();
            line = LineI.line(this, lineIndex);
            rawLines.add(line);
            range(0, lineValues.size()).forEach(i -> columns.get(i).add(lineValues.get(i)));
        } else {
            lineIndex = indexesOfFree.removeAny();
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
            indexesOfFree.requireAbsenceOf(lineIndex);
            requireNotNegative(lineIndex);
            if (lineIndex >= rawLines.size()) {
                throw ExecutionException.execException(lineIndex + ":" + rawLines.size() + path());
            }
            requireNotNull(rawLines.get(lineIndex));
            lines.hasOnlyOnce(rawLines.get(lineIndex));
            columns.forEach(column -> {
                require(lineIndex < column.size());
                requireEqualInts(rawLines.size(), column.size());
            });
            require(lineIndex < rawLines.size());
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
    public ListView<ColumnView<Object>> columnsView() {
        return columnsViewView;
    }

    @Override
    public String toString() {
        return Table.class.getSimpleName() + path().toString();
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        return parent.map(Discoverable::path).orElse(list()).withAppended(name);
    }

    @Override
    public List<Line> rawLines() {
        return listWithValuesOf(rawLines);
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line line) {
        return lines.stream()
                .filter(otherLine -> otherLine.value(attribute).index() == line.index())
                .reduce(StreamUtils.ensureSingle())
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
                .collect(Lists.toList());
    }

    @Override
    public Stream<Line> orderedLinesStream() {
        return rawLines.stream().filter(e -> e != null);
    }

    @EffectSynchronization(ConstraintAspect.class)
    @Override
    public Query query() {
        if (constraint.isEmpty()) {
            final var constraintRoot = forAll();
            synchronize(new TableSynchronization() {

                @EffectSynchronization(ConstraintAspect.class)
                @Override
                public void registerBeforeRemoval(Line removal) {
                    constraintRoot.registerBeforeRemoval(removal);
                    constraintRoot.rating();
                }

                @EffectSynchronization(ConstraintAspect.class)
                @Override
                public void registerAddition(Line addition) {
                    constraintRoot.registerAddition(addition);
                    constraintRoot.rating();
                }
            });
            constraint = Optional.of(constraintRoot);
            unorderedLinesStream().forEach(constraintRoot::registerAddition);
            constraintRoot.rating();
        }
        return constraint.get().query();
    }

    @Override
    public Object identity() {
        return this;
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Table table) {
            return identity() == table.identity();
        }
        throw ExecutionException.execException("Invalid argument type: " + arg);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public DiscoverableRenderer discoverableRenderer() {
        if (threadSafeMirror.isEmpty()) {
            final var mirror = tableSynchronizationAspect(table2(MIRROR_NAME, this, this.headerView()));
            orderedLinesStream().forEach(mirror::add);
            subscribeToAfterAdditions(mirror::add);
            subscribeToBeforeRemoval(mirror::remove);
            threadSafeMirror = Optional.of(mirror);
        }
        return new DiscoverableRenderer() {

            @Override
            public String render() {
                return threadSafeMirror.get().toHtmlTable().toHtmlString();
            }

            @Override
            public Optional<String> title() {
                return Optional.of(threadSafeMirror.get().path().toString());
            }

            @Override
            public List<String> path() {
                return threadSafeMirror.get().path();
            }
        };
    }
}
