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
package net.splitcells.gel.editor.lang;

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

public final class FunctionCallDescription implements ArgumentDescription, SourceCodeQuotation {

    public static FunctionCallDescription functionCallDescription(String functionName, SourceCodeQuote sourceCodeQuote, ArgumentDescription... arguments) {
        return functionCallDescription(functionName, listWithValuesOf(arguments), sourceCodeQuote);
    }

    public static FunctionCallDescription functionCallDescription(String functionName, List<ArgumentDescription> arguments, SourceCodeQuote sourceCodeQuote) {
        return new FunctionCallDescription(functionName, arguments, sourceCodeQuote);
    }

    private final String functionName;
    private final List<ArgumentDescription> arguments;
    private final SourceCodeQuote sourceCodeQuote;

    private FunctionCallDescription(String argFunctionName, List<ArgumentDescription> argArguments, SourceCodeQuote argSourceCodeQuote) {
        arguments = argArguments;
        functionName = argFunctionName;
        sourceCodeQuote = argSourceCodeQuote;
    }

    public List<ArgumentDescription> arguments() {
        return arguments;
    }

    public String functionName() {
        return functionName;
    }

    @Override
    public String toString() {
        return functionName + arguments + " sourceCodeQuote: " + sourceCodeQuote;
    }

    @Override
    public SourceCodeQuote sourceCodeQuote() {
        return sourceCodeQuote;
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof FunctionCallDescription other) {
            return functionName.equals(other.functionName()) && arguments.equals(other.arguments()) && sourceCodeQuote.equals(other.sourceCodeQuote());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Thing.hashCode(functionName, arguments, sourceCodeQuote);
    }
}
