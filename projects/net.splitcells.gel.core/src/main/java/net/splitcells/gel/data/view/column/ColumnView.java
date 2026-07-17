/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.view.column;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.lookup.LookupMethods;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;

public interface ColumnView<T> extends ListView<T>, LookupMethods<T> {
    /**
     * TODO PERFORMANCE
     *
     * @return This is a {@link List} of all values in this {@link ColumnView}.
     * It does not contain all {@link Line} of the corresponding {@link View}.
     * The values have the same order, as the corresponding {@link Table#orderedLines()}.
     */
    default List<T> values() {
        final List<T> values = Lists.<T>list();
        stream()
                .filter(e -> e != null)
                .forEach(e -> values.add(e));
        return values;
    }
}
