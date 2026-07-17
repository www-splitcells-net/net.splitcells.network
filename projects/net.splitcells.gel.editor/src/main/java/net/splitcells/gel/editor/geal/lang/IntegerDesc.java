/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.lang;

import lombok.Getter;
import net.splitcells.gel.editor.lang.SourceCodeQuote;

import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;

public final class IntegerDesc implements ExpressionDesc {

    public static IntegerDesc integerDescForTest(int value) {
        return integerDesc(value, emptySourceCodeQuote());
    }

    public static IntegerDesc integerDesc(int value, SourceCodeQuote sourceCodeQuote) {
        return new IntegerDesc(value, sourceCodeQuote);
    }

    private IntegerDesc(int argValue, SourceCodeQuote argSourceCodeQuote) {
        value = argValue;
        sourceCodeQuote = argSourceCodeQuote;
    }

    @Getter private final int value;
    @Getter private final SourceCodeQuote sourceCodeQuote;
}
