/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.table;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.AspectOrientedResource;
import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.gel.data.view.attribute.Attribute;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

public interface TableFactory extends AspectOrientedResource<Table>, ConnectingConstructor<Table> {
    Table table(String name, Attribute<? extends Object>... attributes);

    Table table(Attribute<? extends Object>... attributes);

    @Deprecated
    default Table _table(List<Attribute<?>> attributes) {
        return table(attributes);
    }

    Table table(List<Attribute<?>> attributes);

    Table table2(String name, Discoverable parent, List<Attribute<Object>> attributes);

    /**
     * TODO REMOVE This method just makes {@link TableFactory} unnecessary complex.
     *
     * @param attributes
     * @param linesValues
     * @return
     */
    @Deprecated
    Table table(List<Attribute<? extends Object>> attributes, List<List<Object>> linesValues);

    @Deprecated
    Table table(String name, Discoverable parent, Attribute<? extends Object>... attributes);

    Table table(String name, Discoverable parent, List<Attribute<? extends Object>> attributes);
}
