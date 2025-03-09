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
package net.splitcells.gel.editor.solution;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.lang.*;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAll.FOR_ALL_NAME;
import static net.splitcells.gel.constraint.type.ForAlls.*;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_ALL_COMBINATIONS_OF;
import static net.splitcells.gel.constraint.type.Then.THEN_NAME;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.view.attribute.AttributeI.stringAttribute;
import static net.splitcells.gel.editor.lang.PrimitiveType.INTEGER;
import static net.splitcells.gel.editor.lang.PrimitiveType.STRING;
import static net.splitcells.gel.rating.rater.lib.AllSame.ALL_SAME_NAME;
import static net.splitcells.gel.rating.rater.lib.AllSame.allSame;
import static net.splitcells.gel.rating.rater.lib.HasSize.HAS_SIZE_NAME;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.MINIMAL_DISTANCE_NAME;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.has_minimal_distance_of;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;

public class SolutionEditor implements Discoverable {
    public static final String AFFECTED_CONTENT = "affected content";
    public static final String AFFECTED_CONTEXT = "affected context";

    public static SolutionEditor solutionEditor(Editor parent, SolutionDescription solutionDescription) {
        return new SolutionEditor(parent, solutionDescription);
    }

    private final Editor parent;
    private final String name;
    private final Map<String, Attribute<?>> attributes = map();
    private Optional<Table> demands = Optional.empty();
    private Optional<Table> supplies;
    private Optional<Solution> solution = Optional.empty();

    private List<String> columnAttributesForOutputFormat = list();
    private List<String> rowAttributesForOutputFormat = list();

    private SolutionEditor(Editor argParent, SolutionDescription solutionDescription) {
        parent = argParent;
        name = solutionDescription.name();
    }

    public Result<SolutionEditor, Tree> parse(SolutionDescription solutionDescription) {
        final Result<SolutionEditor, Tree> result = result();
        result.withValue(this);
        solutionDescription.attributes().entrySet().forEach(ad -> {
            final var attributeDesc = ad.getValue();
            final Attribute<?> attribute;
            if (INTEGER.equals(attributeDesc.primitiveType())) {
                attribute = integerAttribute(attributeDesc.name());
            } else if (STRING.equals(attributeDesc.primitiveType())) {
                attribute = stringAttribute(attributeDesc.name());
            } else {
                throw execException();
            }
            attributes.put(attributeDesc.name(), attribute);
        });
        demands = Optional.of(parseTable(solutionDescription.demands()));
        supplies = Optional.of(parseTable(solutionDescription.supplies()));
        final var problemData = defineProblem("solution")
                .withDemands(demands.orElseThrow())
                .withSupplies(supplies.orElseThrow());
        final var constraintRoot = query(forAll(Optional.of(NO_CONTEXT)));
        final var parsedConstraints = solutionDescription.constraints()
                .mapped(c -> parseConstraint(c, constraintRoot));
        parsedConstraints.forEach(c -> {
            if (c.defective()) {
                result.errorMessages().withAppended(c.errorMessages());
            }
        });
        if (result.defective()) {
            return result;
        }
        solution = Optional.of(problemData
                .withConstraint(constraintRoot.root().orElseThrow())
                .toProblem()
                .asSolution());
        return result;
    }

    private Result<Query, Tree> parseConstraint(ConstraintDescription constraintDescription, Query parentConstraint) {
        final Result<Query, Tree> constraint = result();
        constraint.withValue(parentConstraint);
        final Query nextConstraint;
        final var constraintName = constraintDescription.definition().functionName();
        final var arguments = constraintDescription.definition().arguments();
        if (constraintName.equals(FOR_ALL_NAME)) {
            if (arguments.hasElements()) {
                return constraint.withErrorMessage(tree("ForAll does not support arguments.")
                        .withProperty(AFFECTED_CONTENT, constraintDescription.toString()));
            }
            nextConstraint = parentConstraint.forAll();
        } else if (constraintName.equals(FOR_EACH_NAME)) {
            if (arguments.isEmpty()) {
                nextConstraint = parentConstraint.forAll();
            } else if (arguments.size() == 1) {
                final var arg = arguments.get(0);
                switch (arg) {
                    case ReferenceDescription ref -> {
                        nextConstraint = parentConstraint.forAll(attributes.get(ref.name()));
                    }
                    default -> {
                        return constraint.withErrorMessage(tree("ForEach requires a reference as the argument.")
                                .withProperty("Argument class", arg.getClass().getName())
                                .withProperty("Argument", arg.toString()));
                    }
                }
            } else if (constraintDescription.definition().arguments().size() > 1) {
                return constraint.withErrorMessage(tree("ForEach does not support multiple arguments.")
                        .withProperty(AFFECTED_CONTENT
                                , constraintDescription.toString()));
            } else {
                throw execException();
            }
        } else if (constraintName.equals(FOR_ALL_COMBINATIONS_OF)) {
            if (arguments.size() < 2) {
                return constraint.withErrorMessage(tree(FOR_ALL_COMBINATIONS_OF + " requires at least 2 arguments.")
                        .withProperty(AFFECTED_CONTENT, constraintDescription.toString()));
            }
            final List<Attribute<? extends Object>> combinations = list();
            for (final var arg : arguments) {
                switch (arg) {
                    case ReferenceDescription ref -> {
                        combinations.add(attributeByName(ref.name()));
                    }
                    default -> {
                        return constraint.withErrorMessage(tree("ForAllCombinationsOf only takes attribute references as arguments.")
                                .withProperty("Argument class", arg.getClass().getName())
                                .withProperty("Argument", arg.toString())
                                .withProperty(AFFECTED_CONTENT, arg.toString()));
                    }
                }
            }
            nextConstraint = parentConstraint.forAllCombinationsOf(combinations);
        } else if (constraintName.equals(THEN_NAME)) {
            if (arguments.isEmpty()) {
                return constraint
                        .withErrorMessage(tree("Then constraint requires at least one argument.")
                                .withProperty(AFFECTED_CONTENT, constraintDescription.toString()));
            }
            if (arguments.size() > 1) {
                return constraint.withErrorMessage(tree("Then constraint only support one argument at maximum")
                        .withProperty(AFFECTED_CONTENT, constraintDescription.toString()));
            }
            final Rater rater;
            switch (arguments.get(0)) {
                case FunctionCallDescription functionCall -> {
                    final var parserRater = parseRater(functionCall);
                    if (parserRater.defective()) {
                        constraint.errorMessages().withAppended(parserRater.errorMessages());
                        return constraint;
                    }
                    rater = parserRater.optionalValue().orElseThrow();
                }
                default -> {
                    constraint.withErrorMessage(tree("`" + THEN_NAME + "` requires exactly function call as an argument. Instead an `" + arguments.get(0).getClass().getName() + "` was given.")
                            .withProperty(AFFECTED_CONTENT, constraintDescription.toString()));
                    return constraint;
                }
            }
            nextConstraint = parentConstraint.then(rater);
        } else {
            return constraint.withErrorMessage(tree("Unknown constraint type")
                    .withProperty(AFFECTED_CONTENT, constraintDescription.toString())
                    .withProperty("constraint type", constraintName));
        }
        constraintDescription.children().forEach(c -> {
            final var cResult = parseConstraint(c, nextConstraint);
            if (cResult.defective()) {
                constraint.errorMessages().withAppended(cResult.errorMessages());
            }
        });
        return constraint;
    }

    private Result<Rater, Tree> parseRater(FunctionCallDescription functionCall) {
        final Result<Rater, Tree> rater = Result.result();
        final var name = functionCall.functionName();
        if (name.equals(HAS_SIZE_NAME)) {
            switch (functionCall.arguments().get(0)) {
                case IntegerDescription integer -> {
                    return rater.withValue(hasSize(integer.value()));
                }
                default -> {
                    return rater.withErrorMessage(tree("`" + HAS_SIZE_NAME + "` requires exactly one integer as an argument. Instead an `" + functionCall.arguments().get(0).getClass().getName() + "` was given.")
                            .withProperty(AFFECTED_CONTENT, functionCall.toString()));
                }
            }
        } else if (name.equals(ALL_SAME_NAME)) {
            switch (functionCall.arguments().get(0)) {
                case ReferenceDescription<?> ref -> {
                    return rater.withValue(allSame(attributeByName(ref.name())));
                }
                default -> {
                    return rater.withErrorMessage(tree("`" + ALL_SAME_NAME + "` requires exactly one string as an argument. Instead an `" + functionCall.arguments().get(0).getClass().getName() + "` was given.")
                            .withProperty(AFFECTED_CONTENT, functionCall.toString()));
                }
            }
        } else if (name.equals(MINIMAL_DISTANCE_NAME)) {
            if (functionCall.arguments().isEmpty()) {
                return rater.withErrorMessage(tree("Rater `" + MINIMAL_DISTANCE_NAME + "` requires exactly 2 arguments, but has none.")
                        .withProperty("rater", functionCall.toString()));
            }
            if (functionCall.arguments().size() != 2) {
                return rater.withErrorMessage(tree("Rater `" + MINIMAL_DISTANCE_NAME + "` requires exactly 2 arguments.")
                        .withProperty("rater", functionCall.toString()));
            }
            final Attribute<? extends Object> attribute;
            switch (functionCall.arguments().get(0)) {
                case ReferenceDescription ref -> {
                    attribute = attributeByName(ref.name());
                }
                default -> {
                    return rater.withErrorMessage(tree("`" + MINIMAL_DISTANCE_NAME + "` first argument has to be a reference. Instead an `" + functionCall.arguments().get(0).getClass().getName() + "` was given.")
                            .withProperty(AFFECTED_CONTENT, functionCall.toString()));
                }
            }
            final int minimumDistance;
            switch (functionCall.arguments().get(1)) {
                case IntegerDescription integer -> {
                    minimumDistance = integer.value();
                }
                default -> {
                    return rater.withErrorMessage(tree("`" + MINIMAL_DISTANCE_NAME + "` second argument has to be an integer. Instead an `" + functionCall.arguments().get(1).getClass().getName() + "` was given.")
                            .withProperty(AFFECTED_CONTENT, functionCall.toString()));
                }
            }
            return rater.withValue(has_minimal_distance_of((Attribute<Integer>) attribute, minimumDistance));

        }
        return rater.withErrorMessage(tree("Unknown rater function: " + functionCall.toString()));
    }

    private Attribute<? extends Object> attributeByName(String name) {
        Optional<Attribute<? extends Object>> attribute = Optional.empty();
        if (demands.isPresent()) {
            attribute = demands.orElseThrow().searchAttributeByName(name);
        }
        if (attribute.isEmpty() && supplies.isPresent()) {
            attribute = supplies.orElseThrow().searchAttributeByName(name);
        }
        return attribute.orElseThrow();
    }

    private Table parseTable(TableDescription tableDescription) {
        final List<Attribute<?>> header = list();
        tableDescription.header().flow()
                .map(h -> (Attribute<?>) attributes.get(h.name()))
                .forEach(h -> header.add(h));
        return table(tableDescription.name(), (Discoverable) this, header);
    }

    public Map<String, Attribute<? extends Object>> attributes() {
        return attributes;
    }

    public String name() {
        return name;
    }

    @Override
    public List<String> path() {
        return parent.path().withAppended(name);
    }

    public Optional<Table> demands() {
        return demands;
    }

    public Optional<Table> supplies() {
        return supplies;
    }

    public Optional<Solution> solution() {
        return solution;
    }

    /**
     * TODO Return a serialization, so it is clear, which part is incorrect.
     *
     * @return
     */
    @Override
    public String toString() {
        return super.toString();
    }

    public List<String> columnAttributesForOutputFormat() {
        return columnAttributesForOutputFormat;
    }

    public List<String> rowAttributesForOutputFormat() {
        return rowAttributesForOutputFormat;
    }
}
