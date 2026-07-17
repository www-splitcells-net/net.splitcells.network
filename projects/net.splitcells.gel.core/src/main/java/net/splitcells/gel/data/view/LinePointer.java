/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.view;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.tree.TreeI.tree;

public interface LinePointer extends Domable {
    View context();

    int index();

    default Optional<Line> interpret() {
        return interpret(context());
    }

    @Deprecated
    Optional<Line> interpret(View context);

    @Override
    default Tree toTree() {
        final var dom = tree(LinePointer.class.getSimpleName());
        final var line = interpret();
        if (line.isPresent()) {
            dom.withChild(line.get().toTree());
        } else {
            dom.withProperty("index", index() + "");
        }
        return dom;
    }
}
