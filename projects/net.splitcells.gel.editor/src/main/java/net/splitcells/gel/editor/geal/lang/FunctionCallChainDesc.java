/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.lang;

import lombok.Getter;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.editor.lang.SourceCodeQuote;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;

public final class FunctionCallChainDesc implements StatementDesc {
    public static FunctionCallChainDesc functionCallChainDescForTest(ExpressionDesc expression) {
        return functionCallChainDesc(expression, list(), emptySourceCodeQuote());
    }

    public static FunctionCallChainDesc functionCallChainDescForTest(ExpressionDesc expression
            , List<FunctionCallDesc> functionCalls) {
        return functionCallChainDesc(expression, functionCalls, emptySourceCodeQuote());
    }

    public static FunctionCallChainDesc functionCallChainDesc(ExpressionDesc expression
            , List<FunctionCallDesc> functionCalls
            , SourceCodeQuote quote) {
        return new FunctionCallChainDesc(expression, functionCalls, quote);
    }

    @Getter private final ExpressionDesc expression;
    @Getter private final List<FunctionCallDesc> functionCalls;
    @Getter private final SourceCodeQuote sourceCodeQuote;

    private FunctionCallChainDesc(ExpressionDesc argExpression, List<FunctionCallDesc> argFunctionCalls
            , SourceCodeQuote quote) {
        expression = argExpression;
        functionCalls = argFunctionCalls;
        sourceCodeQuote = quote;
    }
}
