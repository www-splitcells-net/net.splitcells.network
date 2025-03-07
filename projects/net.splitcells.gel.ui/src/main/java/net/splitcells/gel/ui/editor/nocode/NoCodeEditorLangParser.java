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
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenParserBaseVisitor;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.lang.*;
import net.splitcells.gel.ui.SolutionParameters;
import net.splitcells.gel.ui.no.code.editor.NoCodeQueryParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.object.Discoverable.NO_CONTEXT;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.view.attribute.Attributes.parseAttribute;
import static net.splitcells.gel.editor.lang.AttributeDescription.parseAttributeDescription;
import static net.splitcells.gel.editor.lang.ReferenceDescription.referenceDescription;
import static net.splitcells.gel.editor.lang.TableDescription.tableDescription;
import static net.splitcells.gel.problem.ProblemI.problem;
import static net.splitcells.gel.ui.no.code.editor.NoCodeQueryParser.parseNoCodeQuery;

/**
 * <p>TODO Remove the distinction between variable names and the object's name (i.e. `variable_name = attribute(string, "Another Name")`.</p>
 * <p>TODO The hole language will have to be reviewed and adapted, as it is in a poor state.</p>
 * <p>TODO Enable drag and drop via dragula only, when it is explicitly toggled on.</p>
 */
public class NoCodeEditorLangParser extends NoCodeDenParserBaseVisitor<Result<SolutionDescription, Tree>> {
    private static final String ATTRIBUTE = "attribute";
    private static final String CONSTRAINTS = "constraints";
    private static final String CONTENT = "content";
    private static final String DEMANDS = "demands";
    private static final String TABLE = "table";
    private static final String NAME = "name";
    private static final String SOLUTION = "solution";
    private static final String SUPPLIES = "supplies";

    private Result<SolutionDescription, Tree> noCodeEditorLangParsing(String arg) {
        final var lexer = new net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenParser(new CommonTokenStream(lexer));
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
        final var parsedProblem = new NoCodeEditorLangParser().visitSource_unit(parser.source_unit());
        parsedProblem.errorMessages().withAppended(parsingErrors);
        return parsedProblem;
    }

    private Optional<String> name = Optional.empty();
    private Map<String, AttributeDescription> attributes = map();
    private Optional<TableDescription> demands = Optional.empty();
    private Optional<TableDescription> supplies = Optional.empty();
    private List<ConstraintDescription> constraints = list();
    private Result<SolutionDescription, Tree> result = result();
    private List<ReferenceDescription<AttributeDescription>> columnAttributesForOutputFormat = list();
    private List<ReferenceDescription<AttributeDescription>> rowAttributesForOutputFormat = list();

    private NoCodeEditorLangParser() {

    }

    /**
     * Nothing needs to be done for variable access for {@link #CONSTRAINTS},
     * as this is handled by {@link NoCodeQueryParser}.
     *
     * @param ctx the parse tree
     * @return
     */
    @Override
    public Result<SolutionDescription, Tree> visitVariable_access(net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenParser.Variable_accessContext ctx) {
        final var referencedName = ctx.variable_reference().Name().getText();
        if (SOLUTION.equals(referencedName)) {
            if (ctx.function_call().size() > 1) {
                result.withErrorMessage(tree("Variable access for " + SOLUTION + " is only supported for 1 function call, but more where given.")
                        .withProperty("variable access", ctx.getText()));
            }
            final var functionCallname = ctx.function_call(0).function_call_name().string_value().getText();
            if ("columnAttributesForOutputFormat".equals(functionCallname)) {
                ctx.function_call(0).function_call_argument()
                        .forEach(arg -> columnAttributesForOutputFormat
                                .add(referenceDescription(arg.value().variable_reference().Name().getText(), AttributeDescription.class)));
            } else if ("rowAttributesForOutputFormat".equals(functionCallname)) {
                ctx.function_call(0).function_call_argument()
                        .forEach(arg -> rowAttributesForOutputFormat
                                .add(referenceDescription(arg.value().variable_reference().Name().getText(), AttributeDescription.class)));
            }
        } else if (CONSTRAINTS.equals(referencedName)) {
            return result;
        } else {
            result.withErrorMessage(tree("Only function calls"));
        }
        return result;
    }

    @Override
    public Result<SolutionDescription, Tree> visitVariable_definition(net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenParser.Variable_definitionContext ctx) {
        final var variableName = ctx.variable_definition_name().Name().getText();
        if (variableName.equals("constraints")) {
            return null;
        }
        if (ctx.variable_definition_value() == null || ctx.variable_definition_value().value() == null) {
            result.withErrorMessage(tree("Variable definition is missing a name.")
                    .withProperty(CONTENT, ctx.getText()));
            return null;
        }
        if (ctx.variable_definition_value() == null || ctx.variable_definition_value().value() == null) {
            result.withErrorMessage(tree("Variable definition is missing a value.")
                    .withProperty(CONTENT, ctx.getText()));
            return null;
        }
        if (ctx.variable_definition_value().value().function_call() != null) {
            if (ctx.variable_definition_value().value().function_call().size() < 1) {
                result.withErrorMessage(tree("Variable definition with a function call chain as its value, needs at least one function call, but has none.")
                        .withProperty(CONTENT, ctx.getText()));
                return null;
            }
            final var functionCall = ctx.variable_definition_value().value().function_call().get(0);
            final var functionName = functionCall.function_call_name().string_value().getText();
            if (functionName.equals(SOLUTION)) {
                if (!variableName.equals(SOLUTION)) {
                    result.withErrorMessage(tree("A solution can only be defined for the variable name solution.")
                            .withProperty("Given variable name", variableName)
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                if (functionCall.function_call_argument().size() != 3) {
                    result.withErrorMessage(tree("A solution requires 3 arguments: variable name, demands, supplies")
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
            } else if (functionName.equals(TABLE)) {
                if (functionCall.function_call_argument().size() < 2) {
                    result.withErrorMessage(tree("The function database requires at least arguments.")
                            .withProperty("Actual arguments given", functionCall.function_call_argument().toString())
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                final var databaseNameText = functionCall.function_call_argument(0);
                if (databaseNameText.value().string_value() == null) {
                    result.withErrorMessage(tree("The database name (first argument) needs to be string value, but is not.")
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                final var databaseName = databaseNameText.value().string_value().getText();
                final List<ReferenceDescription<AttributeDescription>> databaseAttributes = list();
                for (int i = 1; i < functionCall.function_call_argument().size(); ++i) {
                    final var attributeText = functionCall.function_call_argument(i).value();
                    if (attributeText.Function_call_var_arg() != null) {
                        continue;
                    }
                    if (attributeText.variable_reference() == null) {
                        result.withErrorMessage(tree("The database attributes needs to be attribute references, but are not.")
                                .withProperty("invalid attribute", attributeText.getText())
                                .withProperty(CONTENT, ctx.getText()));
                        return null;
                    }
                    if (!attributes.containsKey(attributeText.variable_reference().Name().getText())) {
                        result.withErrorMessage(tree("A unknown variable is referenced via the namename.")
                                .withProperty("invalid attribute", attributeText.getText())
                                .withProperty(CONTENT, ctx.getText()));
                        return null;
                    }
                    databaseAttributes.add(referenceDescription(attributeText.variable_reference().Name().getText(), AttributeDescription.class));
                }
                if (DEMANDS.equals(variableName)) {
                    this.demands = Optional.of(tableDescription(variableName, databaseAttributes));
                } else if (SUPPLIES.equals(variableName)) {
                    this.supplies = Optional.of(tableDescription(variableName, databaseAttributes));
                } else {
                    return result.withErrorMessage(tree("The only valid variable names are `demands` and `supplies`. Instead `" + variableName + "` was given.")
                            .withProperty(CONTENT, ctx.getText()));
                }
                return null;
            } else if (functionName.equals(ATTRIBUTE)) {
                if (functionCall.function_call_argument().size() != 2) {
                    result.withErrorMessage(tree("The function attribute requires exactly 2 arguments.")
                            .withProperty("Actual arguments given", functionCall.function_call_argument().toString())
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                final var attributeName = functionCall.function_call_argument(0);
                if (attributeName.value().string_value() == null) {
                    result.withErrorMessage(tree("The attributes name (first argument) needs to be string value, but is not.")
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                final var attributeType = functionCall.function_call_argument(1);
                if (attributeType.value().string_value() == null) {
                    result.withErrorMessage(tree("The attributes type (second argument) needs to be string value, but is not.")
                            .withProperty(CONTENT, ctx.getText()));
                    return null;
                }
                final var parsedAttribute = parseAttributeDescription(attributeName.value().string_value().getText()
                        , attributeType.value().string_value().getText());
                if (parsedAttribute.defective()) {
                    result.errorMessages().withAppended(parsedAttribute.errorMessages());
                    return result;
                }
                attributes.put(variableName, parsedAttribute.requiredValue());
                return null;
            } else {
                result.withErrorMessage(tree("Unknown first function name in function chain for variable definition.")
                        .withProperty(CONTENT, ctx.getText()));
                return null;
            }
        } else if (ctx.variable_definition_value().value().variable_reference() != null) {
            result.withErrorMessage(tree("Variable definition with a variable reference as its value is not supported.")
                    .withProperty(CONTENT, ctx.getText()));
            return null;
        } else {
            result.withErrorMessage(tree("Variable definition is missing a value.")
                    .withProperty(CONTENT, ctx.getText()));
            return null;
        }
        return null;
    }
}
