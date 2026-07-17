/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils;

import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.TreeI;

import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TABLE;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TEXT;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * This class provides helper functions in order to create FODS files,
 * which are table sheet files.
 */
public class FodsUtility {
    private FodsUtility() {
        throw constructorIllegal();
    }

    public static Tree tableCell(String cellContent) {
        return TreeI.tree("table-cell", FODS_TABLE)
                .withChild(TreeI.tree("p", FODS_TEXT).withChild(tree(cellContent)));
    }
}
