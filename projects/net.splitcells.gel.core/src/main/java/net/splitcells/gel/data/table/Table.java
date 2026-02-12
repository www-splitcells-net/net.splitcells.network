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

import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.CsvDocument.csvDocument;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * <p>TODO Line index randomizer in order to test index calculations.</p>
 * <p>TODO IDEA Make Database easily and comfortable compatible to POJOs and
 * thereby make a type safe interface of {@link Table}</p>
 * <p>TODO IDEA PERFORMANCE A database should be implementable with a byte matrix as backend.
 * Such an performance optimization would make also sense,
 * if one needs to work on an instance of data (i.e. CSV files),
 * without creating copies or using caches.
 * Such an additional implementation could also be used,
 * in order to measure the performance impact of cache based system
 * and also the correctness of such a system.
 * Such a backend should support complex and dynamic types by using some
 * columns as index storages.
 * These indexes would point to a complex or dynamic object
 * in an appropriate additional storage.
 * In order to make this possible with a good performance the interface
 * needs to support primitive types without casting.</p>
 * <p>TODO Create a {@link Table} implementation with in-memory SQL backend (i.e. H2 database) as prerequisite,
 * for example, to GPU backed {@link Table} implementations.
 * This prerequisite would be a comparably easy undertaking compared to a GPU backed {@link Table},
 * because of the SQL language.
 * Therefore a SQL backed database could show problems,
 * that would be present for GPU backed {@link Table} as well,
 * which would be easier to tackle in the SQL context compared to the GPU context.
 * Keep in mind,
 * that it makes sense to create more than alternative implementations for {@link Table} in these cases,
 * as otherwise the performance increase is very limited.</p>
 */
public interface Table extends View {

    /**
     * Create a new {@link Line} with the given values and places it at the given location.
     *
     * @param lineValues The value of the new {@link Line}.
     * @param index      The location of the new {@link Line}.
     * @return Returns the newly added {@link Line}.
     */
    Line addTranslated(ListView<Object> lineValues, int index);

    default Line addTranslated(Object... values) {
        return addTranslated(listWithValuesOf(values));
    }

    Line addTranslated(ListView<? extends Object> values);

    /**
     * Adds a new {@link Line} to this {@link Table} at the same index, as {@link Line#index()}.
     * The new {@link Line}'s value will be queried from the given {@link Line}.
     *
     * @param line The index and the values of the new {@link Line}.
     * @return The newly created {@link Line}.
     */
    Line add(Line line);

    /**
     * This is a faster version of {@link #add(Line)}.
     * The speed is achieved by requiring the {@link View#headerView()} of this {@link Table} to be a prefix
     * to the {@link View#headerView()} of the given {@link Line#context()}.
     * In other words, all {@link View#headerView()}'s {@link Attribute}s of this {@link Table},
     * need to be present at the same location in {@link View#headerView()} of the {@link Line#context()}.
     *
     * @param line
     * @return
     */
    Line addWithSameHeaderPrefix(Line line);

    /**
     * This is done to make queries on {@link Table},
     * that are more complex, than {@link #persistedLookup(Attribute, Object)} and similar methods.
     * Keep in mind, that this is the recommended way, to do performant and complex queries.
     *
     * @return {@link Constraint} that is subscribed to this {@link Table}.
     */
    default Query query() {
        throw notImplementedYet();
    }

    /**
     * Removes the {@link Line}, where {@link Line#index()} is equal to the argument.
     * Index calculation should be omitted and therefore this method too, if possible.
     * Unfortunately, this method is needed in case a {@link Line} has to be removed from 2 synchronized {@link Table}.
     *
     * @param lineIndex The {@link Line#index()} to be removed.
     */
    void remove(int lineIndex);

    void remove(Line line);

    default void replace(Line newLine) {
        if (null != rawLinesView().get(newLine.index())) {
            remove(newLine.index());
        }
        add(newLine);
    }

    /**
     * These methods signify, that the removal and addition subscribers belong together.
     *
     * @param subscriber This is the synchronize.
     */
    default void synchronize(TableSynchronization subscriber) {
        subscribeToAfterAdditions(subscriber);
        subscribeToBeforeRemoval(subscriber);
    }

    void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber);

    void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber);

    /**
     * TODO The argument type is false.
     *
     * @param subscriber Object to be informed of removal after the fact.
     */
    @Deprecated
    void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber);

    @Override
    default Tree toTree() {
        final var dom = tree(getClass().getSimpleName());
        rawLinesView().stream()
                .filter(line -> line != null)
                .forEach(line -> dom.withChild(line.toTree()));
        return dom;
    }

    default Table withAddedCsv(String csvFile) {
        csvDocument(csvFile, headerView().stream().map(h -> h.name()).toArray(i -> new String[i]))
                .process(row -> {
                    final ListView<Object> rawValues = list();
                    headerView().forEach(attribute -> {
                        final var rawValue = row.value(attribute.name());
                        rawValues.add(attribute.deserializeValue(rawValue));
                    });
                    addTranslated(rawValues);
                });
        return this;
    }

    default Table withAddSimplifiedCsv(String csvFile) {
        final var csvContent = listWithValuesOf(csvFile.split("\n"));
        range(0, csvContent.size()).forEach(i -> {
            final ListView<Object> rawValues = list();
            if (!csvContent.get(i).isBlank()) {
                final var csvLine = listWithValuesOf(csvContent.get(i).split(","));
                range(0, csvLine.size()).forEach(j -> {
                    rawValues.add(headerView().get(j).deserializeValue(csvLine.get(j)));
                });
                addTranslated(rawValues);
            }
        });
        return this;
    }

    default Table withAllLinesRemoved() {
        unorderedLines().forEach(this::remove);
        return this;
    }

    default Optional<Solution> lookupAsSolution() {
        return Optional.empty();
    }
}