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

    Line add(Line line);

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

    void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber);
}