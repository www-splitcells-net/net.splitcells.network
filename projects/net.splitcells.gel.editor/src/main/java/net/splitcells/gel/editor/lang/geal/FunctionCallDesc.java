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
package net.splitcells.gel.editor.lang.geal;

import lombok.Getter;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.editor.lang.SourceCodeQuote;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;

public final class FunctionCallDesc implements ExpressionDesc {
    public static FunctionCallDesc functionCallDesc(NameDesc name) {
        return functionCallDesc(name, list(), emptySourceCodeQuote());
    }

    public static FunctionCallDesc functionCallDesc(NameDesc name, List<ExpressionDesc> arguments) {
        return functionCallDesc(name, arguments, emptySourceCodeQuote());
    }

    public static FunctionCallDesc functionCallDesc(NameDesc name, List<ExpressionDesc> arguments, SourceCodeQuote sourceCodeQuote) {
        return new FunctionCallDesc(name, arguments, sourceCodeQuote);
    }

    @Getter private final NameDesc name;
    @Getter private final List<ExpressionDesc> arguments;
    @Getter private final SourceCodeQuote sourceCodeQuote;

    private FunctionCallDesc(NameDesc argName, List<ExpressionDesc> argArguments, SourceCodeQuote argSourceCodeQuote) {
        name = argName;
        arguments = argArguments;
        sourceCodeQuote = argSourceCodeQuote;
    }
}
