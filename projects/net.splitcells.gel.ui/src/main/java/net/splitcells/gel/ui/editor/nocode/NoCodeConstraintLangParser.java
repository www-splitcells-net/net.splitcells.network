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
package net.splitcells.gel.ui.editor.nocode;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenParser;
import net.splitcells.dem.source.den.DenParser;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.editor.lang.ArgumentDescription;
import net.splitcells.gel.editor.lang.AttributeDescription;
import net.splitcells.gel.editor.lang.ConstraintDescription;
import net.splitcells.gel.editor.lang.FunctionCallDescription;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.editor.lang.ConstraintDescription.constraintDescription;
import static net.splitcells.gel.editor.lang.FunctionCallDescription.functionCallDescription;
import static net.splitcells.gel.editor.lang.ReferenceDescription.referenceDescription;
import static net.splitcells.gel.editor.lang.StringDescription.stringDescription;
import static net.splitcells.gel.editor.solution.SolutionEditor.AFFECTED_CONTENT;
import static net.splitcells.gel.editor.solution.SolutionEditor.AFFECTED_CONTEXT;

@JavaLegacyArtifact
public class NoCodeConstraintLangParser {
    private NoCodeConstraintLangParser() {
    }

    public static Result<ConstraintDescription, Tree> parseConstraintDescription(List<NoCodeDenParser.Function_callContext> functionChain, ParserRuleContext parent) {
        final Result<ConstraintDescription, Tree> constraintDescription = result();
        if (functionChain.isEmpty()) {
            return constraintDescription.withErrorMessage(tree("An empty function chain is invalid.")
                    .withProperty(AFFECTED_CONTENT, parent.getText()));
        }
        constraintDescription.withResult(parseConstraintDescription(functionChain.get(0)));
        if (constraintDescription.defective()) {
            return constraintDescription;
        }
        ConstraintDescription childConstraint = constraintDescription.requiredValue();
        for (int i = 1; i < functionChain.size(); ++i) {
            final var functionCall = functionChain.get(i);
            final var parsedChildConstraint = parseConstraintDescription(functionCall);
            constraintDescription.withErrorMessages(parsedChildConstraint);
            if (parsedChildConstraint.defective()) {
                return constraintDescription;
            }
            childConstraint.children().add(parsedChildConstraint.requiredValue());
            childConstraint = parsedChildConstraint.requiredValue();
        }
        return constraintDescription;
    }

    private static Result<ConstraintDescription, Tree> parseConstraintDescription(NoCodeDenParser.Function_callContext functionCall) {
        final Result<ConstraintDescription, Tree> constraintDescription = result();
        final var name = functionCall.function_call_name().string_value().getText();
        final net.splitcells.dem.data.set.list.List<ArgumentDescription> args = list();
        for (final var arg : functionCall.function_call_argument()) {
            final var argVal = arg.value();
            if (argVal.string_value() != null) {
                args.add(stringDescription(argVal.string_value().getText()));
            } else if (argVal.function_call() != null && !argVal.function_call().isEmpty()) {
                if (argVal.function_call().size() != 1) {
                    return constraintDescription.withErrorMessage(tree("Function chaining is not supported for constraint arguments.")
                            .withProperty(AFFECTED_CONTENT, argVal.getText())
                            .withProperty(AFFECTED_CONTEXT, functionCall.getText()));
                }
                final var functionArg = parseFunctionCallDescription(argVal.function_call().get(0));
                if (functionArg.defective()) {
                    return constraintDescription.withErrorMessages(functionArg);
                }
                args.add(functionArg.requiredValue());
            } else if (argVal.Undefined() != null) {
                continue;
            } else if (argVal.Function_call_var_arg() != null) {
                continue;
            } else if (argVal.variable_reference() != null) {
                args.add(referenceDescription(argVal.variable_reference().Name().getText(), AttributeDescription.class));
            } else {
                return constraintDescription.withErrorMessage(tree("One function call argument for constraint is invalid.")
                        .withProperty(AFFECTED_CONTENT, arg.getText())
                        .withProperty(AFFECTED_CONTEXT, functionCall.getText()));
            }
        }
        return constraintDescription.withValue(constraintDescription(functionCallDescription(name, args)));
    }

    private static Result<FunctionCallDescription, Tree> parseFunctionCallDescription(NoCodeDenParser.Function_callContext functionCall) {
        final Result<FunctionCallDescription, Tree> functionCallDescription = result();
        return functionCallDescription;
    }
}
