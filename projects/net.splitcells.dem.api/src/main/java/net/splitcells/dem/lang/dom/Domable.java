/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang.dom;

import net.splitcells.dem.lang.tree.Tree;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * Perspective representations for instances of this interfaces can be created.
 * This can be seen as an alternative to String.
 */
public interface Domable {

    /**
     * @return Returns a {@link Tree} representing the current object.
     */
    Tree toTree();
}
