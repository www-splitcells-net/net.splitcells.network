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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.attribute.IndexedAttribute;
import org.w3c.dom.Node;

import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.toList;

public class LineWithValues implements Line {

    public static Line lineWithValues(Table context, ListView<Object> values, int index) {
        return new LineWithValues(context, values, index);
    }

    private final Table context;
    private final ListView<Attribute<Object>> header;
    private final ListView<Object> values;
    private final int index;

    private LineWithValues(Table context, ListView<Object> values, int index) {
        this.context = context;
        this.values = values;
        this.index = index;
        header = context.headerView();
    }

    @Override
    public <T> T value(Attribute<T> attribute) {
        final var attributeIndex = range(0, header.size())
                .filter(h -> header.get(h).equals(attribute))
                .findFirst()
                .orElseThrow();
        return (T) values.get(attributeIndex);
    }

    @Override
    public <T> T value(IndexedAttribute<T> attribute) {
        return (T) values.get(attribute.headerIndex());
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public Table context() {
        return context;
    }

    @Override
    public ListView<Object> values() {
        return values;
    }
}
