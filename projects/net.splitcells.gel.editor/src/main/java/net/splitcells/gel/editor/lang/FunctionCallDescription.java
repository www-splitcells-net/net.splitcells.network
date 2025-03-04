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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

public final class FunctionCallDescription implements ArgumentDescription {

    public static FunctionCallDescription functionCallDescription(String functionName, ArgumentDescription... arguments) {
        return functionCallDescription(functionName, listWithValuesOf(arguments));
    }

    public static FunctionCallDescription functionCallDescription(String functionName, List<ArgumentDescription> arguments) {
        return new FunctionCallDescription(functionName, arguments);
    }

    private final String functionName;
    private final List<ArgumentDescription> arguments;

    private FunctionCallDescription(String argFunctionName, List<ArgumentDescription> argArguments) {
        arguments = argArguments;
        functionName = argFunctionName;
    }

    public List<ArgumentDescription> arguments() {
        return arguments;
    }

    public String functionName() {
        return functionName;
    }
}
