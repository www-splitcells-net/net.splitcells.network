/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.ui.editor.nocode;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenParserBaseVisitor;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.editor.lang.*;
import net.splitcells.gel.ui.SolutionParameters;
import net.splitcells.gel.ui.no.code.editor.NoCodeQueryParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.gel.editor.lang.ReferenceDescription.referenceDescription;

public class NoCodeEditorLangParser extends NoCodeDenParserBaseVisitor<Result<SolutionDescription, Tree>> {
    private static final String ATTRIBUTE = "attribute";
    private static final String CONSTRAINTS = "constraints";
    private static final String CONTENT = "content";
    private static final String DEMANDS = "demands";
    private static final String TABLE = "table";
    private static final String NAME = "name";
    private static final String SOLUTION = "solution";
    private static final String SUPPLIES = "supplies";

    private Result<SolutionDescription, Tree> noCodeEditorLangParsing(String arg) {
        final var lexer = new net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenParser(new CommonTokenStream(lexer));
        final List<Tree> parsingErrors = list();
        parser.addErrorListener(new BaseErrorListener() {
            // Ensures, that error messages are not hidden.
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                    throws ParseCancellationException {
                if (offendingSymbol instanceof CommonToken) {
                    final var token = (CommonToken) offendingSymbol;
                    parsingErrors.add(tree("Could not parse problem definition:")
                            .withProperty("line", "" + line)
                            .withProperty("column", "" + charPositionInLine)
                            .withProperty("invalid text", "`" + token.toString(recognizer) + "`")
                            .withProperty("invalid token", "`" + token.getText() + "`")
                            .withProperty("error", msg));
                } else {
                    parsingErrors.add(tree("Could not parse problem definition:")
                            .withProperty("line", "" + line)
                            .withProperty("column", "" + charPositionInLine)
                            .withProperty("invalid text", "`" + offendingSymbol.toString() + "`")
                            .withProperty("error", msg));
                }
            }
        });
        final var parsedProblem = new NoCodeEditorLangParser().visitSource_unit(parser.source_unit());
        parsedProblem.errorMessages().withAppended(parsingErrors);
        return parsedProblem;
    }

    private Optional<String> name = Optional.empty();
    private List<AttributeDescription> attributes = list();
    private Optional<TableDescription> demands = Optional.empty();
    private Optional<TableDescription> supplies = Optional.empty();
    private List<ConstraintDescription> constraints = list();
    private Result<SolutionDescription, Tree> result = result();
    private List<ReferenceDescription<AttributeDescription>> columnAttributesForOutputFormat = list();
    private List<ReferenceDescription<AttributeDescription>> rowAttributesForOutputFormat = list();

    private NoCodeEditorLangParser() {

    }

    /**
     * Nothing needs to be done for variable access for {@link #CONSTRAINTS},
     * as this is handled by {@link NoCodeQueryParser}.
     *
     * @param ctx the parse tree
     * @return
     */
    @Override
    public Result<SolutionDescription, Tree> visitVariable_access(net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenParser.Variable_accessContext ctx) {
        final var referencedName = ctx.variable_reference().Name().getText();
        if (SOLUTION.equals(referencedName)) {
            if (ctx.function_call().size() > 1) {
                result.withErrorMessage(tree("Variable access for " + SOLUTION + " is only supported for 1 function call, but more where given.")
                        .withProperty("variable access", ctx.getText()));
            }
            final var functionCallname = ctx.function_call(0).function_call_name().string_value().getText();
            if ("columnAttributesForOutputFormat".equals(functionCallname)) {
                ctx.function_call(0).function_call_argument()
                        .forEach(arg -> columnAttributesForOutputFormat
                                .add(referenceDescription(arg.value().variable_reference().Name().getText(), AttributeDescription.class)));
            } else if ("rowAttributesForOutputFormat".equals(functionCallname)) {
                ctx.function_call(0).function_call_argument()
                        .forEach(arg -> rowAttributesForOutputFormat
                                .add(referenceDescription(arg.value().variable_reference().Name().getText(), AttributeDescription.class)));
            }
        } else if (CONSTRAINTS.equals(referencedName)) {
            return result;
        } else {
            result.withErrorMessage(tree("Only function calls"));
        }
        return result;
    }
}
