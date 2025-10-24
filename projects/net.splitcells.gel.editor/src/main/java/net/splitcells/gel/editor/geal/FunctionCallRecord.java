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
import lombok.val;
import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.NameDesc;
import net.splitcells.gel.editor.geal.lang.StringDesc;
import net.splitcells.gel.editor.meta.Type;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.CommonMarkUtils.joinDocuments;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.editor.geal.lang.NameDesc.nameDesc;
import static net.splitcells.gel.editor.geal.lang.StringDesc.stringDesc;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;

/**
 * Extracts function call data from {@link FunctionCallDesc},
 * while recording all types of function calls, in order to generate a complete function call documentation.
 * Through the combination of value extraction and documentation recording in single methods,
 * the parsing and its error messages are more standardized.
 */
@Accessors(chain = true)
public class FunctionCallRecord implements Closeable {
    public static FunctionCallRecord functionCallRecord(Optional<Object> argSubject, FunctionCallDesc argFunctionCall, Editor argContext, String argName, int argVariation) {
        return new FunctionCallRecord(argSubject, argFunctionCall, argContext, argName, argVariation, false);
    }

    public static FunctionCallRecord functionCallRecord(Optional<Object> argSubject, FunctionCallDesc argFunctionCall, Editor argContext, String argName, int argVariation, boolean argIsRecording) {
        return new FunctionCallRecord(argSubject, argFunctionCall, argContext, argName, argVariation, argIsRecording);
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
    private final FunctionCallDesc functionCall;
    private final Optional<Object> subject;
    boolean isRecording;
    @Getter Optional<Boolean> subjectAbsent = Optional.empty();
    @Getter Map<Integer, Class<?>> argumentTypes = map();

    private FunctionCallRecord(Optional<Object> argSubject, FunctionCallDesc argFunctionCall, Editor argContext, String argName, int argVariation, boolean argIsRecording) {
        name = argName;
        variation = argVariation;
        context = argContext;
        functionCall = argFunctionCall;
        subject = argSubject;
        isRecording = argIsRecording;
    }

    public FunctionCallRecord addDescription(String addition) {
        joinDocuments(description, addition);
        return this;
    }

    public void requireArgumentCount(int requiredArgumentCount) {
        if (isRecording) {
            return;
        }
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

    public void requireArgumentMinimalCount(int requiredMinimum) {
        if (isRecording) {
            return;
        }
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

    public NameDesc parseArgumentAsType(int argument, String... validValues) {
        if (isRecording) {
            return null;
        }
        val argumentAsType = parseArgumentAsType(argument);
        val validValueList = listWithValuesOf(validValues);
        val anyMatch = validValueList.stream().anyMatch(v -> v.equals(argumentAsType.getValue()));
        if (!anyMatch) {
            throw execException(tree("The "
                    + functionCall.getName().getValue()
                    + " function call's " + argument + " argument only supports the following values: " + validValueList));
        }
        return argumentAsType;
    }

    public NameDesc parseArgumentAsType(int argument) {
        if (isRecording) {
            return null;
        }
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

    public StringDesc parseArgumentAsStringDesc(int argument) {
        if (isRecording) {
            return stringDesc("", emptySourceCodeQuote());
        }
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

    public void failBecauseOfInvalidType(int argument, NameDesc actualType, String... allowedTypes) {
        if (isRecording) {
            return;
        }
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

    public Query parseQuerySubject() {
        if (isRecording) {
            return null;
        }
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

    public <T> T parseSubject(Class<? extends T> type) {
        if (isRecording) {
            return null;
        }
        if (subject.isEmpty()) {
            throw execException(tree("The function " + name + " requires a "
                    + type.getName()
                    + " subject, but none was given instead.")
                    .withProperty("Affected function call", functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        if (type.isInstance(subject.orElseThrow())) {
            return (T) subject.orElseThrow();
        }
        throw execException(tree("The function " + name + " requires a "
                + type.getName()
                + " subject, but a "
                + subject.orElseThrow().getClass().getName()
                + " was given instead.")
                .withProperty("Affected function call", functionCall.getSourceCodeQuote().userReferenceTree()));
    }

    public List<Attribute<? extends Object>> parseAttributeArguments(int from) {
        if (isRecording) {
            return null;
        }
        return functionCall.getArguments().streamIndexes()
                .filter(i -> i >= from)
                .mapToObj(this::parseAttributeArgument).collect(toList());
    }

    public List<Attribute<? extends Object>> parseAttributeArguments() {
        if (isRecording) {
            return null;
        }
        return functionCall.getArguments().streamIndexes().mapToObj(this::parseAttributeArgument).collect(toList());
    }

    public Attribute<? extends Object> parseAttributeArgument(int argument) {
        if (isRecording) {
            return null;
        }
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

    public <T> Attribute<T> parseAttributeArgument(Class<? extends T> type, int argument) {
        if (isRecording) {
            return null;
        }
        final Attribute<?> distanceAttribute = parseAttributeArgument(argument);
        if (!type.isAssignableFrom(distanceAttribute.type())) {
            throw execException(tree("The argument "
                    + argument
                    + " of "
                    + name
                    + " has to be an "
                    + type.getName()
                    + " attribute, but a "
                    + distanceAttribute.type().getName()
                    + " is given instead.")
                    .withProperty("Affected function call", functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        return (Attribute<T>) distanceAttribute;
    }

    public <T> T parseArgument(Class<? extends T> type, int argument) {
        if (isRecording) {
            argumentTypes.put(argument, type);
            return null;
        }
        final var parsed = context.parse(functionCall.getArguments().get(argument));
        if (type.isInstance(parsed)) {
            return (T) parsed;
        }
        throw execException(tree("The argument "
                + argument
                + " of "
                + name
                + " has to be a "
                + type.getName()
                + ", but a "
                + parsed.getClass().getName()
                + " is given instead.")
                .withProperty("Affected function call", functionCall.getSourceCodeQuote().userReferenceTree()));
    }

    public void requireSubjectAbsence() {
        if (isRecording) {
            subjectAbsent = Optional.of(true);
            return;
        }
        if (subject.isPresent()) {
            throw execException(tree("The "
                    + name
                    + " function does not support subjects, but one was given.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
    }

    @Override
    public void close() {
        context.addRecord(this);
    }

    public Flow<Integer> argumentIndexes() {
        return this.getArgumentTypes().keySet2().stream().sorted();
    }
}
