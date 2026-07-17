/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.parser;

import net.splitcells.dem.source.geal.GealParser;
import net.splitcells.gel.editor.geal.lang.ExpressionDesc;

import static java.lang.Integer.parseInt;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.editor.geal.lang.IntegerDesc.integerDesc;
import static net.splitcells.gel.editor.geal.lang.StringDesc.stringDesc;
import static net.splitcells.gel.editor.geal.parser.FunctionCallParser.parseFunctionCall;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.sourceCodeQuote;

public class ExpressionParser extends net.splitcells.dem.source.geal.GealParserBaseVisitor<ExpressionDesc> {
    public static ExpressionDesc parseExpression(GealParser.ExpressionContext arg) {
        return new ExpressionParser().visitExpression(arg);
    }

    @Override
    public ExpressionDesc visitExpression(GealParser.ExpressionContext ctx) {
        if (ctx.function_call() != null) {
            return parseFunctionCall(ctx.function_call());
        } else if (ctx.Integer() != null) {
            return integerDesc(parseInt(ctx.Integer().getText()), sourceCodeQuote(ctx.Integer()));
        } else if (ctx.String() != null) {
            final var text = ctx.String().getText();
            return stringDesc(text.substring(1, text.length() - 1), sourceCodeQuote(ctx.String()));
        } else {
            throw execException(tree("Given an unknown expression type.")
                    .withProperty("Expression", ctx.getText()));
        }
    }
}
