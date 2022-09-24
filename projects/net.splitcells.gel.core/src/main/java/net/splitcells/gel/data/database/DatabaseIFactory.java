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
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.attribute.Attribute;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

public class DatabaseIFactory implements DatabaseFactory {

    @Override
    public Database database(String name, Attribute<? extends Object>... attributes) {
        return new DatabaseI(name, null, attributes);
    }

    @Override
    public Database database(Attribute<? extends Object>... attributes) {
        return new DatabaseI(attributes);
    }

    @Override
    public Database database(List<Attribute<?>> attributes) {
        return new DatabaseI(attributes);
    }

    @Override
    public Database database2(String name, Discoverable parent, List<Attribute<Object>> attributes) {
        return new DatabaseI(name, parent, attributes);
    }

    @Override
    public Database database(List<Attribute<? extends Object>> attributes, List<List<Object>> linesValues) {
        return new DatabaseI(attributes, linesValues);
    }

    @Override
    @Deprecated
    public Database database(String name, Discoverable  parent, Attribute<? extends Object>... attributes) {
        return new DatabaseI(name, parent, listWithValuesOf(attributes).mapped(a -> (Attribute<Object>) a));
    }

    @Override
    public Database database(String name, Discoverable parent, List<Attribute<? extends Object>> attributes) {
        return new DatabaseI(name, parent, attributes.mapped(a -> (Attribute<Object>) a));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
