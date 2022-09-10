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
package net.splitcells.gel.data.table.column;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.lookup.LookupComponents;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;

public interface ColumnView<T> extends ListView<T>, LookupComponents<T> {
    /**
     * TODO PERFORMANCE
     *
     * @return This is a {@link List} of all values in this {@link ColumnView}.
     * It does not contain all {@link Line} of the corresponding {@link Table}.
     */
    default List<T> values() {
        final List<T> values = Lists.<T>list();
        stream()
                .filter(e -> e != null)
                .forEach(e -> values.add(e));
        return values;
    }
}
