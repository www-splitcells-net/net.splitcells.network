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

import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.gel.data.table.LinePointerI.linePointer;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;

/**
 * TODO {@link Line}s and {@link Table}s should be typed. Use a meta {@link Attribute}, which
 * supports multiple types and just use one attribute type per data base.
 */
public interface Line extends Domable {

    static List<?> concat(Line... lines) {
        final List<Object> concatination = list();
        for (var line : lines) {
            line.context().headerView()
                    .forEach(attribute -> concatination.add(line.value(attribute)));
        }
        return concatination;
    }

    <T> T value(Attribute<T> attribute);

    /**
     * Minimize usage of index as it is prone to errors.
     * Access by identity is more secure, than access by integer,
     * because one can do calculations with indexes,
     * which is prone to errors.
     * See references vs pointers in programming languages.
     *
     * @return The index of the {@link Line}in {@link Line#context}.
     */
    int index();

    default LinePointer toLinePointer() {
        return linePointer(context(), index());
    }

    Table context();

    default boolean equalsTo(Line other) {
        return index() == other.index() && context().equals(other.context());
    }

    default boolean isValid() {
        return null != context().rawLinesView().get(index());
    }

    default List<String> toStringList() {
        return listWithValuesOf
                (context().headerView().stream()
                        .map(attribute -> value(attribute).toString())
                        .collect(toList()));
    }

    default List<Object> values() {
        return context()
                .headerView()
                .stream()
                .map(attribute -> value(attribute))
                .collect(toList());
    }
}
