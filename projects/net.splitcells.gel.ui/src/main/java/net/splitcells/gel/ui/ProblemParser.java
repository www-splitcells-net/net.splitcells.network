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
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.dem.lang.perspective.antlr4.DenParserBaseVisitor;
import net.splitcells.gel.problem.Problem;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.data.table.attribute.AttributeI.floatAttribute;
import static net.splitcells.gel.data.table.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.table.attribute.AttributeI.stringAttribute;
import static net.splitcells.gel.ui.QueryParser.parseQuery;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.problem.ProblemI.problem;

public class ProblemParser extends DenParserBaseVisitor<Result<Problem, Perspective>> {

    private Optional<String> name = Optional.empty();

    private Optional<Database> demands = Optional.empty();
    private Optional<Database> supplies = Optional.empty();

    private Result<Problem, Perspective> problem = result();

    public static Result<Problem, Perspective> parseProblem(String arg) {
        final var lexer = new net.splitcells.dem.lang.perspective.antlr4.DenLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.dem.lang.perspective.antlr4.DenParser(new CommonTokenStream(lexer));
        return new ProblemParser().visitSource_unit(parser.source_unit());
    }

    @Override
    public Result<Problem, Perspective> visitSource_unit(net.splitcells.dem.lang.perspective.antlr4.DenParser.Source_unitContext sourceUnit) {
        visitChildren(sourceUnit);
        if (name.isPresent() && demands.isPresent() && supplies.isPresent() && problem.errorMessages().isEmpty()) {
            final var assignments = assignments(name.orElseThrow(), demands.orElseThrow(), supplies.orElseThrow());
            final var parsedQuery = parseQuery(sourceUnit, assignments);
            problem.errorMessages().withAppended(parsedQuery.errorMessages());
            if (parsedQuery.value().isEmpty()) {
                return problem;
            }
            problem.withValue(problem(assignments, parsedQuery.value().orElseThrow().root().orElseThrow()));
        } else {
            if (name.isEmpty()) {
                problem.withErrorMessage(perspective("No name was defined via `name=\"[...]\"`."));
            }
            if (demands.isEmpty()) {
                problem.withErrorMessage(perspective("No demands was defined via `demands=\"[...]\"`."));
            }
            if (supplies.isEmpty()) {
                problem.withErrorMessage(perspective("No supplies was defined via `supplies=\"[...]\"`."));
            }
        }
        return problem;
    }

    @Override
    public Result<Problem, Perspective> visitVariable_definition(net.splitcells.dem.lang.perspective.antlr4.DenParser.Variable_definitionContext ctx) {
        final var ctxName = ctx.Name().getText();
        if (ctxName.equals("name")) {
            if (name.isPresent()) {
                problem.withErrorMessage(perspective("Names are not allowed to be defined multiple times."));
                return null;
            }
            name = Optional.of(ctxName);
        } else if (ctxName.equals("demands")) {
            if (demands.isPresent()) {
                problem.withErrorMessage(perspective("Demands are not allowed to be defined multiple times."));
                return null;
            }
            final List<Attribute<? extends Object>> demandAttributes = list();
            final var firstDemandAttribute = ctx.block_statement().variable_definition();
            if (firstDemandAttribute != null) {
                final var parsedAttribute = parseAttribute(firstDemandAttribute.Name().getText()
                        , firstDemandAttribute.function_call().Name().getText());
                if (parsedAttribute.value().isPresent()) {
                    demandAttributes.add(parsedAttribute.value().get());
                }
                problem.errorMessages().withAppended(parsedAttribute.errorMessages());
            }
            final var additionalDemandAttributes = ctx.block_statement().statement_reversed();
            additionalDemandAttributes.forEach(da -> {
                        final var parsedAttribute = parseAttribute(da.variable_definition().Name().getText()
                                , da.variable_definition().function_call().Name().getText());
                        if (parsedAttribute.value().isPresent()) {
                            demandAttributes.add(parsedAttribute.value().get());
                        }
                        problem.errorMessages().withAppended(parsedAttribute.errorMessages());
                    }
            );
            demands = Optional.of(database(demandAttributes));
        } else if (ctxName.equals("supplies")) {
            if (supplies.isPresent()) {
                problem.withErrorMessage(perspective("Supplies are not allowed to be defined multiple times."));
                return null;
            }
            final List<Attribute<? extends Object>> supplyAttributes = list();
            final var firstSupplyAttribute = ctx.block_statement().variable_definition();
            if (firstSupplyAttribute != null) {
                final var parsedAttribute = parseAttribute(firstSupplyAttribute.Name().getText()
                        , firstSupplyAttribute.function_call().Name().getText());
                if (parsedAttribute.value().isPresent()) {
                    supplyAttributes.add(parsedAttribute.value().get());
                }
                problem.errorMessages().withAppended(parsedAttribute.errorMessages());
            }
            final var additionalSupplyAttributes = ctx.block_statement().statement_reversed();
            additionalSupplyAttributes.forEach(sa -> {
                final var parsedAttribute = parseAttribute(sa.variable_definition().Name().getText()
                        , sa.variable_definition().function_call().Name().getText());
                if (parsedAttribute.value().isPresent()) {
                    supplyAttributes.add(parsedAttribute.value().get());
                }
                problem.errorMessages().withAppended(parsedAttribute.errorMessages());
            });
            supplies = Optional.of(database(supplyAttributes));
        }
        return null;
    }

    private Result<Attribute<? extends Object>, Perspective> parseAttribute(String name, String type) {
        final Result<Attribute<? extends Object>, Perspective> parsedAttribute = result();
        if (type.equals("int")) {
            return parsedAttribute.withValue(integerAttribute(name));
        } else if (type.equals("float")) {
            return parsedAttribute.withValue(floatAttribute(name));
        } else if (type.equals("string")) {
            return parsedAttribute.withValue(stringAttribute(name));
        }
        return parsedAttribute.withErrorMessage(perspective("Unknown attribute type.")
                .withProperty("name", name)
                .withProperty("type", type));
    }
}
