/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class EditorParser {
    public static final String STRING_TYPE = "String";
    public static final String INTEGER_TYPE = "Integer";
    public static final String TABLE_FUNCTION = "table";
    public static final String ATTRIBUTE_FUNCTION = "attribute";
    public static final String SOLUTION_FUNCTION = "solution";

    private EditorParser() {
        throw constructorIllegal();
    }
}
