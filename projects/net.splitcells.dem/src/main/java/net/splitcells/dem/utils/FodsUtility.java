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
