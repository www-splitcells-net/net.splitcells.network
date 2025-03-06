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
package net.splitcells.gel.ui.code.editor;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.source.den.DenParser;
import net.splitcells.dem.source.den.DenParserBaseVisitor;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.editor.lang.*;
import net.splitcells.gel.editor.solution.SolutionEditor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.editor.lang.AttributeDescription.parseAttributeDescription;
import static net.splitcells.gel.editor.lang.ReferenceDescription.referenceDescription;
import static net.splitcells.gel.editor.lang.SolutionDescription.solutionDescription;
import static net.splitcells.gel.editor.lang.TableDescription.tableDescription;
import static net.splitcells.gel.ui.code.editor.CodeConstraintLangParser.parseConstraints;

/**
 * Using {@link SolutionDescription} avoid an indirect ANTLR API dependency.
 * The split also makes it easier, to make backward compatible changes to the parsing regarding the input syntax.
 */
public class CodeEditorLangParser extends DenParserBaseVisitor<Result<SolutionDescription, Tree>> {
    public static Result<SolutionDescription, Tree> editorLangParsing(String arg) {
        final var lexer = new net.splitcells.dem.source.den.DenLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.dem.source.den.DenParser(new CommonTokenStream(lexer));
        final List<Tree> parsingErrors = list();
        parser.addErrorListener(new BaseErrorListener() {
            // Ensures, that error messages are not hidden.
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                    throws ParseCancellationException {
                if (offendingSymbol instanceof CommonToken) {
                    final var token = (CommonToken) offendingSymbol;
                    parsingErrors.add(tree("Could not parse problem definition:")
                            .withProperty("line", "" + line)
                            .withProperty("column", "" + charPositionInLine)
                            .withProperty("invalid text", "`" + token.toString(recognizer) + "`")
                            .withProperty("invalid token", "`" + token.getText() + "`")
                            .withProperty("error", msg));
                } else {
                    parsingErrors.add(tree("Could not parse problem definition:")
                            .withProperty("line", "" + line)
                            .withProperty("column", "" + charPositionInLine)
                            .withProperty("invalid text", "`" + offendingSymbol.toString() + "`")
                            .withProperty("error", msg));
                }
            }
        });
        final var parsedEditor = new CodeEditorLangParser().visitSource_unit(parser.source_unit());
        parsedEditor.errorMessages().withAppended(parsingErrors);
        return parsedEditor;
    }

    private Optional<String> name = Optional.empty();
    private List<AttributeDescription> attributes = list();
    private Optional<TableDescription> demands = Optional.empty();
    private Optional<TableDescription> supplies = Optional.empty();
    private List<ConstraintDescription> constraints = list();
    private Result<SolutionDescription, Tree> result = result();
    private List<ReferenceDescription<AttributeDescription>> columnAttributesForOutputFormat = list();
    private List<ReferenceDescription<AttributeDescription>> rowAttributesForOutputFormat = list();

    private CodeEditorLangParser() {

    }

    @Override
    public Result<SolutionDescription, Tree> visitSource_unit(net.splitcells.dem.source.den.DenParser.Source_unitContext sourceUnit) {
        visitChildren(sourceUnit);
        if (name.isPresent() && demands.isPresent() && supplies.isPresent() && result.errorMessages().isEmpty()) {
            final var parsedConstraints = parseConstraints(sourceUnit);
            if (parsedConstraints.defective()) {
                return result.withErrorMessages(parsedConstraints);
            }
            constraints.withAppended(parsedConstraints.value().orElseThrow());
            final var solutionDescription = solutionDescription(name.get(), attributes, demands.get(), supplies.get(), constraints);
            solutionDescription.columnAttributesForOutputFormat().withAppended(columnAttributesForOutputFormat);
            solutionDescription.rowAttributesForOutputFormat().withAppended(rowAttributesForOutputFormat);
            result.withValue(solutionDescription);
        } else {
            if (name.isEmpty()) {
                result.withErrorMessage(tree("No name was defined via `name=\"[...]\"`."));
            }
            if (demands.isEmpty()) {
                result.withErrorMessage(tree("No demands was defined via `demands=\"[...]\"`."));
            }
            if (supplies.isEmpty()) {
                result.withErrorMessage(tree("No supplies was defined via `supplies=\"[...]\"`."));
            }
        }
        return result;
    }

    @Override
    public Result<SolutionDescription, Tree> visitVariable_definition(net.splitcells.dem.source.den.DenParser.Variable_definitionContext ctx) {
        final var ctxName = ctx.Name().getText();
        if (ctxName.equals("name")) {
            if (name.isPresent()) {
                result.withErrorMessage(tree("Names are not allowed to be defined multiple times."));
                return null;
            }
            name = Optional.of(ctxName);
        } else if (ctxName.equals("demands")) {
            if (demands.isPresent()) {
                result.withErrorMessage(tree("Demands are not allowed to be defined multiple times."));
                return null;
            }
            final List<AttributeDescription> demandAttributes = list();
            final var firstDemandAttribute = ctx.block_statement().variable_definition();
            if (firstDemandAttribute != null) {
                final var parsedAttribute = parseAttributeDescription(firstDemandAttribute.Name().getText()
                        , firstDemandAttribute.function_call().Name().getText());
                final var parsedAttributeValue = parsedAttribute.value();
                if (parsedAttributeValue.isPresent()) {
                    demandAttributes.add(parsedAttributeValue.get());
                }
                result.errorMessages().withAppended(parsedAttribute.errorMessages());
            }
            final var additionalDemandAttributes = ctx.block_statement().statement_reversed();
            additionalDemandAttributes.forEach(da -> {
                        final var parsedAttribute = parseAttributeDescription(da.variable_definition().Name().getText()
                                , da.variable_definition().function_call().Name().getText());
                        if (parsedAttribute.value().isPresent()) {
                            demandAttributes.add(parsedAttribute.value().get());
                        }
                        result.errorMessages().withAppended(parsedAttribute.errorMessages());
                    }
            );
            attributes.withAppended(demandAttributes);
            demands = Optional.of(tableDescription("demands"
                    , demandAttributes.mapped(da -> referenceDescription(da.name(), AttributeDescription.class))));
        } else if (ctxName.equals("supplies")) {
            if (supplies.isPresent()) {
                result.withErrorMessage(tree("Supplies are not allowed to be defined multiple times."));
                return null;
            }
            final List<AttributeDescription> supplyAttributes = list();
            final var firstSupplyAttribute = ctx.block_statement().variable_definition();
            if (firstSupplyAttribute != null) {
                final var parsedAttribute = parseAttributeDescription(firstSupplyAttribute.Name().getText()
                        , firstSupplyAttribute.function_call().Name().getText());
                final var parsedAttributeValue = parsedAttribute.value();
                if (parsedAttributeValue.isPresent()) {
                    supplyAttributes.add(parsedAttributeValue.get());
                }
                result.errorMessages().withAppended(parsedAttribute.errorMessages());
            }
            final var additionalSupplyAttributes = ctx.block_statement().statement_reversed();
            additionalSupplyAttributes.forEach(sa -> {
                final var parsedAttribute = parseAttributeDescription(sa.variable_definition().Name().getText()
                        , sa.variable_definition().function_call().Name().getText());
                if (parsedAttribute.value().isPresent()) {
                    supplyAttributes.add(parsedAttribute.value().get());
                }
                result.errorMessages().withAppended(parsedAttribute.errorMessages());
            });
            attributes.withAppended(supplyAttributes);
            supplies = Optional.of(tableDescription("supplies"
                    , supplyAttributes.mapped(sa -> referenceDescription(sa.name(), AttributeDescription.class))));
        }
        return null;
    }

    @Override
    public Result<SolutionDescription, Tree> visitFunction_call(DenParser.Function_callContext ctx) {
        final var subjectName = ctx.Name().getText();
        if (ctx.Name().getText().equals("constraints")) {
            return null;
        }
        if (ctx.access() == null && ctx.function_call_arguments() == null) {
            result.withErrorMessage(tree("Empty function calls are not supported at the top level.")
                    .withProperty("function call", ctx.getText()));
            return null;
        }
        if (ctx.access() == null && ctx.function_call_arguments() != null && !ctx.Name().getText().equals("constraints")) {
            result.withErrorMessage(tree("Only the constraints function is allowed to be called at the top level without a subject.")
                    .withProperty("function call", ctx.getText()));
            return null;
        }
        final var functionName = ctx.access().Name().getText();
        if (functionName.equals("columnAttributesForOutputFormat")
                && subjectName.equals("solution")) {
            columnAttributesForOutputFormat.add(referenceDescription(
                    ctx.access().function_call_arguments().function_call_arguments_element().Name().getText()
                    , AttributeDescription.class));
            ctx.access().function_call_arguments().function_call_arguments_next()
                    .forEach(e -> columnAttributesForOutputFormat.add(
                            referenceDescription(e.getText(), AttributeDescription.class)));
        } else if (functionName.equals("rowAttributesForOutputFormat")
                && subjectName.equals("solution")) {
            rowAttributesForOutputFormat.add(referenceDescription(
                    ctx.access().function_call_arguments().function_call_arguments_element().Name().getText()
                    , AttributeDescription.class));
            ctx.access().function_call_arguments().function_call_arguments_next()
                    .forEach(e -> rowAttributesForOutputFormat
                            .add(referenceDescription(e.function_call_arguments_element().Name().getText()
                                    , AttributeDescription.class)));
        } else {
            result.withErrorMessage(tree("There is an unknown top level function call.")
                    .withProperty("subject name", subjectName)
                    .withProperty("function name", functionName));
        }
        return null;
    }
}
