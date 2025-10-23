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

public class FunctionCallRunnerParser<T> {
    public static <R> FunctionCallRunnerParser<R> functionCallRunnerParser(Function<FunctionCallRecord, R> argParser
            , String argName
            , int argVariation) {
        return new FunctionCallRunnerParser<>(argParser, argName, argVariation);
    }

    private final Function<FunctionCallRecord, T> parser;
    private final String name;
    private final int variation;

    private FunctionCallRunnerParser(Function<FunctionCallRecord, T> argParser, String argName, int argVariation) {
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
        try (val fcr = context.functionCallRecord(null, null, name, variation)) {
            return null;
        }
    }
}
