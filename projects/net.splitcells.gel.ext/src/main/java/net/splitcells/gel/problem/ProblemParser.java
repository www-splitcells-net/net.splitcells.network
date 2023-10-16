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
package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.ConstraintParser;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.dem.lang.perspective.antlr4.DenParserBaseVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.constraint.ConstraintParser.parseConstraint;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.constraint.type.ForAlls.forEach;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

public class ProblemParser extends DenParserBaseVisitor<Problem> {

    private Optional<String> name;

    private Optional<Database> demands = Optional.empty();
    private Optional<Database> supplies = Optional.empty();
    private Optional<Constraint> constraints = Optional.empty();

    public static Problem parseProblem(String arg) {
        final var lexer = new net.splitcells.dem.lang.perspective.antlr4.DenLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.dem.lang.perspective.antlr4.DenParser(new CommonTokenStream(lexer));
        return new ProblemParser().visitSource_unit(parser.source_unit());
    }

    @Override
    public Problem visitSource_unit(net.splitcells.dem.lang.perspective.antlr4.DenParser.Source_unitContext sourceUnit) {
        visitChildren(sourceUnit);
        final var assignments = assignments(name.orElseThrow(), demands.orElseThrow(), supplies.orElseThrow());
        parseConstraint(sourceUnit, assignments);
        return null;
    }

    @Override
    public Problem visitVariable_definition(net.splitcells.dem.lang.perspective.antlr4.DenParser.Variable_definitionContext ctx) {
        final var ctxName = ctx.Name().getText();
        if (ctxName.equals("name")) {
            if (name.isPresent()) {
                throw executionException("Names are not allowed to be defined multiple times.");
            }
            name = Optional.of(ctxName);
        } else if (ctxName.equals("demands")) {
            if (demands.isPresent()) {
                throw executionException("Demands are not allowed to be defined multiple times.");
            }
            final List<Attribute<? extends Object>> demandAttributes = list();
            final var firstDemandAttribute = ctx.block_statement().variable_definition();
            if (firstDemandAttribute != null) {
                demandAttributes.add(
                        parseAttribute(firstDemandAttribute.Name().getText()
                                , firstDemandAttribute.function_call().Name().getText()));
            }
            final var additionalDemandAttributes = ctx.block_statement().statement_reversed();
            additionalDemandAttributes.forEach(da -> demandAttributes
                    .add(parseAttribute(da.variable_definition().Name().getText()
                            , da.variable_definition().function_call().Name().getText())));
            demands = Optional.of(database(demandAttributes));
        } else if (ctxName.equals("supplies")) {
            if (supplies.isPresent()) {
                throw executionException("Supplies are not allowed to be defined multiple times.");
            }
            final List<Attribute<? extends Object>> supplyAttributes = list();
            final var firstSupplyAttribute = ctx.block_statement().variable_definition();
            if (firstSupplyAttribute != null) {
                supplyAttributes.add(
                        parseAttribute(firstSupplyAttribute.Name().getText()
                                , firstSupplyAttribute.function_call().Name().getText()));
            }
            final var additionalSupplyAttributes = ctx.block_statement().statement_reversed();
            additionalSupplyAttributes.forEach(sa -> supplyAttributes
                    .add(parseAttribute(sa.variable_definition().Name().getText()
                            , sa.variable_definition().function_call().Name().getText())));
            supplies = Optional.of(database(supplyAttributes));
        }
        return null;
    }

    private Attribute<? extends Object> parseAttribute(String name, String type) {
        if (type.equals("int")) {
            return attribute(Integer.class, name);
        } else if (type.equals("float")) {
            return attribute(Float.class, name);
        } else if (type.equals("string")) {
            return attribute(String.class, name);
        } else {
            throw executionException(type);
        }
    }
}
