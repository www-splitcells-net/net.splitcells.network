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

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.perspective;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.gel.common.Language.*;

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.data.table.attribute.IndexedAttribute;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.data.table.attribute.Attribute;

public class LineI implements Line {
    private final Table context;
    private final int index;

    public static Line line(Table context, int index) {
        return new LineI(context, index);
    }

    protected LineI(Table context, int line) {
        this.context = context;
        this.index = line;
    }

    @Override
    public <T> T value(Attribute<T> attribute) {
        return context.columnView(requireNotNull(attribute)).get(index);
    }

    @Override
    public <T> T value(IndexedAttribute<T> attribute) {
        return (T) context.columnsView().get(attribute.headerIndex()).get(index);
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
    public boolean equals(Object arg) {
        if (this == arg) {
            return true;
        } else if (arg instanceof Line) {
            final var argLine = (Line) arg;
            return index() == argLine.index() && context().equals(argLine.context());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        final var header = context().headerView();
        return "index = " + index() + ", " + range(0, header.size())
                .mapToObj(i -> header.get(i).name() + " = " + value(header.get(i)).toString())
                .reduce("", (a, b) -> {
                    if (!a.isBlank()) {
                        return a + ", " + b;
                    }
                    return b;
                });
    }

    @Override
    public Tree toTree() {
        final var perspective = perspective(Line.class.getSimpleName());
        perspective.withProperty(INDEX.value(), "" + index);
        context.headerView().forEach(attribute -> {
            final var value = context.columnView(attribute).get(index);
            final Tree domValue;
            if (value == null) {
                domValue = perspective("");
            } else {
                if (value instanceof Domable) {
                    domValue = ((Domable) value).toTree();
                } else {
                    domValue = perspective(value.toString());
                }
            }
            final var valuePerspective = perspective(VALUE.value());
            valuePerspective.withProperty(TYPE.value(), attribute.name());
            valuePerspective.withChild(perspective(CONTENT.value()).withChild(domValue));
            perspective.withChild(valuePerspective);
        });
        return perspective;
    }

    @Override
    public int hashCode() {
        return Thing.hashCode(index(), context());
    }

}
