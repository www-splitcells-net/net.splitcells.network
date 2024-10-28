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
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.antlr4.DenParser;
import net.splitcells.dem.lang.tree.antlr4.DenParserBaseVisitor;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.view.attribute.Attribute;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.object.Discoverable.NO_CONTEXT;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAll.FOR_ALL_NAME;
import static net.splitcells.gel.constraint.type.ForAlls.*;
import static net.splitcells.gel.constraint.type.Then.THEN_NAME;
import static net.splitcells.gel.ui.RaterParser.parseRater;

public class QueryParser extends DenParserBaseVisitor<Result<Query, Tree>> {

    public static Result<Query, Tree> parseQuery(DenParser.Source_unitContext sourceUnit
            , Assignments assignments) {
        final var parser = new QueryParser(assignments);
        parser.visitSource_unit(sourceUnit);
        return parser.nextConstraint;
    }

    private final Query parentConstraint;
    private final Assignments assignments;
    private Result<Query, Tree> nextConstraint = result();

    private QueryParser(Assignments assignmentsArg) {
        assignments = assignmentsArg;
        parentConstraint = query(forAll(Optional.of(NO_CONTEXT)));
    }

    private QueryParser(Assignments assignmentsArg, Query parentConstraintArg) {
        assignments = assignmentsArg;
        parentConstraint = parentConstraintArg;
    }

    @Override
    public Result<Query, Tree> visitVariable_definition(DenParser.Variable_definitionContext ctx) {
        if (ctx.Name().getText().equals("constraints")) {
            visitFunction_call(ctx.function_call());
        }
        return nextConstraint;
    }

    @Override
    public Result<Query, Tree> visitAccess(DenParser.AccessContext access) {
        nextConstraint = parseQuery(access.Name().getText(), access.function_call_arguments());
        if (access.access() != null) {
            final var childConstraintParser = new QueryParser(assignments, nextConstraint.value().orElseThrow());
            final var intermediate = childConstraintParser.visitAccess(access.access());
            nextConstraint.errorMessages().withAppended(intermediate.errorMessages());
        }
        return nextConstraint;
    }

    private Result<Query, Tree> parseQuery(String constraintType, DenParser.Function_call_argumentsContext arguments) {
        final Result<Query, Tree> parsedConstraint = result();
        if (constraintType.equals(FOR_ALL_NAME)) {
            if (arguments.function_call_arguments_element() != null) {
                return parsedConstraint.withErrorMessage(tree("ForAll does not support arguments: " + arguments.getText()));
            }
            parsedConstraint.withValue(parentConstraint.forAll());
        } else if (constraintType.equals(FOR_EACH_NAME)) {
            if (arguments.function_call_arguments_element() != null
                    && arguments.function_call_arguments_next().isEmpty()) {
                // TODO FIX
                if (arguments.function_call_arguments_element().function_call() != null
                        && !arguments.function_call_arguments_element().function_call().isEmpty()) {
                    return parsedConstraint.withErrorMessage(tree("Function call arguments are not supported for "
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
                if (attributeMatches.isEmpty()) {
                    return parsedConstraint.withErrorMessage(tree("Attribute for constraint argument not found.")
                            .withProperty("constraint type", constraintType)
                            .withProperty("not found attribute name", attributeName));
                }
                if (attributeMatches.size() != 1) {
                    return parsedConstraint.withErrorMessage(tree("For each constraint argument only exact one attribute match is allowed, but multiple were found.")
                            .withProperty("constraint type", constraintType)
                            .withProperty("attributes matches", attributeMatches.toString())
                            .withProperty("searched attribute name", attributeName));
                }
                parsedConstraint.withValue(parentConstraint.forAll(attributeMatches.get(0)));
                return parsedConstraint;
            }
            if (!arguments.function_call_arguments_next().isEmpty()) {
                return parsedConstraint
                        .withErrorMessage(tree("ForEach does not support multiple arguments: "
                                + arguments.getText()));
            }
            return parsedConstraint.withErrorMessage(tree("Invalid program state."));
        } else if (constraintType.equals(FOR_ALL_COMBINATIONS_OF)) {
            if (arguments.function_call_arguments_element() != null) {
                if (arguments.function_call_arguments_element().function_call() != null
                        && !arguments.function_call_arguments_element().function_call().isEmpty()) {
                    return parsedConstraint.withErrorMessage(tree("Function call arguments are not supported for "
                            + FOR_ALL_COMBINATIONS_OF
                            + " constraint: "
                            + arguments.getText()));
                }
                final var attributeMatches = parseAttributes(arguments);
                if (attributeMatches.isEmpty()) {
                    return parsedConstraint.withErrorMessage(tree("Attribute for constraint argument not found.")
                            .withProperty("constraint type", constraintType)
                            .withProperty("arguments", arguments.getText()));
                }
                parsedConstraint.withValue(parentConstraint.forAllCombinationsOf(attributeMatches.get()));
                return parsedConstraint;
            }
            return parsedConstraint.withErrorMessage(tree(FOR_ALL_COMBINATIONS_OF + " constraint type required arguments, but has none."));
        } else if (constraintType.equals(THEN_NAME)) {
            if (arguments.function_call_arguments_element() == null) {
                return parsedConstraint
                        .withErrorMessage(tree("Then constraint requires at least one argument: "
                                + arguments.getText()));
            }
            if (arguments.function_call_arguments_element().function_call() != null) {
                final var rater = parseRater(arguments.function_call_arguments_element().function_call(), assignments);
                if (rater.defective()) {
                    parsedConstraint.errorMessages().withAppended(rater.errorMessages());
                    return parsedConstraint;
                }
                return parsedConstraint.withValue(parentConstraint.then(rater.value().orElseThrow()));
            }
            parsedConstraint.withErrorMessage(tree("Could not parse argument of then constraint: "
                    + arguments.getText()));
        } else {
            return parentConstraint.constraintResult(constraintType, list(), list());
        }
        return parsedConstraint;
    }

    private Optional<List<Attribute<?>>> parseAttributes(DenParser.Function_call_argumentsContext arguments) {
        final List<Attribute<?>> parsedAttributes = list();
        final var attributeName = arguments
                .function_call_arguments_element()
                .Name()
                .getText();
        final var firstAttribute = parseAttribute(attributeName);
        if (firstAttribute.isEmpty()) {
            return Optional.empty();
        }
        parsedAttributes.add(firstAttribute.get());
        for (final var nextArg : arguments.function_call_arguments_next()) {
            final var nextAttribute = parseAttribute(nextArg.function_call_arguments_element().Name().getText());
            if (nextAttribute.isEmpty()) {
                return Optional.empty();
            }
            parsedAttributes.add(nextAttribute.get());
        }
        return Optional.of(parsedAttributes);
    }

    private Optional<Attribute<?>> parseAttribute(String name) {
        final var attributeMatches = assignments.headerView().stream()
                .filter(da -> da.name().equals(name))
                .map(e -> (Attribute<? extends Object>) e)
                .collect(toList());
        if (attributeMatches.size() != 1) {
            nextConstraint.withErrorMessage(tree("Could not find attribute by name.")
                    .withProperty("name", name));
            return Optional.empty();
        }
        return Optional.of(attributeMatches.get(0));
    }

    @Override
    public Result<Query, Tree> visitFunction_call(DenParser.Function_callContext functionCall) {
        nextConstraint = parseQuery(functionCall.Name().getText()
                , functionCall.function_call_arguments());
        if (functionCall.access() != null && nextConstraint.value().isPresent()) {
            final var childConstraintParser = new QueryParser(assignments, nextConstraint.value().orElseThrow());
            final var intermediate = childConstraintParser.visitAccess(functionCall.access());
            nextConstraint.errorMessages().withAppended(intermediate.errorMessages());
        }
        return nextConstraint;
    }

    @Override
    public Result<Query, Tree> visitStatement(DenParser.StatementContext statement) {
        if (statement.variable_definition() != null) {
            return visitVariable_definition(statement.variable_definition());
        } else if (statement.function_call() != null
                && statement.function_call().Name().getText().equals("constraints")) {
            if (statement.function_call().access().isEmpty()) {
                return nextConstraint
                        .withErrorMessage(tree("Empty constraint is not allowed: " + statement.getText()));
            }
            if (statement.function_call().access() != null) {
                final var childConstraintParser = new QueryParser(assignments, parentConstraint);
                final var intermediate = childConstraintParser.visitAccess(statement.function_call().access());
                nextConstraint.errorMessages().withAppended(intermediate.errorMessages());
            }
            return nextConstraint;
        }
        return nextConstraint;
    }
}
