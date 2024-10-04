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

import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenParser;
import net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenParserBaseVisitor;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.ui.Editor;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.rating.rater.lib.AllSame.ALL_SAME_NAME;
import static net.splitcells.gel.rating.rater.lib.AllSame.allSame;
import static net.splitcells.gel.rating.rater.lib.HasSize.HAS_SIZE_NAME;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.MINIMAL_DISTANCE_NAME;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.has_minimal_distance_of;

public class NoCodeRaterParser extends NoCodeDenParserBaseVisitor<Result<Rater, Tree>> {
    public static Result<Rater, Tree> parseNoCodeRater(NoCodeDenParser.Function_callContext functionCall, Editor editor) {
        return new NoCodeRaterParser(editor).visitFunction_call(functionCall);
    }

    private final Editor editor;

    private NoCodeRaterParser(Editor editorArg) {
        editor = editorArg;
    }

    @Override
    public Result<Rater, Tree> visitFunction_call(NoCodeDenParser.Function_callContext functionCall) {
        final Result<Rater, Tree> rater = Result.result();
        final var functionName = functionCall.function_call_name().string_value().getText();
        if (functionName.equals(HAS_SIZE_NAME)) {
            final int argument = Integers.parse(functionCall.function_call_argument()
                    .get(0)
                    .value()
                    .string_value()
                    .Integer()
                    .getText());
            return rater.withValue(hasSize(argument));
        } else if (functionName.equals(ALL_SAME_NAME)) {
            return rater.withValue(allSame(editor.attributeByVarName(functionCall.function_call_argument()
                    .get(0)
                    .value()
                    .string_value()
                    .getText())));
        } else if (functionName.equals(MINIMAL_DISTANCE_NAME)) {
            if (functionCall.function_call_argument() == null || functionCall.function_call_argument().isEmpty()) {
                return rater.withErrorMessage(tree("Rater `" + MINIMAL_DISTANCE_NAME + "` requires exactly 2 arguments, but has none.")
                        .withProperty("rater", functionCall.getText()));
            }
            if (functionCall.function_call_argument().size() != 2) {
                return rater.withErrorMessage(tree("Rater `" + MINIMAL_DISTANCE_NAME + "` requires exactly 2 arguments.")
                        .withProperty("rater", functionCall.getText()));
            }
            final var attribute = editor.attributeByVarName(functionCall.function_call_argument()
                    .get(0)
                    .value()
                    .variable_reference()
                    .Name()
                    .getText());

            final var minimumDistance = Double.parseDouble(functionCall.function_call_argument()
                    .get(1)
                    .value()
                    .string_value()
                    .Integer()
                    .getText());
            return rater.withValue(has_minimal_distance_of((Attribute<Integer>) attribute, minimumDistance));

        }
        return rater.withErrorMessage(tree("Unknown rater function: " + functionCall.getText()));
    }
}
