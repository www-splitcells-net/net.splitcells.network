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
package net.splitcells.gel.ui.editor.code;

import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.source.den.DenParser;
import net.splitcells.dem.source.den.DenParserBaseVisitor;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.editor.lang.ArgumentDescription;
import net.splitcells.gel.editor.lang.ConstraintDescription;
import net.splitcells.gel.editor.lang.FunctionCallDescription;
import net.splitcells.gel.editor.lang.SourceCodeQuote;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.source.SourceUtils.getLine;
import static net.splitcells.dem.source.SourceUtils.root;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.editor.lang.ConstraintDescription.constraintDescription;
import static net.splitcells.gel.editor.lang.FunctionCallDescription.functionCallDescription;
import static net.splitcells.gel.editor.lang.IntegerDescription.integerDescription;
import static net.splitcells.gel.editor.lang.ReferenceDescription.referenceDescription;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.sourceCodeQuote;
import static net.splitcells.gel.editor.solution.SolutionEditor.AFFECTED_CONTENT;

/**
 * IDEA Logically this code should be inside {@link CodeEditorLangParser},
 * as the separation between the 2 only seem to create more complexity.
 */
public class CodeConstraintLangParser extends DenParserBaseVisitor<Result<List<ConstraintDescription>, Tree>> {
    public static Result<List<ConstraintDescription>, Tree> parseConstraints(DenParser.Source_unitContext sourceUnit) {
        final var parser = new CodeConstraintLangParser();
        parser.visitSource_unit(sourceUnit);
        return parser.constraints;
    }

    private final Result<List<ConstraintDescription>, Tree> constraints
            = Result.<List<ConstraintDescription>, Tree>result().withValue(list());

    private CodeConstraintLangParser() {

    }

    private static Result<FunctionCallDescription, Tree> parseFunctionCallDescription(DenParser.Function_callContext functionCall) {
        return parseFunctionCallDescription(functionCall.Name().getText(), functionCall.function_call_arguments());
    }


    private static Result<FunctionCallDescription, Tree> parseFunctionCallDescription(String name
            , DenParser.Function_call_argumentsContext arguments
    ) {
        final Result<FunctionCallDescription, Tree> parsedFunctionCallDescription = result();
        final List<ArgumentDescription> args = list();
        {
            final var firstArgument = arguments.function_call_arguments_element();
            if (firstArgument != null) {
                if (firstArgument.Integer() != null) {
                    args.add(integerDescription(Integers.parse(firstArgument.Integer().getText()), sourceCodeQuote(firstArgument)));
                } else if (firstArgument.Name() != null) {
                    args.add(referenceDescription(firstArgument.Name().getText(), Object.class, sourceCodeQuote(firstArgument)));
                } else if (firstArgument.function_call() != null) {
                    final var tmp = parseFunctionCallDescription(firstArgument.function_call());
                    if (tmp.defective()) {
                        return parsedFunctionCallDescription.withErrorMessages(tmp);
                    }
                    args.add(tmp.optionalValue().orElseThrow());
                } else {
                    return parsedFunctionCallDescription.withErrorMessage(tree("Unknown first function call argument syntax.")
                            .withProperty(AFFECTED_CONTENT, arguments.getText()));
                }
            }
        }
        for (final var argNext : arguments.function_call_arguments_next()) {
            final var argCurrent = argNext.function_call_arguments_element();
            if (argCurrent.Integer() != null) {
                args.add(integerDescription(Integers.parse(argCurrent.Integer().getText()), sourceCodeQuote(argCurrent)));
            } else if (argCurrent.Name() != null) {
                args.add(referenceDescription(argCurrent.Name().getText(), Object.class, sourceCodeQuote(argCurrent)));
            } else if (argCurrent.function_call() != null) {
                final var tmp = parseFunctionCallDescription(argCurrent.function_call());
                if (tmp.defective()) {
                    return parsedFunctionCallDescription.withErrorMessages(tmp);
                }
                args.add(tmp.optionalValue().orElseThrow());
            } else {
                return parsedFunctionCallDescription.withErrorMessage(tree("Unknown first function call argument syntax.")
                        .withProperty(AFFECTED_CONTENT, arguments.getText()));
            }
        }
        return parsedFunctionCallDescription.withValue(functionCallDescription(name, args, sourceCodeQuote(arguments)));
    }

    private static Result<ConstraintDescription, Tree> parseConstraintDescription(DenParser.Function_callContext functionCall) {
        return parseConstraintDescription(functionCall.Name().getText(), functionCall.function_call_arguments(), Optional.ofNullable(functionCall.access()));
    }

    private static Result<ConstraintDescription, Tree> parseConstraintDescription(String name
            , DenParser.Function_call_argumentsContext arguments
            , Optional<DenParser.AccessContext> child
    ) {
        final Result<ConstraintDescription, Tree> parsedConstraintDescription = result();
        final var parsedConstraintFunction = parseFunctionCallDescription(name, arguments);
        if (parsedConstraintFunction.defective()) {
            return parsedConstraintDescription.withErrorMessages(parsedConstraintFunction);
        }
        final var constraint = constraintDescription(parsedConstraintFunction.optionalValue().orElseThrow(), sourceCodeQuote(arguments));
        child.ifPresent(childVal -> {
            final var parsedChild = parseConstraintDescription(childVal.Name().getText()
                    , childVal.function_call_arguments()
                    , Optional.ofNullable(childVal.access()));
            parsedConstraintDescription.withErrorMessages(parsedChild);
            if (parsedChild.working()) {
                constraint.children().add(parsedChild.optionalValue().orElseThrow());
            }
        });
        return parsedConstraintDescription.withValue(constraint);
    }

    @Override
    public Result<List<ConstraintDescription>, Tree> visitFunction_call(DenParser.Function_callContext functionCall) {
        if ("constraints".equals(functionCall.Name().getText()) && functionCall.access() != null) {
            final var parsedConstraint = parseConstraintDescription(functionCall.access().Name().getText()
                    , functionCall.access().function_call_arguments()
                    , Optional.ofNullable(functionCall.access().access()));
            if (parsedConstraint.defective()) {
                return constraints.withErrorMessages(parsedConstraint);
            }
            constraints.optionalValue().orElseThrow().add(parsedConstraint.optionalValue().orElseThrow());
        }
        return constraints;
    }

    @Override
    public Result<List<ConstraintDescription>, Tree> visitVariable_definition(DenParser.Variable_definitionContext ctx) {
        if (ctx.Name().getText().equals("constraints")) {
            final var parsedConstraint = parseConstraintDescription(ctx.function_call());
            if (parsedConstraint.defective()) {
                return constraints.withErrorMessages(parsedConstraint);
            }
            constraints.optionalValue().orElseThrow().add(parsedConstraint.optionalValue().orElseThrow());
        }
        return constraints;
    }
}
