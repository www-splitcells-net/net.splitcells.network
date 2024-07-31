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
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.lang.perspective.antlr4.DenParser;
import net.splitcells.dem.lang.perspective.no.code.antlr4.NoCodeDenParser;
import net.splitcells.dem.lang.perspective.no.code.antlr4.NoCodeDenParserBaseVisitor;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.attribute.Attributes;
import net.splitcells.gel.ui.QueryParser;

import java.util.Optional;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.object.Discoverable.NO_CONTEXT;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAll.FOR_ALL_NAME;
import static net.splitcells.gel.constraint.type.ForAlls.*;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_ALL_COMBINATIONS_OF;
import static net.splitcells.gel.constraint.type.Then.THEN_NAME;
import static net.splitcells.gel.ui.RaterParser.parseRater;
import static net.splitcells.gel.ui.no.code.editor.NoCodeRaterParser.parseNoCodeRater;

public class NoCodeQueryParser extends NoCodeDenParserBaseVisitor<Result<Query, Perspective>> {
    private static final String AFFECTED_CONTENT = "affected content";

    public static Result<Query, Perspective> parseNoCodeQuery(NoCodeDenParser.Source_unitContext sourceUnit
            , Assignments assignments) {
        final var parser = new NoCodeQueryParser(assignments);
        parser.visitSource_unit(sourceUnit);
        return parser.nextConstraint;
    }

    private final Query parentConstraint;
    private final Assignments assignments;
    private Result<Query, Perspective> nextConstraint = result();

    private NoCodeQueryParser(Assignments assignmentsArg) {
        assignments = assignmentsArg;
        parentConstraint = query(forAll(Optional.of(NO_CONTEXT)));
    }

    private NoCodeQueryParser(Assignments assignmentsArg, Query parentConstraintArg) {
        assignments = assignmentsArg;
        parentConstraint = parentConstraintArg;
    }

    @Override
    public Result<Query, Perspective> visitVariable_definition(NoCodeDenParser.Variable_definitionContext ctx) {
        if (ctx.variable_definition_name().Name().getText().equals("constraints")) {
            nextConstraint.withValue(parentConstraint);
            visitFunction_call(ctx.variable_definition_value().value().function_call(), 0);
        }
        return nextConstraint;
    }

    @Override
    public Result<Query, Perspective> visitVariable_access(NoCodeDenParser.Variable_accessContext ctx) {
        if (ctx.variable_reference().Name().getText().equals("constraints")) {
            nextConstraint.withValue(parentConstraint);
            visitFunction_call(ctx.function_call(), 0);
        }
        return nextConstraint;
    }

    private Result<Query, Perspective> visitFunction_call(java.util.List<NoCodeDenParser.Function_callContext> functionCallChain
            , int currentIndex) {
        final var firstCall = functionCallChain.get(currentIndex);
        final var parseResults = parseQuery(firstCall.function_call_name().string_value().getText()
                , firstCall.function_call_argument()
                , firstCall);
        if (parseResults.value().isPresent()) {
            nextConstraint.withValue(parseResults.value().orElseThrow())
                    .errorMessages().withAppended(parseResults.errorMessages());
        } else {
            nextConstraint.errorMessages().withAppended(parseResults.errorMessages());
            return nextConstraint;
        }

        if (currentIndex < functionCallChain.size() - 1) {
            final var childConstraintParser = new NoCodeQueryParser(assignments, nextConstraint.value().orElseThrow());
            final var intermediate = childConstraintParser.visitFunction_call(functionCallChain, ++currentIndex);
            nextConstraint.withValue(intermediate.value().orElseThrow());
            nextConstraint.errorMessages().withAppended(intermediate.errorMessages());
        }
        return nextConstraint;
    }

    private Result<Query, Perspective> parseQuery(String constraintType, java.util.List<NoCodeDenParser.Function_call_argumentContext> arguments
            , NoCodeDenParser.Function_callContext constraintFunctionCall) {
        final Result<Query, Perspective> parsedConstraint = result();
        if (constraintType.equals(FOR_ALL_NAME)) {
            if (arguments != null && arguments.size() > 0) {
                return parsedConstraint.withErrorMessage(perspective("ForAll does not support arguments.")
                        .withProperty(AFFECTED_CONTENT
                                , arguments.stream().map(a -> a.getText()).toList().toString()));
            }
            parsedConstraint.withValue(parentConstraint.forAll());
        } else if (constraintType.equals(FOR_EACH_NAME)) {
            if (arguments == null || arguments.isEmpty()) {
                parsedConstraint.withValue(parentConstraint.forAll());
                return parsedConstraint;
            } else if (arguments.size() == 1) {
                parsedConstraint.withValue(parentConstraint.forAll
                        (assignments.attributeByName(arguments.get(0).value().variable_reference().Name().getText())));
                return parsedConstraint;
            } else if (arguments.size() > 1) {
                return parsedConstraint
                        .withErrorMessage(perspective("ForEach does not support multiple arguments.")
                                .withProperty(AFFECTED_CONTENT
                                        , arguments.stream().map(a -> a.getText()).toList().toString()));
            }
            return parsedConstraint.withErrorMessage(perspective("Could not parse ForEach, because of unknown reason."));
        } else if (constraintType.equals(FOR_ALL_COMBINATIONS_OF)) {
            if (arguments.size() < 2) {
                return parsedConstraint.withErrorMessage(perspective(FOR_ALL_COMBINATIONS_OF + " requires at least 2 arguments.")
                        .withProperty(AFFECTED_CONTENT
                                , arguments.stream().map(a -> a.getText()).toList().toString()));
            }
            final List<Attribute<? extends Object>> combination = list();
            for (final var a : arguments) {
                if (a.value().Function_call_var_arg() != null) {
                    continue;
                }
                final var attributeName = a.value().variable_reference().Name().getText();
                combination.add(assignments.attributeByName(attributeName));
            }
            parsedConstraint.withValue(parentConstraint.forAllCombinationsOf(combination));
            return parsedConstraint;
        } else if (constraintType.equals(THEN_NAME)) {
            if (arguments == null) {
                return parsedConstraint.withErrorMessage(perspective("Then constraint requires at least one argument, but argument list is null."));
            }
            if (arguments.isEmpty()) {
                return parsedConstraint
                        .withErrorMessage(perspective("Then constraint requires at least one argument.")
                                .withProperty(AFFECTED_CONTENT
                                        , arguments.stream().map(a -> a.getText()).toList().toString()));
            }
            if (arguments.size() > 1) {
                return parsedConstraint
                        .withErrorMessage(perspective("Then constraint only support one argument at maximum")
                                .withProperty(AFFECTED_CONTENT
                                        , arguments.stream().map(a -> a.getText()).toList().toString()));
            }

            final var rater = parseNoCodeRater(arguments.get(0).value().function_call(0), assignments);
            if (rater.defective()) {
                parsedConstraint.errorMessages().withAppended(rater.errorMessages());
                return parsedConstraint;
            }
            return parsedConstraint.withValue(parentConstraint.then(rater.value().orElseThrow()));
        } else {
            return parsedConstraint.withErrorMessage(perspective("Unknown constraint type")
                    .withProperty(AFFECTED_CONTENT, constraintFunctionCall.getText())
                    .withProperty("constraint type", constraintType));
        }
        return parsedConstraint;
    }
}
