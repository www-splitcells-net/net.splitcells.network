/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.parser;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.source.geal.GealParser;
import net.splitcells.gel.editor.geal.lang.StatementDesc;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.editor.geal.parser.FunctionCallChainParser.parseFunctionCallChain;
import static net.splitcells.gel.editor.geal.parser.VariableDefinitionParser.parseVariableDefinition;

@JavaLegacy
public class StatementParser extends net.splitcells.dem.source.geal.GealParserBaseVisitor<StatementDesc> {

    public static StatementDesc parseStatement(GealParser.StatementContext statementContext) {
        return new StatementParser().visitStatement(statementContext);
    }

    private StatementParser() {
    }

    @Override
    public StatementDesc visitStatement(GealParser.StatementContext ctx) {
        if (ctx.variable_definition() != null) {
            return parseVariableDefinition(ctx.variable_definition());
        } else if (ctx.function_call_chain() != null) {
            return parseFunctionCallChain(ctx.function_call_chain());
        } else {
            throw execException(tree("The content type of statement is unknown.").withProperty("statement", ctx.getText()));
        }
    }
}
