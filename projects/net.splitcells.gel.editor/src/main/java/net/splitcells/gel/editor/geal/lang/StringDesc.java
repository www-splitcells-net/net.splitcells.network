/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.lang;

import lombok.Getter;
import net.splitcells.gel.editor.lang.SourceCodeQuote;

import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;

public final class StringDesc implements ExpressionDesc {
    public static StringDesc stringDescForTest(String value) {
        return new StringDesc(value, emptySourceCodeQuote());
    }

    public static StringDesc stringDesc(String value, SourceCodeQuote sourceCodeQuote) {
        return new StringDesc(value, sourceCodeQuote);
    }

    @Getter private final String value;
    @Getter private final SourceCodeQuote sourceCodeQuote;

    private StringDesc(String argValue, SourceCodeQuote argSourceCodeQuote) {
        value = argValue;
        sourceCodeQuote = argSourceCodeQuote;
    }
}
