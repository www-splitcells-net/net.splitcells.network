/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;

/**
 * TODO Line index randomizer in order to test index calculations.
 */
public interface Database extends Table {

    Line addTranslated(List<? extends Object> values);

    Line add(Line line);

    @Deprecated
    void remove(int lineIndex);

    void remove(Line line);

    default void replace(Line newLine) {
        if (null != rawLinesView().get(newLine.index())) {
            remove(newLine.index());
        }
        add(newLine);
    }

    /**
     * This methods signifies, that the removal and addition subscribers belong together.
     *
     * @param subscriber This is the synchronize.
     */
    default void synchronize(DatabaseSynchronization subscriber) {
        subscribeToAfterAdditions(subscriber);
        subscriberToBeforeRemoval(subscriber);
    }

    void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber);

    void subscriberToBeforeRemoval(BeforeRemovalSubscriber subscriber);

    void subscriberToAfterRemoval(BeforeRemovalSubscriber subscriber);
}