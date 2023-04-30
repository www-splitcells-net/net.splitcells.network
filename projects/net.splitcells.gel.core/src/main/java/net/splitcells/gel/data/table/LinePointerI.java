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

import net.splitcells.dem.data.atom.Thing;

import java.util.Optional;


public class LinePointerI implements LinePointer {
    public static LinePointer linePointer(Table context, int index) {
        return new LinePointerI(context, index);
    }

    private final Table context;
    private final int index;

    private LinePointerI(Table context, int index) {
        this.context = context;
        this.index = index;
    }

    @Override
    public Table context() {
        return context;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public Optional<Line> interpret(Table context) {
        if (context.rawLinesView().size() <= index) {
            return Optional.empty();
        }
        return Optional.ofNullable(context.rawLine(index));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof LinePointer) {
            final var other = (LinePointer) arg;
            return context().equals(other.context()) && index() == other.index();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Thing.hashCode(index(), context());
    }
}
