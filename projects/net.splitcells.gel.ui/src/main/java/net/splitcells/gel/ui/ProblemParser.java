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
package net.splitcells.gel.ui;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.lang.perspective.antlr4.DenParser;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.dem.lang.perspective.antlr4.DenParserBaseVisitor;
import net.splitcells.gel.data.table.attribute.Attributes;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.ui.QueryParser.parseQuery;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.problem.ProblemI.problem;

/**
 * TODO Throwing exceptions works better, if one just ones to abort the process and return an error.
 * For this an {@link RuntimeException} is required, that states,
 * that it is an user facing message.
 * Create an error factory and manager, in order to avoid Java's throw statement.
 * This makes it easier to switch between different technologies (i.e. RuntimeException vs {@link System#exit(int)})
 * for error handling and focus on the error handling structure and API.
 * Note, the exceptions work best, if it used as an abort of the current processing context
 * (i.e. processing the request of a user and cleaning up all request specific data on error).
 */
public class ProblemParser extends DenParserBaseVisitor<Result<SolutionParameters, Perspective>> {

    private Optional<String> name = Optional.empty();

    private Optional<Database> demands = Optional.empty();
    private Optional<Database> supplies = Optional.empty();
    private final SolutionParameters solutionParameters = SolutionParameters.solutionParameters();

    private Result<SolutionParameters, Perspective> result = result();

    public static Result<SolutionParameters, Perspective> parseProblem(String arg) {
        final var lexer = new net.splitcells.dem.lang.perspective.antlr4.DenLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.dem.lang.perspective.antlr4.DenParser(new CommonTokenStream(lexer));
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
        final var parsedProblem = new ProblemParser().visitSource_unit(parser.source_unit());
        parsedProblem.errorMessages().withAppended(parsingErrors);
        return parsedProblem;
    }

    private ProblemParser() {

    }

    @Override
    public Result<SolutionParameters, Perspective> visitSource_unit(net.splitcells.dem.lang.perspective.antlr4.DenParser.Source_unitContext sourceUnit) {
        visitChildren(sourceUnit);
        if (name.isPresent() && demands.isPresent() && supplies.isPresent() && result.errorMessages().isEmpty()) {
            final var assignments = assignments(name.orElseThrow(), demands.orElseThrow(), supplies.orElseThrow());
            final var parsedQuery = parseQuery(sourceUnit, assignments);
            result.errorMessages().withAppended(parsedQuery.errorMessages());
            if (parsedQuery.value().isEmpty()) {
                return result;
            }
            solutionParameters.withProblem(problem(assignments, parsedQuery.value().orElseThrow().root().orElseThrow()));
            result.withValue(solutionParameters);
        } else {
            if (name.isEmpty()) {
                result.withErrorMessage(perspective("No name was defined via `name=\"[...]\"`."));
            }
            if (demands.isEmpty()) {
                result.withErrorMessage(perspective("No demands was defined via `demands=\"[...]\"`."));
            }
            if (supplies.isEmpty()) {
                result.withErrorMessage(perspective("No supplies was defined via `supplies=\"[...]\"`."));
            }
        }
        return result;
    }

    @Override
    public Result<SolutionParameters, Perspective> visitVariable_definition(net.splitcells.dem.lang.perspective.antlr4.DenParser.Variable_definitionContext ctx) {
        final var ctxName = ctx.Name().getText();
        if (ctxName.equals("name")) {
            if (name.isPresent()) {
                result.withErrorMessage(perspective("Names are not allowed to be defined multiple times."));
                return null;
            }
            name = Optional.of(ctxName);
        } else if (ctxName.equals("demands")) {
            if (demands.isPresent()) {
                result.withErrorMessage(perspective("Demands are not allowed to be defined multiple times."));
                return null;
            }
            final List<Attribute<? extends Object>> demandAttributes = list();
            final var firstDemandAttribute = ctx.block_statement().variable_definition();
            if (firstDemandAttribute != null) {
                final var parsedAttribute = Attributes.parseAttribute(firstDemandAttribute.Name().getText()
                        , firstDemandAttribute.function_call().Name().getText());
                final var parsedAttributeValue = parsedAttribute.value();
                if (parsedAttributeValue.isPresent()) {
                    demandAttributes.add(parsedAttributeValue.get());
                }
                result.errorMessages().withAppended(parsedAttribute.errorMessages());
            }
            final var additionalDemandAttributes = ctx.block_statement().statement_reversed();
            additionalDemandAttributes.forEach(da -> {
                        final var parsedAttribute = Attributes.parseAttribute(da.variable_definition().Name().getText()
                                , da.variable_definition().function_call().Name().getText());
                        if (parsedAttribute.value().isPresent()) {
                            demandAttributes.add(parsedAttribute.value().get());
                        }
                        result.errorMessages().withAppended(parsedAttribute.errorMessages());
                    }
            );
            demands = Optional.of(database(demandAttributes));
        } else if (ctxName.equals("supplies")) {
            if (supplies.isPresent()) {
                result.withErrorMessage(perspective("Supplies are not allowed to be defined multiple times."));
                return null;
            }
            final List<Attribute<? extends Object>> supplyAttributes = list();
            final var firstSupplyAttribute = ctx.block_statement().variable_definition();
            if (firstSupplyAttribute != null) {
                final var parsedAttribute = Attributes.parseAttribute(firstSupplyAttribute.Name().getText()
                        , firstSupplyAttribute.function_call().Name().getText());
                final var parsedAttributeValue = parsedAttribute.value();
                if (parsedAttributeValue.isPresent()) {
                    supplyAttributes.add(parsedAttributeValue.get());
                }
                result.errorMessages().withAppended(parsedAttribute.errorMessages());
            }
            final var additionalSupplyAttributes = ctx.block_statement().statement_reversed();
            additionalSupplyAttributes.forEach(sa -> {
                final var parsedAttribute = Attributes.parseAttribute(sa.variable_definition().Name().getText()
                        , sa.variable_definition().function_call().Name().getText());
                if (parsedAttribute.value().isPresent()) {
                    supplyAttributes.add(parsedAttribute.value().get());
                }
                result.errorMessages().withAppended(parsedAttribute.errorMessages());
            });
            supplies = Optional.of(database(supplyAttributes));
        }
        return null;
    }

    @Override
    public Result<SolutionParameters, Perspective> visitFunction_call(DenParser.Function_callContext ctx) {
        final var subjectName = ctx.Name().getText();
        if (ctx.Name().getText().equals("constraints")) {
            return null;
        }
        if (ctx.access() == null && ctx.function_call_arguments() == null) {
            result.withErrorMessage(perspective("Empty function calls are not supported at the top level.")
                    .withProperty("function call", ctx.getText()));
            return null;
        }
        if (ctx.access() == null && ctx.function_call_arguments() != null && !ctx.Name().getText().equals("constraints")) {
            result.withErrorMessage(perspective("Only the constraints function is allowed to be called at the top level without a subject.")
                    .withProperty("function call", ctx.getText()));
            return null;
        }
        final var functionName = ctx.access().Name().getText();
        if (functionName.equals("columnAttributesForOutputFormat")
                && subjectName.equals("solution")) {
            solutionParameters.columnAttributesForOutputFormat()
                    .add(ctx.access().function_call_arguments().function_call_arguments_element().Name().getText());
            ctx.access().function_call_arguments().function_call_arguments_next()
                    .forEach(e -> solutionParameters.columnAttributesForOutputFormat()
                            .add(e.getText()));
        } else if (functionName.equals("rowAttributesForOutputFormat")
                && subjectName.equals("solution")) {
            solutionParameters.rowAttributesForOutputFormat()
                    .add(ctx.access().function_call_arguments().function_call_arguments_element().Name().getText());
            ctx.access().function_call_arguments().function_call_arguments_next()
                    .forEach(e -> solutionParameters.rowAttributesForOutputFormat()
                            .add(e.function_call_arguments_element().Name().getText()));
        } else {
            result.withErrorMessage(perspective("There is an unknown top level function call.")
                    .withProperty("subject name", subjectName)
                    .withProperty("function name", functionName));
        }
        return null;
    }
}
