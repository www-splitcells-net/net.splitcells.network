/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.view;

import net.splitcells.dem.data.atom.Thing;

import java.util.Optional;


public class LinePointerI implements LinePointer {
    public static LinePointer linePointer(View context, int index) {
        return new LinePointerI(context, index);
    }

    private final View context;
    private final int index;

    private LinePointerI(View context, int index) {
        this.context = context;
        this.index = index;
    }

    @Override
    public View context() {
        return context;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public Optional<Line> interpret(View context) {
        if (context.rawLinesView().size() <= index) {
            return Optional.empty();
        }
        return Optional.ofNullable(context.rawLine(index));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof LinePointer other) {
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
