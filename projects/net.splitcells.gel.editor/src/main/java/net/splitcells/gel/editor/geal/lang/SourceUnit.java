/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.lang;

import lombok.Getter;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.editor.lang.SourceCodeQuotation;
import net.splitcells.gel.editor.lang.SourceCodeQuote;

import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;

public class SourceUnit implements SourceCodeQuotation {
    public static SourceUnit sourceUnitForTest(List<StatementDesc> statements) {
        return new SourceUnit(statements, emptySourceCodeQuote());
    }

    public static SourceUnit sourceUnit(List<StatementDesc> statements, SourceCodeQuote quote) {
        return new SourceUnit(statements, quote);
    }

    @Getter private final List<StatementDesc> statements;
    @Getter private final SourceCodeQuote sourceCodeQuote;

    private SourceUnit(List<StatementDesc> argStatements, SourceCodeQuote quote) {
        statements = argStatements;
        sourceCodeQuote = quote;
    }
}
