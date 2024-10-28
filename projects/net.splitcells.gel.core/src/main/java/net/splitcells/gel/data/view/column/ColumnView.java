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
package net.splitcells.gel.data.view.column;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.lookup.LookupComponents;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;

public interface ColumnView<T> extends ListView<T>, LookupComponents<T> {
    /**
     * TODO PERFORMANCE
     *
     * @return This is a {@link List} of all values in this {@link ColumnView}.
     * It does not contain all {@link Line} of the corresponding {@link View}.
     * The values have the same order, as the corresponding {@link Database#orderedLines()}.
     */
    default List<T> values() {
        final List<T> values = Lists.<T>list();
        stream()
                .filter(e -> e != null)
                .forEach(e -> values.add(e));
        return values;
    }
}
