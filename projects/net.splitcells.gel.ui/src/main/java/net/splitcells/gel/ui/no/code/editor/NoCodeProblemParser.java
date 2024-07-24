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
import net.splitcells.gel.ui.SolutionParameters;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.object.Discoverable.NO_CONTEXT;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.Attributes.parseAttribute;
import static net.splitcells.gel.problem.ProblemI.problem;
import static net.splitcells.gel.ui.no.code.editor.NoCodeQueryParser.parseNoCodeQuery;

/**
 * TODO There should only be one parser based on {@link Perspective} as input.
 * The {@link Perspective} would be produced via dedicated parsers for each grammar.
 * Thereby, duplicate problem parsing logic could be avoided.
 */
public class NoCodeProblemParser extends NoCodeDenParserBaseVisitor<Result<SolutionParameters, Perspective>> {
    private static final String ATTRIBUTE = "attribute";
    private static final String CONSTRAINTS = "constraints";
    private static final String CONTENT = "content";
    private static final String DEMANDS = "demands";
    private static final String DATABASE = "database";
    private static final String NAME = "name";
    private static final String SOLUTION = "solution";
    private static final String SUPPLIES = "supplies";


    public static Result<SolutionParameters, Perspective> parseNoCodeProblem(String arg) {
        return new NoCodeProblemParser().parseNoCodeProblemIntern(arg);
    }

    public static Map<String, String> parseNoCodeStrings(String arg) {
        final var parser = new NoCodeProblemParser();
        parser.parseNoCodeProblemIntern(arg);
        return parser.strings;
    }

    public static Map<String, Attribute<? extends Object>> parseNoCodeAttributes(String arg) {
        final var parser = new NoCodeProblemParser();
        parser.parseNoCodeProblemIntern(arg);
        return parser.attributes;
    }

    public static Map<String, Database> parseNoCodeDatabases(String arg) {
        final var parser = new NoCodeProblemParser();
        parser.parseNoCodeProblemIntern(arg);
        return parser.databases;
    }

    private final Result<SolutionParameters, Perspective> result = result();
    private final Map<String, String> strings = map();
    private final Map<String, Attribute<? extends Object>> attributes = map();
    private final Map<String, Database> databases = map();
    private NoCodeDenParser.Source_unitContext currentSourceUnit;

    private final SolutionParameters solutionParameters = SolutionParameters.solutionParameters();

    private NoCodeProblemParser() {

    }

    /**
     * Nothing needs to be done for variable access for {@link #CONSTRAINTS},
     * as this is handled by {@link NoCodeQueryParser}.
     *
     * @param ctx the parse tree
     * @return
     */
    @Override
    public Result<SolutionParameters, Perspective> visitVariable_access(NoCodeDenParser.Variable_accessContext ctx) {
        final var referencedName = ctx.variable_reference().Name().getText();
        if (SOLUTION.equals(referencedName)) {
            if (ctx.function_call().size() > 1) {
                result.withErrorMessage(perspective("Variable access for " + SOLUTION + " is only supported for 1 function call, but more where given.")
                        .withProperty("variable access", ctx.getText()));
            }
            final var functionCallname = ctx.function_call(0).function_call_name().string_value().getText();
            if ("columnAttributesForOutputFormat".equals(functionCallname)) {
                ctx.function_call(0).function_call_argument()
                        .forEach(arg -> solutionParameters
                                .columnAttributesForOutputFormat()
                                .add(arg.value().variable_reference().Name().getText()));
            } else if ("rowAttributesForOutputFormat".equals(functionCallname)) {
                ctx.function_call(0).function_call_argument()
                        .forEach(arg -> solutionParameters
                                .rowAttributesForOutputFormat()
                                .add(arg.value().variable_reference().Name().getText()));
            }
        } else if (CONSTRAINTS.equals(referencedName)) {
            return null;
        } else {
            result.withErrorMessage(perspective("Only function calls"));
        }
        return null;
    }

    private Result<SolutionParameters, Perspective> parseNoCodeProblemIntern(String arg) {
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
        final var parsedProblem = visitSource_unit(parser.source_unit());
        parsedProblem.errorMessages().withAppended(parsingErrors);
        return parsedProblem;
    }

    @Override
    public Result<SolutionParameters, Perspective> visitSource_unit(net.splitcells.dem.lang.perspective.no.code.antlr4.NoCodeDenParser.Source_unitContext sourceUnit) {
        currentSourceUnit = sourceUnit;
        visitChildren(sourceUnit);
        currentSourceUnit = null;
        return result;
    }

    @Override
    public Result<SolutionParameters, Perspective> visitVariable_definition(NoCodeDenParser.Variable_definitionContext ctx) {
        final var variableName = ctx.variable_definition_name().Name().getText();
        if (variableName.equals("constraints")) {
            return null;
        }
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
        } else if (ctx.variable_definition_value().value().function_call() != null) {
            if (ctx.variable_definition_value().value().function_call().size() < 1) {
                result.withErrorMessage(perspective("Variable definition with a function call chain as its value, needs at least one function call, but has none.")
                        .withProperty(CONTENT, ctx.getText()));
                return null;
            }
            final var functionCall = ctx.variable_definition_value().value().function_call().get(0);
            final var functionName = functionCall.function_call_name().string_value().getText();
            if (functionName.equals(SOLUTION)) {
                if (!variableName.equals(SOLUTION)) {
                    result.withErrorMessage(perspective("A solution can only be defined for the variable name solution.")
                            .withProperty("Given variable name", variableName)
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                if (functionCall.function_call_argument().size() != 3) {
                    result.withErrorMessage(perspective("A solution requires 3 arguments: variable name, demands, supplies")
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                final var assignments = assignments(functionCall.function_call_argument(0).value().string_value().getText()
                        , databases.get(functionCall.function_call_argument(1).value().variable_reference().Name().getText())
                        , databases.get(functionCall.function_call_argument(2).value().variable_reference().Name().getText()));
                final var parsedQuery = parseNoCodeQuery(currentSourceUnit, assignments);
                result.errorMessages().withAppended(parsedQuery.errorMessages());
                if (parsedQuery.defective()) {
                    parsedQuery.errorMessages().forEach(result::withErrorMessage);
                    return result;
                }
                solutionParameters.withProblem(problem(assignments, parsedQuery.value().orElseThrow().root().orElseThrow()));
                result.withValue(solutionParameters);
            } else if (functionName.equals(DATABASE)) {
                if (functionCall.function_call_argument().size() < 2) {
                    result.withErrorMessage(perspective("The function database requires at least arguments.")
                            .withProperty("Actual arguments given", functionCall.function_call_argument().toString())
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                final var databaseNameText = functionCall.function_call_argument(0);
                if (databaseNameText.value().string_value() == null) {
                    result.withErrorMessage(perspective("The database name (first argument) needs to be string value, but is not.")
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                final var databaseName = databaseNameText.value().string_value().getText();
                if (databases.containsKey(variableName)) {
                    result.withErrorMessage(perspective("A variable with this name already exists.")
                            .withProperty("variable name", variableName)
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                final List<Attribute<? extends Object>> databaseAttributes = list();
                for (int i = 1; i < functionCall.function_call_argument().size(); ++i) {
                    final var attributeText = functionCall.function_call_argument(i).value();
                    if (attributeText.Function_call_var_arg() != null) {
                        continue;
                    }
                    if (attributeText.variable_reference() == null) {
                        result.withErrorMessage(perspective("The database attributes needs to be attribute references, but are not.")
                                .withProperty("invalid attribute", attributeText.getText())
                                .withProperty(CONTENT, ctx.getText()));
                        return null;
                    }
                    if (!attributes.containsKey(attributeText.variable_reference().Name().getText())) {
                        result.withErrorMessage(perspective("A unknown variable is referenced via the namename.")
                                .withProperty("invalid attribute", attributeText.getText())
                                .withProperty(CONTENT, ctx.getText()));
                        return null;
                    }
                    databaseAttributes.add(attributes.get(attributeText.variable_reference().Name().getText()));
                }
                databases.put(variableName, database(databaseName, NO_CONTEXT, databaseAttributes));
                return null;
            } else if (functionName.equals(ATTRIBUTE)) {
                if (functionCall.function_call_argument().size() != 2) {
                    result.withErrorMessage(perspective("The function attribute requires exactly 2 arguments.")
                            .withProperty("Actual arguments given", functionCall.function_call_argument().toString())
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                final var attributeName = functionCall.function_call_argument(0);
                if (attributeName.value().string_value() == null) {
                    result.withErrorMessage(perspective("The attributes name (first argument) needs to be string value, but is not.")
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                final var attributeType = functionCall.function_call_argument(1);
                if (attributeType.value().string_value() == null) {
                    result.withErrorMessage(perspective("The attributes type (second argument) needs to be string value, but is not.")
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                final var attributeParsing = parseAttribute(attributeName.value().string_value().getText(), attributeType.value().string_value().getText());
                if (attributeParsing.defective()) {
                    result.errorMessages().addAll(attributeParsing.errorMessages());
                    return null;
                }
                final var newAttribute = attributeParsing.value().orElseThrow();
                attributes.put(newAttribute.name(), newAttribute);
                return null;
            } else {
                result.withErrorMessage(perspective("Unknown first function name in function chain for variable definition.")
                        .withProperty(CONTENT, ctx.getText()));
                return null;
            }
        } else if (ctx.variable_definition_value().value().variable_reference() != null) {
            result.withErrorMessage(perspective("Variable definition with a variable reference as its value is not supported.")
                    .withProperty(CONTENT, ctx.getText()));
            return null;
        } else {
            result.withErrorMessage(perspective("Variable definition is missing a value.")
                    .withProperty(CONTENT, ctx.getText()));
            return null;
        }
        return null;
    }
}
