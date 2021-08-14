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
package net.splitcells.gel.data.table;

import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.gel.data.table.LinePointerI.linePointer;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;

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
