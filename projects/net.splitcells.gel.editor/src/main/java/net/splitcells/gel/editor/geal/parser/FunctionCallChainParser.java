/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.parser;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.source.geal.GealParser;
import net.splitcells.gel.editor.geal.lang.FunctionCallChainDesc;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.lang.SourceCodeQuote;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.editor.geal.parser.ExpressionParser.parseExpression;
import static net.splitcells.gel.editor.geal.parser.FunctionCallParser.parseFunctionCall;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.sourceCodeQuote;

public class FunctionCallChainParser extends net.splitcells.dem.source.geal.GealParserBaseVisitor<FunctionCallChainDesc> {
    public static FunctionCallChainDesc parseFunctionCallChain(GealParser.Function_call_chainContext arg) {
        return new FunctionCallChainParser().visitFunction_call_chain(arg);
    }

    private FunctionCallChainParser() {

    }

    @Override
    public FunctionCallChainDesc visitFunction_call_chain(GealParser.Function_call_chainContext ctx) {
        final var expression = parseExpression(ctx.expression());
        final List<FunctionCallDesc> chain = list();
        if (ctx.function_call() != null) {
            ctx.function_call().forEach(f -> chain.add(parseFunctionCall(f)));
        }
        return FunctionCallChainDesc.functionCallChainDesc(expression, chain, sourceCodeQuote(ctx));
    }
}
