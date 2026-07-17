/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.lang;

import net.splitcells.gel.editor.lang.SourceCodeQuotation;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

public sealed interface ExpressionDesc extends SourceCodeQuotation permits FunctionCallDesc
        , IntegerDesc
        , NameDesc
        , StringDesc {
    default <T extends ExpressionDesc> T to(Class<T> clazz) {
        if (this.getClass().isAssignableFrom(clazz)) {
            return (T) this;
        }
        throw execException(tree("Incompatible types.")
                .withProperty("this type", getClass().toString())
                .withProperty("given type", clazz.toString()));
    }
}
