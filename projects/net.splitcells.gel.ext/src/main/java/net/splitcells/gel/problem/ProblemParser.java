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
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.attribute.AttributeI;
import net.splitcells.gel.ext.problem.ProblemParser.Variable_definitionContext;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.constraint.type.ForAlls.forEach;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.problem.ProblemI.problem;

public class ProblemParser {
    public static Problem parseProblem(String arg) {
        new ProblemParser(arg);
        return null;
    }

    private final Database demands;
    private final Database supplies;
    private final Constraint constraints;
    private final Assignments assignments;

    private Constraint parseConstraint(Variable_definitionContext variableDefinition) {
        final var constraintName = variableDefinition.function_call().Name().getText();
        if (!variableDefinition.function_call().access().isEmpty()) {
            throw executionException("Function chaining is not supported for constraint definition yet.");
        }
        if (constraintName.equals("forAll")) {
            if (variableDefinition.function_call().call_arguments().call_arguments_element() != null) {
                throw executionException("ForAll does not support arguments");
            }
            return forAll();
        } else if (constraintName.equals("forEach")) {
            if (variableDefinition.function_call().call_arguments().call_arguments_element() != null
                    && variableDefinition.function_call().call_arguments().call_arguments_next().isEmpty()) {
                if (!variableDefinition.function_call().call_arguments().call_arguments_element().function_call().isEmpty()) {
                    throw executionException("Function call argument are not supported for forEach constraint.");
                }
                final var attributeName = variableDefinition.function_call()
                        .call_arguments()
                        .call_arguments_element()
                        .Name()
                        .getText();
                final var demandAttributeMatches = demands.headerView().stream()
                        .filter(da -> da.name().equals(attributeName))
                        .collect(toList());
                final var supplyAttributeMatches = supplies.headerView().stream()
                        .filter(sa -> sa.name().equals(attributeName))
                        .collect(toList());
                requireEquals(demandAttributeMatches.size() + supplyAttributeMatches.size(), 1);
                final Attribute<? extends Object> matchedAttribute;
                if (demandAttributeMatches.hasElements()) {
                    matchedAttribute = demandAttributeMatches.get(0);
                } else if (supplyAttributeMatches.hasElements()) {
                    matchedAttribute = supplyAttributeMatches.get(0);
                } else {
                    throw executionException("Invalid program state.");
                }
                return forEach(matchedAttribute);
            }
            if (!variableDefinition.function_call().call_arguments().call_arguments_next().isEmpty()) {
                throw executionException("ForEach does not support multiple arguments.");
            }
            throw executionException("Invalid program state.");
        } else {
            throw executionException("Unknown constraint name: " + constraintName);
        }
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

    private ProblemParser(String arg) {
        final var lexer = new net.splitcells.gel.ext.problem.ProblemLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.gel.ext.problem.ProblemParser(new CommonTokenStream(lexer));
        final var source_unit = parser.source_unit();
        final var names = source_unit.statement().stream()
                .filter(vd -> vd.variable_definition() != null)
                .filter(vd -> vd.variable_definition().Name().getText().equals("name"))
                .collect(toList());
        names.requireSizeOf(1);
        final var name = names.get(0).getText();
        final List<Attribute<? extends Object>> demandAttributes = list();
        {
            final var demandDefinition = source_unit.statement().stream()
                    .filter(vd -> vd.variable_definition() != null)
                    .filter(vd -> vd.variable_definition().Name().getText().equals("demands"))
                    .collect(toList());
            demandDefinition.requireSizeOf(1);
            final var firstDemandAttribute = demandDefinition.get(0).variable_definition().map().variable_definition();
            if (firstDemandAttribute != null) {
                demandAttributes.add(
                        parseAttribute(firstDemandAttribute.Name().getText()
                                , firstDemandAttribute.function_call().Name().getText()));
            }
            final var additionalDemandAttributes = demandDefinition.get(0).variable_definition().map().statement_reversed();
            additionalDemandAttributes.forEach(da -> demandAttributes
                    .add(parseAttribute(da.variable_definition().Name().getText()
                            , da.variable_definition().function_call().Name().getText())));
        }
        final List<Attribute<? extends Object>> supplyAttributes = list();
        {
            final var supplyDefinition = source_unit.statement().stream()
                    .filter(vd -> vd.variable_definition() != null)
                    .filter(vd -> vd.variable_definition().Name().getText().equals("supplies"))
                    .collect(toList());
            supplyDefinition.requireSizeOf(1);
            final var firstSupplyAttribute = supplyDefinition.get(0).variable_definition().map().variable_definition();
            if (firstSupplyAttribute != null) {
                supplyAttributes.add(
                        parseAttribute(firstSupplyAttribute.Name().getText()
                                , firstSupplyAttribute.function_call().Name().getText()));
            }
            final var additionalSupplyAttributes = supplyDefinition.get(0).variable_definition().map().statement_reversed();
            additionalSupplyAttributes.forEach(sa -> supplyAttributes
                    .add(parseAttribute(sa.variable_definition().Name().getText()
                            , sa.variable_definition().function_call().Name().getText())));
        }
        demands = database(demandAttributes);
        supplies = database(supplyAttributes);
        assignments = assignments(name, demands, supplies);
        final var constraintDefinition = source_unit.statement().stream()
                .filter(vd -> vd.variable_definition() != null)
                .filter(vd -> vd.variable_definition().Name().getText().equals("constraints"))
                .collect(toList());

        constraintDefinition.requireSizeOf(1);
        constraints = parseConstraint(constraintDefinition.get(0).variable_definition());
    }
}
