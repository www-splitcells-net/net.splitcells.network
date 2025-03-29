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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenParser;
import net.splitcells.dem.source.den.DenParser;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.editor.lang.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Optional;
import java.util.stream.IntStream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.editor.lang.ConstraintDescription.constraintDescription;
import static net.splitcells.gel.editor.lang.FunctionCallDescription.functionCallDescription;
import static net.splitcells.gel.editor.lang.ReferenceDescription.referenceDescription;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.sourceCodeQuote;
import static net.splitcells.gel.editor.lang.StringDescription.stringDescription;
import static net.splitcells.gel.editor.solution.SolutionEditor.AFFECTED_CONTENT;
import static net.splitcells.gel.editor.solution.SolutionEditor.AFFECTED_CONTEXT;

@JavaLegacyArtifact
public class NoCodeConstraintLangParser {
    private NoCodeConstraintLangParser() {
    }

    public static Result<ConstraintDescription, Tree> parseConstraintDescription(java.util.List<NoCodeDenParser.Function_callContext> functionChain, ParserRuleContext parent) {
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
        final var argsParsing = parseArgumentDescriptions(functionCall.function_call_argument(), functionCall);
        if (argsParsing.defective()) {
            return constraintDescription.withErrorMessages(argsParsing);
        }
        return constraintDescription.withValue(constraintDescription(functionCallDescription(name, argsParsing.requiredValue(), sourceCodeQuote(functionCall)), sourceCodeQuote(functionCall)));
    }

    private static Result<FunctionCallDescription, Tree> parseFunctionCallDescription(NoCodeDenParser.Function_callContext functionCall) {
        final Result<FunctionCallDescription, Tree> functionCallDescription = result();
        final var name = functionCall.function_call_name().string_value().getText();
        final var argsParsing = parseArgumentDescriptions(functionCall.function_call_argument(), functionCall);
        if (argsParsing.defective()) {
            return functionCallDescription.withErrorMessages(argsParsing);
        }
        return functionCallDescription.withValue(functionCallDescription(name, argsParsing.requiredValue(), sourceCodeQuote(functionCall)));
    }

    private static Result<List<ArgumentDescription>, Tree> parseArgumentDescriptions(java.util.List<NoCodeDenParser.Function_call_argumentContext> arguments, ParserRuleContext parent) {
        final Result<List<ArgumentDescription>, Tree> argumentDescriptions = result();
        final net.splitcells.dem.data.set.list.List<ArgumentDescription> args = list();
        for (final var arg : arguments) {
            final var argVal = arg.value();
            if (argVal.string_value() != null) {
                args.add(stringDescription(argVal.string_value().getText(), sourceCodeQuote(argVal)));
            } else if (argVal.function_call() != null && !argVal.function_call().isEmpty()) {
                if (argVal.function_call().size() != 1) {
                    return argumentDescriptions.withErrorMessage(tree("Function chaining is not supported for constraint arguments.")
                            .withProperty(AFFECTED_CONTENT, argVal.getText())
                            .withProperty(AFFECTED_CONTEXT, parent.getText()));
                }
                final var functionArg = parseFunctionCallDescription(argVal.function_call().get(0));
                if (functionArg.defective()) {
                    return argumentDescriptions.withErrorMessages(functionArg);
                }
                args.add(functionArg.requiredValue());
            } else if (argVal.Undefined() != null || argVal.Function_call_var_arg() != null) {
                continue;
            } else if (argVal.variable_reference() != null) {
                args.add(referenceDescription(argVal.variable_reference().Name().getText(), AttributeDescription.class, sourceCodeQuote(argVal)));
            } else {
                return argumentDescriptions.withErrorMessage(tree("One function call argument for constraint is invalid.")
                        .withProperty(AFFECTED_CONTENT, arg.getText())
                        .withProperty(AFFECTED_CONTEXT, parent.getText()));
            }
        }
        return argumentDescriptions.withValue(args);
    }
}
