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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * <p>TODO Line index randomizer in order to test index calculations.</p>
 * <p>TODO IDEA Make Database easily and comfortable compatible to POJOs and
 * thereby make a type safe interface of {@link Database}</p>
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
 * needs to support primitive types without casting.
 * </p>>
 */
public interface Database extends Table {

    Line addTranslated(List<? extends Object> values);

    /**
     * Adds a new {@link Line} to this {@link Database} at the same index, as {@link Line#index()}.
     * The new {@link Line}'s value will be queried from the given {@link Line}.
     *
     * @param line The index and the values of the new {@link Line}.
     * @return The newly created {@link Line}.
     */
    Line add(Line line);

    /**
     * This is a faster version of {@link #add(Line)}.
     * The speed is achieved by requiring the {@link Table#headerView()} of this {@link Database} to be a prefix
     * to the {@link Table#headerView()} of the given {@link Line#context()}.
     * In other words, all {@link Table#headerView()}'s {@link Attribute}s of this {@link Database},
     * need to be present at the same location in {@link Table#headerView()} of the {@link Line#context()}.
     *
     * @param line
     * @return
     */
    Line addWithSameHeaderPrefix(Line line);

    /**
     * This is done to make queries on {@link Database},
     * that are more complex, than {@link #lookup(Attribute, Object)} and similar methods.
     * Keep in mind, that this is the recommended way, to do performant and complex queries.
     *
     * @return {@link Constraint} that is subscribed to this {@link Database}.
     */
    default Query query() {
        throw notImplementedYet();
    }

    /**
     * Removes the {@link Line}, where {@link Line#index()} is equal to the argument.
     * Index calculation should be omitted and therefore this method too, if possible.
     * Unfortunately, this method is needed in case a {@link Line} has to be removed from 2 synchronized {@link Database}.
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
    default void synchronize(DatabaseSynchronization subscriber) {
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
}