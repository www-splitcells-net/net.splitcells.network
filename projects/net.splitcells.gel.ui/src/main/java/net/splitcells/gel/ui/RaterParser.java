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

import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.lang.perspective.antlr4.DenParser;
import net.splitcells.dem.lang.perspective.antlr4.DenParserBaseVisitor;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.framework.Rater;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.rating.rater.lib.AllSame.ALL_SAME_NAME;
import static net.splitcells.gel.rating.rater.lib.AllSame.allSame;
import static net.splitcells.gel.rating.rater.lib.HasSize.HAS_SIZE_NAME;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.MINIMAL_DISTANCE_NAME;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.has_minimal_distance_of;

public class RaterParser extends DenParserBaseVisitor<Result<Rater, Perspective>> {
    public static Result<Rater, Perspective> parseRater(DenParser.Function_callContext functionCall, Assignments assignments) {
        return new RaterParser(assignments).visitFunction_call(functionCall);
    }

    private final Assignments assignments;

    private RaterParser(Assignments assignmentsArg) {
        assignments = assignmentsArg;
    }

    @Override
    public Result<Rater, Perspective> visitFunction_call(DenParser.Function_callContext functionCall) {
        final Result<Rater, Perspective> rater = Result.result();
        if (functionCall.Name().getText().equals(HAS_SIZE_NAME)) {
            if (functionCall.function_call_arguments().function_call_arguments_element().Integer() != null) {
                final int argument = Integers.parse(functionCall
                        .function_call_arguments()
                        .function_call_arguments_element()
                        .Integer()
                        .getText());
                return rater.withValue(hasSize(argument));
            }
        } else if (functionCall.Name().getText().equals(ALL_SAME_NAME)) {
            final var firstArgument = functionCall.function_call_arguments().function_call_arguments_element();
            if (firstArgument.Name() != null) {
                return rater.withValue(allSame(assignments.attributeByName(firstArgument.Name().getText())));

            }
        } else if (functionCall.Name().getText().equals(MINIMAL_DISTANCE_NAME)) {
            if (functionCall.function_call_arguments().function_call_arguments_element() == null) {
                return rater.withErrorMessage(perspective("Rater `" + MINIMAL_DISTANCE_NAME + "` requires exactly 2 arguments, but has none.")
                        .withProperty("rater", functionCall.getText()));
            }
            final var attribute = assignments.attributeByName(
                    functionCall.function_call_arguments().function_call_arguments_element().Name().getText());
            if (functionCall.function_call_arguments().function_call_arguments_next().size() != 1) {
                return rater.withErrorMessage(perspective("Rater `" + MINIMAL_DISTANCE_NAME + "` requires exactly 2 arguments.")
                        .withProperty("rater", functionCall.getText()));
            }
            final var minimumDistance = Double.parseDouble(functionCall
                    .function_call_arguments().function_call_arguments_next().get(0).function_call_arguments_element().getText());
            return rater.withValue(has_minimal_distance_of((Attribute<Integer>) attribute, minimumDistance));

        }
        return rater.withErrorMessage(perspective("Unknown rater function: " + functionCall.getText()));
    }
}
