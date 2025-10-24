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
package net.splitcells.gel.editor.geal.runners;

import lombok.val;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.FunctionCallRecord;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.tree.TreeI.tree;

public class FunctionCallRunnerParser<T> {
    public static <R> FunctionCallRunnerParser<R> functionCallRunnerParser(String argName
            , int argVariation
            , Function<FunctionCallRecord, R> argParser) {
        return new FunctionCallRunnerParser<>(argName, argVariation, argParser);
    }

    private final Function<FunctionCallRecord, T> parser;
    private final String name;
    /**
     * Functions with the same name, but different arguments have distinct variation ids.
     */
    private final int variation;

    private FunctionCallRunnerParser(String argName, int argVariation, Function<FunctionCallRecord, T> argParser) {
        parser = argParser;
        name = argName;
        variation = argVariation;
    }

    public T parse(Optional<Object> subject, Editor context, FunctionCallDesc functionCall) {
        try (val fcr = context.functionCallRecord(subject, functionCall, name, variation)) {
            return parser.apply(fcr);
        }
    }

    public Tree document(Editor context) {
        try (val fcr = context.functionCallRecord(null, null, name, variation, true)) {
            parser.apply(fcr);
            val functionCallDoc = tree("chapter", SEW);
            functionCallDoc.withChild(tree("title", SEW).withText(fcr.getName() + " " + fcr.getVariation()));
            val list = tree("list", SEW).withParent(functionCallDoc);
            if (fcr.getRequireSubjectAbsent()) {
                list.withChild(tree("item", SEW).withText("No Subject"));
            } else if (fcr.getRequiredSubjectTypes().size() == 1) {
                list.withChild(tree("item", SEW).withText("The subject has to be of type "
                        + fcr.getRequiredSubjectTypes().get(0).getSimpleName() + "."));
            } else if (fcr.getRequiredSubjectTypes().hasElements()) {
                val typesDescription = fcr.getRequiredSubjectTypes()
                        .stream()
                        .map(Class::getSimpleName)
                        .reduce((a, b) -> a + ", " + b)
                        .orElseThrow();
                list.withChild(tree("item", SEW).withText("The subject has to be one of the following types: " + typesDescription));
            }
            if (fcr.getRequiredArgumentCount() != -1) {
                list.withChild(tree("item", SEW).withText("Exactly " + fcr.getRequiredArgumentCount() + " arguments have to be present."));
            }
            if (fcr.getRequiredMinimalArgumentCount() != -1) {
                list.withChild(tree("item", SEW).withText("At least " + fcr.getRequiredMinimalArgumentCount() + " arguments have to be present."));
            }
            if (fcr.getOnlyAttributesAsArgument()) {
                tree("list", SEW).withParent(functionCallDoc).withText("Only attribute references are allowed as arguments.");
            } else {
                val arguments = tree("list", SEW).withParent(functionCallDoc);
                fcr.argumentIndexes().forEach(i -> {
                    final String validValues;
                    final String validValuesDesc;
                    if (fcr.getArgumentsValidNames().hasKey(i)) {
                        validValues = fcr.getArgumentsValidNames().get(i).stream().reduce((a, b) -> a + ", " + b).orElse("");
                    } else {
                        validValues = "";
                    }
                    if (!validValues.isEmpty()) {
                        validValuesDesc = " : valid values = " + validValues;
                    } else {
                        validValuesDesc = "";
                    }
                    arguments.withChild(tree("item", SEW).withText(i
                            + ": type = "
                            + fcr.getArgumentTypes().get(i).getSimpleName()
                            + validValuesDesc));
                });
                if (fcr.getOnlyAttributesAsArgumentsFrom() != -1) {
                    arguments.withChild(tree("item", SEW).withText("Starting with index " + fcr.getOnlyAttributesAsArgumentsFrom() + " a arbitrary number of only attribute arguments are accepted."));
                }
            }
            return functionCallDoc;
        }
    }
}
