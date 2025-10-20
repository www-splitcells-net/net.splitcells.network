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
package net.splitcells.gel.editor.geal;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallChainDesc;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.NameDesc;
import net.splitcells.gel.editor.geal.lang.StringDesc;
import net.splitcells.gel.editor.meta.Type;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.lang.CommonMarkUtils.joinDocuments;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_ALL_COMBINATIONS_OF;
import static net.splitcells.gel.editor.geal.lang.NameDesc.nameDesc;
import static net.splitcells.gel.editor.geal.lang.StringDesc.stringDesc;

/**
 * Extracts function call data from {@link FunctionCallDesc},
 * while recording all types of function calls, in order to generate a complete function call documentation.
 * Through the combination of value extraction and documentation recording in single methods,
 * the parsing and its error messages are more standardized.
 */
@Accessors(chain = true)
public class FunctionCallRecord implements Closeable {
    public static FunctionCallRecord functionCallRecord(Editor argContext, String argName, int argVariation) {
        return new FunctionCallRecord(argContext, argName, argVariation);
    }

    @Getter private final String name;
    /**
     * {@link #name} + {@link #variation} is the id.
     * A function with a name can have multiple variation,
     * where each one has different arguments.
     */
    @Getter private final int variation;
    /**
     * This is a CommonMark document.
     */
    private StringBuilder description = StringUtils.stringBuilder();
    private final Editor context;

    private FunctionCallRecord(Editor argContext, String argName, int argVariation) {
        name = argName;
        variation = argVariation;
        context = argContext;
    }

    public FunctionCallRecord addDescription(String addition) {
        joinDocuments(description, addition);
        return this;
    }

    public void requireArgumentCount(FunctionCallDesc functionCall, int requiredArgumentCount) {
        if (functionCall.getArguments().size() != requiredArgumentCount) {
            throw execException(tree("The "
                    + name
                    + " function requires exactly "
                    + requiredArgumentCount
                    + " arguments, but "
                    + functionCall.getArguments().size()
                    + " were given.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
    }

    public void requireArgumentMinimalCount(FunctionCallDesc functionCall, int requiredMinimum) {
        if (functionCall.getArguments().size() < requiredMinimum) {
            throw execException(tree("The "
                    + name
                    + " function requires at least "
                    + requiredMinimum
                    + " arguments, but "
                    + functionCall.getArguments().size() + " were given instead.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
    }

    public NameDesc parseArgumentAsType(FunctionCallDesc functionCall, int argument) {
        final var first = context.parse(functionCall.getArguments().get(argument));
        switch (first) {
            case Type n -> {
                return nameDesc(n.getName(), functionCall.getArguments().get(argument).getSourceCodeQuote());
            }
            default -> throw execException(tree("The argument "
                    + argument
                    + " of "
                    + name
                    + "has to be a name, but "
                    + first.getClass().getName()
                    + " was given instead.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
    }

    public StringDesc parseArgumentAsStringDesc(FunctionCallDesc functionCall, int argument) {
        final var first = context.parse(functionCall.getArguments().get(argument));
        switch (first) {
            case String n -> {
                return stringDesc(n, functionCall.getArguments().get(argument).getSourceCodeQuote());
            }
            default -> throw execException(tree("The argument "
                    + argument
                    + " of "
                    + name
                    + "has to be a string, but "
                    + first.getClass().getName()
                    + " was given instead.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
    }

    public void failBecauseOfInvalidType(FunctionCallDesc functionCall, int argument, NameDesc actualType, String... allowedTypes) {
        final var allowedTypeList = Lists.list(allowedTypes).stream()
                .map(at -> "the " + at + " type")
                .reduce((a, b) -> a + " or " + b)
                .orElseThrow();
        throw execException(tree("The argument "
                + argument
                + " has to be a reference to "
                + allowedTypeList
                + ", but " + actualType.getValue() + " was given instead.")
                .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
    }

    public Query parseQuerySubject(FunctionCallDesc functionCall, Optional<Object> subject) {
        if (subject.isEmpty()) {
            throw execException(tree("The "
                    + name
                    + " function requires a Solution or Query as a subject, but no was given.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        if (!(subject.orElseThrow() instanceof Solution) && !(subject.orElseThrow() instanceof Query)) {
            throw execException(tree("The "
                    + name
                    + " function requires a Solution or Query as a subject, but "
                    + subject.orElseThrow().getClass().getName()
                    + " was given instead.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        final Query subjectVal;
        if (subject.orElseThrow() instanceof Solution solution) {
            subjectVal = query(solution.constraint());
        } else if (subject.orElseThrow() instanceof Query query) {
            subjectVal = query;
        } else {
            throw execException(tree("The function " + name + " requires a solution or query as a subject, but "
                    + subject.orElseThrow().getClass().getName() + " was given instead.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        return subjectVal;
    }

    public Attribute<? extends Object> parseAttribute(FunctionCallDesc functionCall, int argument) {
        final var a = functionCall.getArguments().get(argument);
        final var parsed = context.parse(a);
        switch (parsed) {
            case Attribute<? extends Object> attribute -> {
                return attribute;
            }
            default -> throw execException(tree("The argument "
                    + argument
                    + " of "
                    + name
                    + "has to be an attribute, but a "
                    + parsed.getClass().getName()
                    + " was given instead.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
    }

    @Override
    public void close() {
        context.addRecord(this);
    }
}
