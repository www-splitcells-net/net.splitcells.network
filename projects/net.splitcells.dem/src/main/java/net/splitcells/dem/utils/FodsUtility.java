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

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import org.w3c.dom.Element;

import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TABLE;
import static net.splitcells.dem.lang.namespace.NameSpaces.FODS_TEXT;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * This class provides helper functions in order to create FODS files,
 * which are table sheet files.
 */
@JavaLegacyArtifact
public class FodsUtility {
    private FodsUtility() {
        throw constructorIllegal();
    }

    public static Element tableCell(String cellContent) {
        final var tableCell = elementWithChildren(FODS_TABLE, "table-cell");
        final var cellContentElement = rElement(FODS_TEXT, "p");
        tableCell.appendChild(cellContentElement);
        cellContentElement.appendChild(textNode(cellContent));
        return tableCell;
    }
}
