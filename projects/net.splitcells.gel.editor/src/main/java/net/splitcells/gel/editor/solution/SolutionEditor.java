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
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.lang.*;
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
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.view.attribute.AttributeI.stringAttribute;
import static net.splitcells.gel.editor.lang.PrimitiveType.INTEGER;
import static net.splitcells.gel.editor.lang.PrimitiveType.STRING;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;

public class SolutionEditor implements Discoverable {
    private static final String AFFECTED_CONTENT = "affected content";

    public static SolutionEditor solutionEditor(Editor parent, SolutionDescription solutionDescription) {
        return new SolutionEditor(parent, solutionDescription);
    }

    private final Editor parent;
    private final String name;
    private final Map<String, Attribute<?>> attributes = map();
    private Optional<Table> demands = Optional.empty();
    private Optional<Table> supplies;
    private Optional<Solution> solution = Optional.empty();

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
        if (constraintDescription.definition().functionName().equals(FOR_ALL_NAME)) {
            if (constraintDescription.definition().arguments().hasElements()) {
                return constraint.withErrorMessage(tree("ForAll does not support arguments.")
                        .withProperty(AFFECTED_CONTENT, toString()));
            }
            nextConstraint = parentConstraint.forAll();
        } else if (constraintDescription.definition().functionName().equals(FOR_EACH_NAME)) {
            if (constraintDescription.definition().arguments().isEmpty()) {
                nextConstraint = parentConstraint.forAll();
            } else if (constraintDescription.definition().arguments().size() == 1) {
                final var arg = constraintDescription.definition().arguments().get(0);
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
                                , toString()));
            } else {
                throw execException();
            }
        } else if (constraintDescription.definition().functionName().equals(FOR_ALL_COMBINATIONS_OF)) {
            if (constraintDescription.definition().arguments().size() < 2) {
                return constraint.withErrorMessage(tree(FOR_ALL_COMBINATIONS_OF + " requires at least 2 arguments.")
                        .withProperty(AFFECTED_CONTENT, toString()));
            }
            final List<Attribute<? extends Object>> combinations = list();
            for (final var arg : constraintDescription.definition().arguments()) {
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
        } else {
            throw execException();
        }
        constraintDescription.children().forEach(c -> {
            final var cResult = parseConstraint(c, nextConstraint);
            if (cResult.defective()) {
                constraint.errorMessages().withAppended(cResult.errorMessages());
            }
        });
        return constraint;
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
}
