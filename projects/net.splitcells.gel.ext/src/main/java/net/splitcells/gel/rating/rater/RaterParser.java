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
package net.splitcells.gel.rating.rater;

import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.lang.perspective.antlr4.DenParser;
import net.splitcells.dem.lang.perspective.antlr4.DenParserBaseVisitor;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.lib.AllSame;
import net.splitcells.gel.rating.rater.lib.HasSize;

import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.rating.rater.lib.AllSame.allSame;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;

public class RaterParser extends DenParserBaseVisitor<Rater> {
    public static Rater parseRater(DenParser.Function_callContext functionCall, Assignments assignments) {
        return new RaterParser(assignments).visitFunction_call(functionCall);
    }

    private final Assignments assignments;

    private RaterParser(Assignments assignmentsArg) {
        assignments = assignmentsArg;
    }

    @Override
    public Rater visitFunction_call(DenParser.Function_callContext functionCall) {
        if (functionCall.Name().getText().equals("hasSize")) {
            if (functionCall.function_call_arguments().function_call_arguments_element().Integer() != null) {
                final int argument = Integers.parse(functionCall
                        .function_call_arguments()
                        .function_call_arguments_element()
                        .Integer()
                        .getText());
                return hasSize(argument);
            }
        } else if (functionCall.Name().getText().equals("allSame")) {
            final var firstArgument = functionCall.function_call_arguments().function_call_arguments_element();
            if (firstArgument.Name() != null) {
                return allSame(assignments.attributeByName(firstArgument.Name().getText()));

            }
        }
        throw executionException("Unknown rater function: " + functionCall.getText());
    }
}
