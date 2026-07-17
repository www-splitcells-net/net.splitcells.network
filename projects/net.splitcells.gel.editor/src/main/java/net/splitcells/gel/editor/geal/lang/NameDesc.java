/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.lang;

import lombok.Getter;
import net.splitcells.gel.editor.lang.SourceCodeQuote;

import java.util.regex.Pattern;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;

public final class NameDesc implements ExpressionDesc {
    public static NameDesc nameDescForTest(String value) {
        return new NameDesc(value, emptySourceCodeQuote());
    }

    public static NameDesc nameDesc(String value, SourceCodeQuote sourceCodeQuote) {
        return new NameDesc(value, sourceCodeQuote);
    }

    private static final Pattern PATTERN = Pattern.compile("[a-zA-Z_]++[a-zA-Z0-9_]*");
    @Getter private final String value;
    @Getter private final SourceCodeQuote sourceCodeQuote;

    private NameDesc(String argValue, SourceCodeQuote argSourceCodeQuote) {
        if (!PATTERN.matcher(argValue).matches()) {
            throw execException(tree("The name is invalid.").withProperty("value", argValue)
                    .withProperty("source code quote", argSourceCodeQuote.toString()));
        }
        value = argValue;
        sourceCodeQuote = argSourceCodeQuote;
    }
}
