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
package net.splitcells.gel.constraint;

import net.splitcells.dem.lang.perspective.antlr4.DenParser;
import net.splitcells.dem.lang.perspective.antlr4.DenParserBaseVisitor;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.attribute.Attribute;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.constraint.type.ForAlls.forEach;

public class ConstraintParser extends DenParserBaseVisitor<Constraint> {

    public static Constraint parseConstraint(DenParser.Source_unitContext sourceUnit
            , Assignments assignments) {
        return new ConstraintParser(assignments).visitSource_unit(sourceUnit);
    }

    private Query constraints;
    private final Assignments assignments;

    private ConstraintParser(Assignments assignmentsArg) {
        assignments = assignmentsArg;
    }

    @Override
    public Constraint visitVariable_definition(DenParser.Variable_definitionContext ctx) {
        return visitChildren(ctx);
    }

    private Constraint parseConstraint(DenParser.Function_callContext functionCall) {
        final var constraintName = functionCall.Name().getText();
        if (!functionCall.access().isEmpty()) {
            throw executionException("Function chaining is not supported for constraint definition yet.");
        }
        if (constraintName.equals("forAll")) {
            if (functionCall.function_call_arguments().function_call_arguments_element() != null) {
                throw executionException("ForAll does not support arguments");
            }
            return forAll();
        } else if (constraintName.equals("forEach")) {
            if (functionCall.function_call_arguments().function_call_arguments_element() != null
                    && functionCall.function_call_arguments().function_call_arguments_next().isEmpty()) {
                if (!functionCall.function_call_arguments().function_call_arguments_element().function_call().isEmpty()) {
                    throw executionException("Function call argument are not supported for forEach constraint.");
                }
                final var attributeName = functionCall
                        .function_call_arguments()
                        .function_call_arguments_element()
                        .Name()
                        .getText();
                final var attributeMatches = assignments.headerView().stream()
                        .filter(da -> da.name().equals(attributeName))
                        .collect(toList());
                attributeMatches.requireSizeOf(1);
                return forEach(attributeMatches.get(0));
            }
            if (!functionCall.function_call_arguments().function_call_arguments_next().isEmpty()) {
                throw executionException("ForEach does not support multiple arguments.");
            }
            throw executionException("Invalid program state.");
        } else {
            throw executionException("Unknown constraint name: " + constraintName);
        }
    }
}
