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
package net.splitcells.gel.ui.no.code.editor;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.lang.perspective.no.code.antlr4.NoCodeDenParser;
import net.splitcells.dem.lang.perspective.no.code.antlr4.NoCodeDenParserBaseVisitor;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.ui.ProblemParser;
import net.splitcells.gel.ui.SolutionParameters;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.problem.ProblemI.problem;
import static net.splitcells.gel.ui.QueryParser.parseQuery;

public class NoCodeProblemParser extends NoCodeDenParserBaseVisitor<Result<SolutionParameters, Perspective>> {
    private static final String NAME = "name";
    private static final String DEMANDS = "demands";
    private static final String SUPPLIES = "supplies";
    private static final String CONTENT = "content";

    public static Result<SolutionParameters, Perspective> parseNoCodeProblem(String arg) {
        final var lexer = new net.splitcells.dem.lang.perspective.no.code.antlr4.NoCodeDenLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.dem.lang.perspective.no.code.antlr4.NoCodeDenParser(new CommonTokenStream(lexer));
        final List<Perspective> parsingErrors = list();
        parser.addErrorListener(new BaseErrorListener() {
            // Ensures, that error messages are not hidden.
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                    throws ParseCancellationException {
                if (offendingSymbol instanceof CommonToken) {
                    final var token = (CommonToken) offendingSymbol;
                    parsingErrors.add(perspective("Could not parse problem definition:")
                            .withProperty("line", "" + line)
                            .withProperty("column", "" + charPositionInLine)
                            .withProperty("invalid text", "`" + token.toString(recognizer) + "`")
                            .withProperty("invalid token", "`" + token.getText() + "`")
                            .withProperty("error", msg));
                } else {
                    parsingErrors.add(perspective("Could not parse problem definition:")
                            .withProperty("line", "" + line)
                            .withProperty("column", "" + charPositionInLine)
                            .withProperty("invalid text", "`" + offendingSymbol.toString() + "`")
                            .withProperty("error", msg));
                }
            }
        });
        final var parsedProblem = new NoCodeProblemParser().visitSource_unit(parser.source_unit());
        parsedProblem.errorMessages().withAppended(parsingErrors);
        return parsedProblem;
    }

    private Result<SolutionParameters, Perspective> result = result();
    private Map<String, String> strings = map();
    private Map<String, Attribute<? extends Object>> attributes = map();
    private Map<String, Database> databases = map();

    private final SolutionParameters solutionParameters = SolutionParameters.solutionParameters();

    private NoCodeProblemParser() {

    }

    @Override
    public Result<SolutionParameters, Perspective> visitSource_unit(net.splitcells.dem.lang.perspective.no.code.antlr4.NoCodeDenParser.Source_unitContext sourceUnit) {
        visitChildren(sourceUnit);
        if (strings.containsKey(NAME) && databases.containsKey(DEMANDS) && databases.containsKey(SUPPLIES) && result.errorMessages().isEmpty()) {
            final var assignments = assignments(strings.get(NAME), databases.get(DEMANDS), databases.get(SUPPLIES));
        } else {
            if (!strings.containsKey(NAME)) {
                result.withErrorMessage(perspective("No name was defined via `name=\"[...]\"`."));
            }
            if (!databases.containsKey(DEMANDS)) {
                result.withErrorMessage(perspective("No demands was defined via `demands=\"[...]\"`."));
            }
            if (!databases.containsKey(SUPPLIES)) {
                result.withErrorMessage(perspective("No supplies was defined via `supplies=\"[...]\"`."));
            }
        }
        return result;
    }

    @Override
    public Result<SolutionParameters, Perspective> visitVariable_definition(NoCodeDenParser.Variable_definitionContext ctx) {
        final var variableName = ctx.variable_definition_name().Name().getText();
        if (strings.containsKey(variableName) || attributes.containsKey(variableName) || databases.containsKey(variableName)) {
            result.withErrorMessage(perspective("Variable with this name already exists.")
                    .withProperty(CONTENT, ctx.getText()));
            return null;
        }
        if (ctx.variable_definition_value() == null || ctx.variable_definition_value().value() == null) {
            result.withErrorMessage(perspective("Variable definition is missing a name.")
                    .withProperty(CONTENT, ctx.getText()));
            return null;
        }
        if (ctx.variable_definition_value() == null || ctx.variable_definition_value().value() == null) {
            result.withErrorMessage(perspective("Variable definition is missing a value.")
                    .withProperty(CONTENT, ctx.getText()));
            return null;
        }
        if (ctx.variable_definition_value().value().string_value() != null) {
            strings.put(variableName, ctx.variable_definition_value().value().string_value().getText());
            return null;
        }
        return null;
    }
}
