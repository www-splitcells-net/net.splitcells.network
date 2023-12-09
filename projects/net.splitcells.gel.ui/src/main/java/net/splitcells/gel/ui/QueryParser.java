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

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.lang.perspective.antlr4.DenParser;
import net.splitcells.dem.lang.perspective.antlr4.DenParserBaseVisitor;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.type.ForAll;
import net.splitcells.gel.data.assignment.Assignments;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.object.Discoverable.NO_CONTEXT;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAll.FOR_ALL_NAME;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.ui.RaterParser.parseRater;

public class QueryParser extends DenParserBaseVisitor<Result<Query, Perspective>> {

    public static Result<Query, Perspective> parseQuery(DenParser.Source_unitContext sourceUnit
            , Assignments assignments) {
        final var parser = new QueryParser(assignments);
        parser.visitSource_unit(sourceUnit);
        return parser.nextConstraint;
    }

    private final Query parentConstraint;
    private final Assignments assignments;
    private Result<Query, Perspective> nextConstraint = result();

    private QueryParser(Assignments assignmentsArg) {
        assignments = assignmentsArg;
        parentConstraint = query(forAll(Optional.of(NO_CONTEXT)));
    }

    private QueryParser(Assignments assignmentsArg, Query parentConstraintArg) {
        assignments = assignmentsArg;
        parentConstraint = parentConstraintArg;
    }

    @Override
    public Result<Query, Perspective> visitVariable_definition(DenParser.Variable_definitionContext ctx) {
        if (ctx.Name().getText().equals("constraints")) {
            visitFunction_call(ctx.function_call());
        }
        return nextConstraint;
    }

    @Override
    public Result<Query, Perspective> visitAccess(DenParser.AccessContext access) {
        nextConstraint = parseQuery(access.Name().getText(), access.function_call_arguments());
        if (access.access() != null) {
            final var childConstraintParser = new QueryParser(assignments, nextConstraint.value().orElseThrow());
            childConstraintParser.visitAccess(access.access());
        }
        return nextConstraint;
    }

    private Result<Query, Perspective> parseQuery(String constraintType, DenParser.Function_call_argumentsContext arguments) {
        final Result<Query, Perspective> parsedConstraint = result();
        if (constraintType.equals("forAll")) {
            if (arguments.function_call_arguments_element() != null) {
                return parsedConstraint.withErrorMessage(perspective("ForAll does not support arguments: " + arguments.getText()));
            }
            parsedConstraint.withValue(parentConstraint.forAll());
        } else if (constraintType.equals("forEach")) {
            if (arguments.function_call_arguments_element() != null
                    && arguments.function_call_arguments_next().isEmpty()) {
                // TODO FIX
                if (arguments.function_call_arguments_element().function_call() != null
                        && !arguments.function_call_arguments_element().function_call().isEmpty()) {
                    return parsedConstraint.withErrorMessage(perspective("Function call arguments are not supported for "
                            + FOR_ALL_NAME
                            + " constraint: "
                            + arguments.getText()));
                }
                final var attributeName = arguments
                        .function_call_arguments_element()
                        .Name()
                        .getText();
                final var attributeMatches = assignments.headerView().stream()
                        .filter(da -> da.name().equals(attributeName))
                        .collect(toList());
                if (attributeMatches.size() != 1) {
                    return parsedConstraint.withErrorMessage(perspective("For each constraint argument only exact one attribute match is allowed.")
                            .withProperty("constraint type", constraintType)
                            .withProperty("attributes matches", attributeMatches.toString())
                            .withProperty("searched attribute name", attributeName));
                }
                parsedConstraint.withValue(parentConstraint.forAll(attributeMatches.get(0)));
                return parsedConstraint;
            }
            if (!arguments.function_call_arguments_next().isEmpty()) {
                return parsedConstraint
                        .withErrorMessage(perspective("ForEach does not support multiple arguments: "
                                + arguments.getText()));
            }
            return parsedConstraint.withErrorMessage(perspective("Invalid program state."));
        } else if (constraintType.equals("then")) {
            if (arguments.function_call_arguments_element() == null) {
                return parsedConstraint
                        .withErrorMessage(perspective("Then constraint requires at least one argument: "
                                + arguments.getText()));
            }
            if (arguments.function_call_arguments_element().function_call() != null) {
                return parsedConstraint.withValue
                        (parentConstraint.then
                                (parseRater(arguments.function_call_arguments_element().function_call()
                                        , assignments)));
            }
            parsedConstraint.withErrorMessage(perspective("Could not parse argument of then constraint: "
                    + arguments.getText()));
        } else {
            parsedConstraint.withValue(parentConstraint.constraint(constraintType, list(), list()));
        }
        return parsedConstraint;
    }

    @Override
    public Result<Query, Perspective> visitFunction_call(DenParser.Function_callContext functionCall) {
        nextConstraint = parseQuery(functionCall.Name().getText()
                , functionCall.function_call_arguments());
        if (functionCall.access() != null) {
            final var childConstraintParser = new QueryParser(assignments, nextConstraint.value().orElseThrow());
            childConstraintParser.visitAccess(functionCall.access());
        }
        return nextConstraint;
    }

    @Override
    public Result<Query, Perspective> visitStatement(DenParser.StatementContext statement) {
        if (statement.variable_definition() != null) {
            return visitVariable_definition(statement.variable_definition());
        } else if (statement.function_call() != null
                && statement.function_call().Name().getText().equals("constraints")) {
            if (statement.function_call().access().isEmpty()) {
                return nextConstraint
                        .withErrorMessage(perspective("Empty constraint is not allowed: " + statement.getText()));
            }
            if (statement.function_call().access() != null) {
                final var childConstraintParser = new QueryParser(assignments, parentConstraint);
                childConstraintParser.visitAccess(statement.function_call().access());
            }
            return nextConstraint;
        }
        return nextConstraint;
    }
}
